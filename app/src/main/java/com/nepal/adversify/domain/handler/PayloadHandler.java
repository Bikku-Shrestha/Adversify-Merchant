package com.nepal.adversify.domain.handler;

import com.generic.appbase.domain.dto.ActionEvent;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.collection.SimpleArrayMap;
import timber.log.Timber;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PayloadHandler extends PayloadCallback {

    private final SimpleArrayMap<Long, Payload> incomingPayloads = new SimpleArrayMap<>();
    private final SimpleArrayMap<Long, Payload> outgoingPayloads = new SimpleArrayMap<>();
    private com.nepal.adversify.domain.callback.PayloadCallback payloadCallback;

    @Inject
    public PayloadHandler(final com.nepal.adversify.domain.callback.PayloadCallback payloadCallback) {
        this.payloadCallback = payloadCallback;
    }

    @Override
    public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
        Timber.d("onPayloadReceived");
        incomingPayloads.put(payload.getId(), payload);
    }

    @Override
    public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate update) {
        if (update.getStatus() == PayloadTransferUpdate.Status.SUCCESS) {
            long payloadId = update.getPayloadId();
            if (incomingPayloads.containsKey(payloadId)) {
                Payload payload = incomingPayloads.remove(payloadId);
                if (payload.getType() == Payload.Type.BYTES) {
                    ActionEvent actionEvent = ActionEvent.valueOf(new String(payload.asBytes(), UTF_8));
                    handleAction(endpointId, actionEvent);
                }
            } else if (outgoingPayloads.containsKey(payloadId)) {
                outgoingPayloads.remove(payloadId);
                payloadCallback.onClientPayloadSent(endpointId);
            }
        } else if (update.getStatus() == PayloadTransferUpdate.Status.FAILURE) {
            Timber.d("Failed to send payload");
        }
    }

    private void handleAction(String endpointId, ActionEvent actionEvent) {
        Timber.d("handleAction");
        switch (actionEvent) {
            case ACTION_INITIAL_INFO:
                payloadCallback.sendInitialPayload(endpointId);
                break;
            case ACTION_FULL_INFO:
                payloadCallback.sendFullInfoPayload(endpointId);
                break;
        }
    }

    public void sendPayload(String endpointId, Payload payload) {
        Timber.d("sendPayload");
        outgoingPayloads.put(payload.getId(), payload);
        payloadCallback.onSendPayload(endpointId, payload);
    }

}
