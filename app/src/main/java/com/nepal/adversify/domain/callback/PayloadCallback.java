package com.nepal.adversify.domain.callback;

import com.google.android.gms.nearby.connection.Payload;

public interface PayloadCallback {

    void onPayloadSent(long payloadId, String endpointId);

    void onSendPayload(String endpointId, Payload payload);

    void onClientDataReceived(String endpointId, long id, Object obj);

}
