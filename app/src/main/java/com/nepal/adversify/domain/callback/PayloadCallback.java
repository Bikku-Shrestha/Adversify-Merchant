package com.nepal.adversify.domain.callback;

import com.generic.appbase.domain.dto.PayloadData;
import com.google.android.gms.nearby.connection.Payload;

public interface PayloadCallback {

    void onPayloadSent(long payloadId, String endpointId);

    void onSendPayload(String endpointId, Payload payload);

    void onBytePayloadReceived(String endpointId, long id, PayloadData data);

    void onFilePayloadReceived(String endpointId, long id, PayloadData data);
}
