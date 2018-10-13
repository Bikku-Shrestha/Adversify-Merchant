package com.nepal.adversify.domain.handler;

import com.generic.appbase.domain.dto.ActionEvent;
import com.nepal.adversify.domain.callback.RequestCallback;

import javax.inject.Inject;

public class RequestHandler {

    private RequestCallback merchantActionCallback;

    @Inject
    public RequestHandler(final RequestCallback merchantActionCallback) {
        this.merchantActionCallback = merchantActionCallback;
    }

    public void handleAction(String endpointId, ActionEvent actionInitialInfo) {
        switch (actionInitialInfo) {
            case ACTION_CLIENT_PROFILE_INFO:
                merchantActionCallback.requestProfileInfo(endpointId);
                break;
            case ACTION_MERCHANT_FULL_INFO:
                merchantActionCallback.sendFullInfo(endpointId);
                break;
            case ACTION_MERCHANT_INITIAL_INFO:
                merchantActionCallback.sendInitialInfo(endpointId);
                break;
        }
    }
}
