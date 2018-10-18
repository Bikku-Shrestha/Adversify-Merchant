package com.nepal.adversify.manager;

import android.content.Context;
import android.os.Handler;
import android.os.ParcelFileDescriptor;

import com.generic.appbase.domain.dto.Category;
import com.generic.appbase.domain.dto.ClientInfo;
import com.generic.appbase.domain.dto.DetailMerchantInfo;
import com.generic.appbase.domain.dto.Location;
import com.generic.appbase.domain.dto.PayloadData;
import com.generic.appbase.domain.dto.PreviewMerchantInfo;
import com.generic.appbase.domain.dto.ReviewInfo;
import com.generic.appbase.utils.CommonUtils;
import com.generic.appbase.utils.SerializationUtils;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.Payload;
import com.nepal.adversify.R;
import com.nepal.adversify.domain.callback.ConnectionCallback;
import com.nepal.adversify.domain.callback.PayloadCallback;
import com.nepal.adversify.domain.handler.ConnectionHandler;
import com.nepal.adversify.domain.handler.PayloadHandler;
import com.nepal.adversify.domain.model.ClientModel;
import com.nepal.adversify.domain.model.MerchantModel;
import com.nepal.adversify.mapper.ReviewInfoToReviewModelMapper;
import com.nepal.adversify.viewmodel.HomeViewModel;
import com.nepal.adversify.viewmodel.MerchantViewModel;

import java.io.FileNotFoundException;
import java.util.Objects;

import androidx.annotation.NonNull;
import timber.log.Timber;

import static com.generic.appbase.connection.ConnectionInfo.STRATEGY;

public class AdvertiseManager implements ConnectionCallback, PayloadCallback {

    ConnectionsClient mConnectionsClient;

    ConnectionHandler mConnectionHandler;
    PayloadHandler mPayloadHandler;

    private Context mContext;
    private final HomeViewModel mHomeViewModel;
    private final MerchantViewModel mMerchantViewModel;
    private final ReviewInfoToReviewModelMapper reviewInfoToReviewModelMapper;

    public AdvertiseManager(final Context context, HomeViewModel mHomeViewModel,
                            ConnectionsClient mConnectionsClient,
                            MerchantViewModel mMerchantViewModel,
                            ReviewInfoToReviewModelMapper reviewInfoToReviewModelMapper) {
        mContext = context;
        this.mHomeViewModel = mHomeViewModel;
        this.mConnectionsClient = mConnectionsClient;
        this.mMerchantViewModel = mMerchantViewModel;
        this.reviewInfoToReviewModelMapper = reviewInfoToReviewModelMapper;

        mConnectionHandler = new ConnectionHandler(this);
        mPayloadHandler = new PayloadHandler(this);
    }

    public void startAdvertising() {
        // We were unable to start advertising.
        mConnectionsClient.startAdvertising(
                Objects.requireNonNull(mMerchantViewModel.getCombinedMerchantLiveData().getValue()).title,
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
                                            mMerchantViewModel.getCombinedMerchantLiveData().getValue().title
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
        Category category = Objects.requireNonNull(mMerchantViewModel.getCombinedMerchantLiveData().getValue()).category;
        return catId == category.ordinal();
    }

    public void sendInitialInfo(String endpointId) {
        Timber.d("sendInitialPayload");
        PreviewMerchantInfo previewMerchantData = mMerchantViewModel.getPreviewMerchantData();
        if (previewMerchantData.hasFile) {
            MerchantModel value = mMerchantViewModel.getCombinedMerchantLiveData().getValue();
            try {
                ParcelFileDescriptor pfd = mContext.getContentResolver().openFileDescriptor(value.image, "r");
                Payload payloadImage = Payload.fromFile(pfd);
                previewMerchantData.fileId = payloadImage.getId();

                mPayloadHandler.sendPayload(endpointId, Payload.fromBytes(
                        Objects.requireNonNull(SerializationUtils.serializeToByteArray(
                                previewMerchantData
                        ))
                ));
                mPayloadHandler.sendPayload(endpointId, payloadImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            mPayloadHandler.sendPayload(endpointId, Payload.fromBytes(
                    Objects.requireNonNull(SerializationUtils.serializeToByteArray(
                            previewMerchantData
                    ))
            ));
        }

    }

    public void sendFullInfo(String endpointId) {
        Timber.d("sendFullInfoPayload");
        DetailMerchantInfo detailMerchantInfo = mMerchantViewModel.getDetailMerchantInfo();
        if (detailMerchantInfo.hasFile) {
            MerchantModel value = mMerchantViewModel.getCombinedMerchantLiveData().getValue();
            try {
                ParcelFileDescriptor pfd = mContext.getContentResolver().openFileDescriptor(value.image, "r");
                Payload payloadImage = Payload.fromFile(pfd);
                detailMerchantInfo.fileId = payloadImage.getId();

                mPayloadHandler.sendPayload(endpointId, Payload.fromBytes(
                        Objects.requireNonNull(SerializationUtils.serializeToByteArray(
                                detailMerchantInfo
                        ))
                ));

                mPayloadHandler.sendPayload(endpointId, payloadImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            mPayloadHandler.sendPayload(endpointId,
                    Payload.fromBytes(
                            Objects.requireNonNull(SerializationUtils.serializeToByteArray(
                                    detailMerchantInfo
                            ))
                    )
            );
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
    public void onClientDataReceived(String endpointId, long id, Object obj) {
        Timber.d("onClientDataReceived");
        PayloadData payloadData = (PayloadData) obj;
        switch (payloadData.dataType) {
            case PayloadData.CLIENT_INFO:
                ClientModel clientModel = mapClientInfo((ClientInfo) payloadData);
                mHomeViewModel.addConnectedClient(endpointId, clientModel);
                break;
            case PayloadData.MERCHANT_DETAIL_INFO:
                sendFullInfo(endpointId);
                break;
            case PayloadData.MERCHANT_REVIEW_INFO:
                ReviewInfo reviewInfo = (ReviewInfo) payloadData;
                mMerchantViewModel.addReviewData(reviewInfoToReviewModelMapper.from(reviewInfo));
                break;
        }
    }

    private ClientModel mapClientInfo(ClientInfo payloadData) {
        MerchantModel value = mMerchantViewModel.getCombinedMerchantLiveData().getValue();
        ClientModel clientModel = new ClientModel();
        clientModel.id = payloadData.id;
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
