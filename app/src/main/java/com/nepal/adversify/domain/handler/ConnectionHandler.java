package com.nepal.adversify.domain.handler;

import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.nepal.adversify.domain.callback.ConnectionCallback;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.collection.SimpleArrayMap;
import timber.log.Timber;

public class ConnectionHandler extends ConnectionLifecycleCallback {

    private final SimpleArrayMap<String, ConnectionInfo> incomingConnection = new SimpleArrayMap<>();
    private ConnectionCallback connectionCallback;

    @Inject
    public ConnectionHandler(final ConnectionCallback connectionCallback) {
        this.connectionCallback = connectionCallback;
    }

    @Override
    public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
        Timber.d("onConnectionInitiated");
        int catId = connectionCallback.extractCategoryId(connectionInfo.getEndpointName());
        if (catId > 0) {
            if (connectionCallback.doesMerchantCategoryMatch(catId)) {
                connectionCallback.acceptConnection(endpointId, connectionInfo);
                incomingConnection.put(endpointId, connectionInfo);
                return;
            }
        }
        connectionCallback.rejectConnection(endpointId, connectionInfo);
    }

    @Override
    public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution result) {
        ConnectionInfo connectionInfo = incomingConnection.remove(endpointId);
        switch (result.getStatus().getStatusCode()) {
            case ConnectionsStatusCodes.STATUS_OK:
                // We're connected! Can now start sending and receiving data.
                connectionCallback.onClientConnected(endpointId, connectionInfo.getEndpointName());
                break;
            case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                // The connection was rejected by one or both sides.
                connectionCallback.onClientConnectionRejected(endpointId, connectionInfo.getEndpointName());
                break;
            case ConnectionsStatusCodes.STATUS_ERROR:
                // The connection broke before it was able to be accepted.
                connectionCallback.onClientConnectionError(endpointId, connectionInfo.getEndpointName());
                break;
        }
    }

    @Override
    public void onDisconnected(@NonNull String endpointId) {
        connectionCallback.onClientDisconnected(endpointId);
    }

}
