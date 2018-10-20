package com.nepal.adversify.domain.handler;

import com.generic.appbase.domain.dto.PayloadData;
import com.generic.appbase.utils.SerializationUtils;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

import java.io.File;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.collection.SimpleArrayMap;
import timber.log.Timber;

public class PayloadHandler extends PayloadCallback {

    private final SimpleArrayMap<Long, Payload> incomingPayloads = new SimpleArrayMap<>();
    private final SimpleArrayMap<Long, Payload> outgoingPayloads = new SimpleArrayMap<>();
    private final SimpleArrayMap<Long, PayloadData> filePayloads = new SimpleArrayMap<>();
    private com.nepal.adversify.domain.callback.PayloadCallback payloadCallback;

    @Inject
    public PayloadHandler(final com.nepal.adversify.domain.callback.PayloadCallback payloadCallback) {
        this.payloadCallback = payloadCallback;
    }

    @Override
    public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
        Timber.d("onPayloadReceived");
        if (payload.getType() == Payload.Type.BYTES) {
            PayloadData payloadData = (PayloadData) SerializationUtils.deSerializeFromByteArray(payload.asBytes());
            if (payloadData.hasFile) {
                filePayloads.put(payloadData.fileId, payloadData);
            }
            payloadCallback.onBytePayloadReceived(endpointId, payload.getId(), payloadData);
        } else
            incomingPayloads.put(payload.getId(), payload);
    }

    @Override
    public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate update) {
        long payloadId = update.getPayloadId();
        if (incomingPayloads.containsKey(payloadId)) {
            Payload payload = incomingPayloads.get(payloadId);
            switch (payload.getType()) {
                case Payload.Type.FILE:
                    Timber.d("File payload update..");
                    if (update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {
                        incomingPayloads.remove(payloadId);
                        PayloadData filePayloadData = filePayloads.remove(payload.getId());
                        File payloadFile = payload.asFile().asJavaFile();
                        File renamedFile = new File(payloadFile.getParentFile(), filePayloadData.fileName);
                        payloadFile.renameTo(renamedFile);
                        filePayloadData.fileName = renamedFile.getAbsolutePath();
                        payloadCallback.onFilePayloadReceived(endpointId, payload.getId(), filePayloadData);
                    }
                    break;
                case Payload.Type.STREAM:
                    break;
            }
        } else if (outgoingPayloads.containsKey(payloadId)) {
            if (update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {
                outgoingPayloads.remove(payloadId);
                payloadCallback.onPayloadSent(payloadId, endpointId);
            }
        }
    }

    public void sendPayload(String endpointId, Payload payload) {
        Timber.d("sendPayload");
        outgoingPayloads.put(payload.getId(), payload);
        payloadCallback.onSendPayload(endpointId, payload);
    }

}
