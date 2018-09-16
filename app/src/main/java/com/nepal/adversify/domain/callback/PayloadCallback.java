package com.nepal.adversify.domain.callback;

import com.google.android.gms.nearby.connection.Payload;

public interface PayloadCallback {

    void sendInitialPayload(String endpointId);

    void sendFullInfoPayload(String endpointId);

    void onClientPayloadSent(String endpointId);

    void onSendPayload(String endpointId, Payload payload);
}
