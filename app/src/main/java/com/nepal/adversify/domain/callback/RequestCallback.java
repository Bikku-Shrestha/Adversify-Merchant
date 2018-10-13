package com.nepal.adversify.domain.callback;

public interface RequestCallback {

    void requestProfileInfo(String endpointId);

    void sendInitialInfo(String endpointId);

    void sendFullInfo(String endpointId);
}
