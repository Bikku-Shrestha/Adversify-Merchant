package com.nepal.adversify.domain.callback;

import com.google.android.gms.nearby.connection.ConnectionInfo;

import androidx.annotation.NonNull;

public interface ConnectionCallback {

    void acceptConnection(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo);

    void onClientConnected(String endpointId, String endpointName);

    void onClientConnectionRejected(String endpointId, String endpointName);

    void onClientConnectionError(String endpointId, String endpointName);

    void onClientDisconnected(String endpointId);

    int extractCategoryId(String endpointName);

    void rejectConnection(String endpointId, ConnectionInfo connectionInfo);

    boolean doesMerchantCategoryMatch(int catId);
}
