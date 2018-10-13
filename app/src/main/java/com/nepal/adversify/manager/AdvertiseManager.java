package com.nepal.adversify.manager;

import android.content.Context;
import android.os.Handler;

import com.generic.appbase.domain.dto.ActionEvent;
import com.generic.appbase.utils.SerializationUtils;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.Payload;
import com.nepal.adversify.R;
import com.nepal.adversify.data.ConnectedClient;
import com.nepal.adversify.data.PreferenceHelper;
import com.nepal.adversify.domain.callback.ConnectionCallback;
import com.nepal.adversify.domain.callback.PayloadCallback;
import com.nepal.adversify.domain.callback.RequestCallback;
import com.nepal.adversify.domain.handler.ConnectionHandler;
import com.nepal.adversify.domain.handler.PayloadHandler;
import com.nepal.adversify.domain.handler.RequestHandler;
import com.nepal.adversify.viewmodel.HomeViewModel;

import java.io.IOException;
import java.util.Objects;

import androidx.annotation.NonNull;
import timber.log.Timber;

import static com.generic.appbase.connection.ConnectionInfo.STRATEGY;

public class AdvertiseManager implements ConnectionCallback, PayloadCallback,
        RequestCallback {

    ConnectionsClient mConnectionsClient;
    PreferenceHelper mPreferenceHelper;

    ConnectionHandler mConnectionHandler;
    PayloadHandler mPayloadHandler;
    RequestHandler mRequestHandler;

    private Context mContext;
    private HomeViewModel mHomeViewModel;

    public AdvertiseManager(final Context context, HomeViewModel mHomeViewModel,
                            ConnectionsClient mConnectionsClient,
                            PreferenceHelper mPreferenceHelper) {
        mContext = context;
        this.mHomeViewModel = mHomeViewModel;
        this.mConnectionsClient = mConnectionsClient;
        this.mPreferenceHelper = mPreferenceHelper;

        mConnectionHandler = new ConnectionHandler(this);
        mPayloadHandler = new PayloadHandler(this);
        mRequestHandler = new RequestHandler(this);
    }

    public void startAdvertising() {
        // We were unable to start advertising.
        mConnectionsClient.startAdvertising(
                Objects.requireNonNull(mHomeViewModel.getMerchantLiveData().getValue()).title,
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
                            mHomeViewModel.setStatusMessage(
                                    String.format(
                                            mContext.getString(R.string.value_broadcasted_merchant_info),
                                            mHomeViewModel.getMerchantLiveData().getValue().title
                                    )
                            );
                        })
                .addOnFailureListener(
                        e -> {
                            Timber.d("Error broadcasting information");
                            mHomeViewModel.setStatusMessage(mContext.getString(R.string.value_error_broadcasting));
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
        mRequestHandler.handleAction(endpointId, ActionEvent.ACTION_CLIENT_PROFILE_INFO);
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
        restartAdvertising();
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

    @Override
    public void sendInitialInfo(String endpointId) {
        Timber.d("sendInitialPayload");
        try {
            mPayloadHandler.sendPayload(endpointId,
                    Payload.fromStream(
                            SerializationUtils.serialize(mHomeViewModel.getMerchantLiveData().getValue())
                    )
            );
        } catch (IOException e) {
            Timber.e(e);
        }

    }

    @Override
    public void sendFullInfo(String endpointId) {
        Timber.d("sendFullInfoPayload");
        try {
            mPayloadHandler.sendPayload(endpointId,
                    Payload.fromStream(
                            SerializationUtils.serialize(mHomeViewModel.getMerchantLiveData().getValue())
                    )
            );
        } catch (IOException e) {
            Timber.e(e);
        }
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
    public void onClientInfoReceived(String endpointId, long id, Object obj) {
        Timber.d("onClientInfoReceived");
        mHomeViewModel.addConnectedClient(endpointId, (ConnectedClient) obj);
    }

    @Override
    public void onClientRequestReceived(String endpointId, long id, ActionEvent actionEvent) {
        Timber.d("onClientRequestReceived");
        mRequestHandler.handleAction(endpointId, actionEvent);
    }

    @Override
    public void requestProfileInfo(String endpointId) {
        Timber.d("requestProfileInfo");
        mPayloadHandler.sendPayload(endpointId,
                Payload.fromBytes(ActionEvent.ACTION_CLIENT_PROFILE_INFO.toString().getBytes()));
    }

}
