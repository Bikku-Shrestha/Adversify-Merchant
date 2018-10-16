package com.nepal.adversify.manager;

import android.content.Context;
import android.os.Handler;

import com.generic.appbase.domain.dto.ClientInfo;
import com.generic.appbase.domain.dto.Location;
import com.generic.appbase.domain.dto.PayloadData;
import com.generic.appbase.utils.CommonUtils;
import com.generic.appbase.utils.SerializationUtils;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.Payload;
import com.nepal.adversify.R;
import com.nepal.adversify.data.PreferenceHelper;
import com.nepal.adversify.domain.callback.ConnectionCallback;
import com.nepal.adversify.domain.callback.PayloadCallback;
import com.nepal.adversify.domain.handler.ConnectionHandler;
import com.nepal.adversify.domain.handler.PayloadHandler;
import com.nepal.adversify.domain.model.ClientModel;
import com.nepal.adversify.domain.model.MerchantModel;
import com.nepal.adversify.viewmodel.HomeViewModel;
import com.nepal.adversify.viewmodel.MerchantViewModel;

import java.util.Objects;

import androidx.annotation.NonNull;
import timber.log.Timber;

import static com.generic.appbase.connection.ConnectionInfo.STRATEGY;

public class AdvertiseManager implements ConnectionCallback, PayloadCallback {

    ConnectionsClient mConnectionsClient;
    PreferenceHelper mPreferenceHelper;

    ConnectionHandler mConnectionHandler;
    PayloadHandler mPayloadHandler;

    private Context mContext;
    private final HomeViewModel mHomeViewModel;
    private final MerchantViewModel mMerchantViewModel;

    public AdvertiseManager(final Context context, HomeViewModel mHomeViewModel,
                            ConnectionsClient mConnectionsClient,
                            PreferenceHelper mPreferenceHelper,
                            MerchantViewModel mMerchantViewModel) {
        mContext = context;
        this.mHomeViewModel = mHomeViewModel;
        this.mConnectionsClient = mConnectionsClient;
        this.mPreferenceHelper = mPreferenceHelper;
        this.mMerchantViewModel = mMerchantViewModel;

        mConnectionHandler = new ConnectionHandler(this);
        mPayloadHandler = new PayloadHandler(this);
    }

    public void startAdvertising() {
        // We were unable to start advertising.
        mConnectionsClient.startAdvertising(
                Objects.requireNonNull(mMerchantViewModel.getMerchantLiveData().getValue()).title,
                com.generic.appbase.connection.ConnectionInfo.NEARBY_CONNECTION_SERVICE_ID,
                mConnectionHandler,
                new AdvertisingOptions.Builder()
                        .setStrategy(STRATEGY)
                        .build()
        )
                .addOnSuccessListener(
                        unusedResult -> {
                            // We're advertising!
                            Timber.d("Broadcasted successfully");
                            mHomeViewModel.getStatusLiveData().setValue(
                                    String.format(
                                            mContext.getString(R.string.value_broadcasted_merchant_info),
                                            mMerchantViewModel.getMerchantLiveData().getValue().title
                                    )
                            );
                        })
                .addOnFailureListener(
                        e -> {
                            Timber.d("Error broadcasting information");
                            mHomeViewModel.getStatusLiveData().setValue(mContext.getString(R.string.value_error_broadcasting));
                        });
    }

    private void restartAdvertising() {
        mConnectionsClient.stopAdvertising();
        new Handler().postDelayed(this::startAdvertising, 3000);
    }

    public void stopAdvertising() {
        mConnectionsClient.stopAllEndpoints();
        mConnectionsClient.stopAdvertising();
    }

    @Override
    public void acceptConnection(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
        Timber.d("acceptConnection");
        mConnectionsClient.acceptConnection(endpointId, mPayloadHandler);
    }

    @Override
    public void onClientConnected(String endpointId, String endpointName) {
        Timber.d("onClientConnected");
        sendInitialInfo(endpointId);
    }

    @Override
    public void onClientConnectionRejected(String endpointId, String endpointName) {
        Timber.d("onClientConnectionRejected");
    }

    @Override
    public void onClientConnectionError(String endpointId, String endpointName) {
        Timber.d("onClientConnectionError");
    }

    @Override
    public void onClientDisconnected(String endpointId) {
        Timber.d("onClientDisconnected");
        mHomeViewModel.removeConnectedClient(endpointId);
//        restartAdvertising();
    }

    @Override
    public int extractCategoryId(String endpointName) {
        Timber.d("extractCategoryId");
        String[] split = endpointName.split(":");
        return Integer.parseInt(split[0]);
    }

    @Override
    public void rejectConnection(String endpointId, ConnectionInfo connectionInfo) {
        Timber.d("rejectConnection");
        mConnectionsClient.rejectConnection(endpointId);
    }

    @Override
    public boolean doesMerchantCategoryMatch(int catId) {
        Timber.d("doesMerchantCategoryMatch");
        int merchantCategoryId = mPreferenceHelper.getMerchantCategoryId();
        return catId == merchantCategoryId;
    }

    public void sendInitialInfo(String endpointId) {
        Timber.d("sendInitialPayload");
        mPayloadHandler.sendPayload(endpointId,
                Payload.fromBytes(
                        Objects.requireNonNull(SerializationUtils.serializeToByteArray(
                                mMerchantViewModel.getPreviewMerchantData()
                        ))
                )
        );

    }

    public void sendFullInfo(String endpointId) {
        Timber.d("sendFullInfoPayload");
        mPayloadHandler.sendPayload(endpointId,
                Payload.fromBytes(
                        Objects.requireNonNull(SerializationUtils.serializeToByteArray(
                                mMerchantViewModel.getDetailMerchantInfo()
                        ))
                )
        );
    }

    @Override
    public void onPayloadSent(long payloadId, String endpointId) {
        Timber.d("onPayloadSent");
    }

    @Override
    public void onSendPayload(String endpointId, Payload payload) {
        mConnectionsClient.sendPayload(endpointId, payload);
    }

    @Override
    public void onClientDataReceived(String endpointId, long id, Object obj) {
        Timber.d("onClientDataReceived");
        PayloadData payloadData = (PayloadData) obj;
        if (payloadData.dataType == PayloadData.CLIENT_INFO_WITHOUT_IMAGE ||
                payloadData.dataType == PayloadData.CLIENT_INFO_WITH_IMAGE) {
            ClientModel clientModel = mapClientInfo((ClientInfo) payloadData);
            mHomeViewModel.addConnectedClient(endpointId, clientModel);
        } else if (payloadData.dataType == PayloadData.MERCHANT_DETAIL_INFO) {
            sendFullInfo(endpointId);
        }
    }

    private ClientModel mapClientInfo(ClientInfo payloadData) {
        MerchantModel value = mMerchantViewModel.getMerchantLiveData().getValue();
        ClientModel clientModel = new ClientModel();
        clientModel.name = payloadData.name;
        clientModel.avatar = payloadData.avatar;
        clientModel.location = payloadData.location;
        if (value != null) {
            Location from = value.location;
            Location to = payloadData.location;
            clientModel.distance = String.valueOf((int) CommonUtils.calculateDistance(from, to));
        }
        return clientModel;
    }

}
