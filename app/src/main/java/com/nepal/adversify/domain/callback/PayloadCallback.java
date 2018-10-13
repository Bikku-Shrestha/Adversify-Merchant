package com.nepal.adversify.domain.callback;

import com.generic.appbase.domain.dto.ActionEvent;
import com.google.android.gms.nearby.connection.Payload;

public interface PayloadCallback {

    void onPayloadSent(long payloadId, String endpointId);

    void onSendPayload(String endpointId, Payload payload);

    void onClientInfoReceived(String endpointId, long id, Object obj);

    void onClientRequestReceived(String endpointId, long id, ActionEvent actionEvent);

}
