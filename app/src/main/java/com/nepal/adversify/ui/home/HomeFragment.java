package com.nepal.adversify.ui.home;


import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.generic.appbase.ui.BaseFragment;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.ConnectionsStatusCodes;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.nearby.connection.Strategy;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.nepal.adversify.R;
import com.nepal.adversify.data.ActionEvent;
import com.nepal.adversify.data.UserInfo;
import com.nepal.adversify.utils.SerializationUtils;
import com.nepal.adversify.viewmodel.HomeViewModel;
import com.nepal.adversify.viewmodel.HomeViewModelFactory;

import java.io.IOException;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import timber.log.Timber;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {

    private static final Strategy STRATEGY = Strategy.P2P_STAR;
    @Inject
    HomeViewModelFactory mHomeViewModelFactory;
    private ConnectionsClient mConnectionsClient;
    private TextView mStatusTextView;
    private HomeViewModel mHomeViewModel;
    private final PayloadCallback mPayloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String endpointId, @NonNull Payload payload) {
            Timber.d("onPayloadReceived");
            ActionEvent actionEvent = ActionEvent.valueOf(new String(payload.asBytes(), UTF_8));
            handleAction(endpointId, actionEvent);
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String endpointId, @NonNull PayloadTransferUpdate payloadTransferUpdate) {
            Timber.d("onPayloadTransferUpdate");
        }
    };
    private final ConnectionLifecycleCallback mConnectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
            Timber.d("onConnectionInitiated");
            Timber.d("Accepting connection with endpoint: %s", endpointId);

            mConnectionsClient.acceptConnection(endpointId, mPayloadCallback);
        }

        @Override
        public void onConnectionResult(@NonNull String endpointId, @NonNull ConnectionResolution result) {
            Timber.d("onConnectionResult");
            switch (result.getStatus().getStatusCode()) {
                case ConnectionsStatusCodes.STATUS_OK:
                    // We're connected! Can now start sending and receiving data.
                    Timber.d("Connection successful");
                    addConnectedClient(endpointId);
                    sendInitialPayload(endpointId);
                    break;
                case ConnectionsStatusCodes.STATUS_CONNECTION_REJECTED:
                    // The connection was rejected by one or both sides.
                    break;
                case ConnectionsStatusCodes.STATUS_ERROR:
                    // The connection broke before it was able to be accepted.
                    Timber.d("Connection error");
                    break;
            }
        }

        @Override
        public void onDisconnected(@NonNull String endpointId) {
            Timber.d("onDisconnected");
            mHomeViewModel.removeConnectedClient(endpointId);
        }
    };
    private ActionEvent mActionEvent;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mHomeViewModel = ViewModelProviders.of(this, mHomeViewModelFactory).get(HomeViewModel.class);
        observeData();
        loadData();
    }

    @Override
    protected View onViewReady(View view, Bundle savedInstanceState) {
        Timber.d("onViewReady");

        mStatusTextView = view.findViewById(R.id.connectedStatus);
        mStatusTextView.setText(R.string.value_broadcasting);

        mConnectionsClient = Nearby.getConnectionsClient(getContext());

        return view;
    }

    private void observeData() {
        Timber.d("Observing livedata");
        mHomeViewModel.getUserLiveData().observe(this, data -> {
            Timber.d("Username: %s", data.username);
        });

        mHomeViewModel.getMerchantLiveData().observe(this, data -> {
            Timber.d("Merchant title: %s", data.title);
        });

        mHomeViewModel.getConnectedClient().observe(this, data -> {
            Timber.d("Total connected clients: %d", data.size());
            showToast("Total connected clients: " + data.size());
        });
    }

    private void loadData() {
        Timber.d("loadData");
        mHomeViewModel.loadUserInfo();
        mHomeViewModel.loadMerchantInfo();
    }

    private void permissionGranted() {
        getView().findViewById(R.id.discoverButton).setOnClickListener((v) -> {
            Timber.d("Broadcast button clicked");
            startAdvertising();
        });
    }

    private void startAdvertising() {
        // We were unable to start advertising.
        UserInfo userInfo = mHomeViewModel.getUserInfo();
        mConnectionsClient.startAdvertising(
                userInfo.username,
                userInfo.id,
                mConnectionLifecycleCallback,
                new AdvertisingOptions.Builder()
                        .setStrategy(STRATEGY)
                        .build()
        )
                .addOnSuccessListener(
                        unusedResult -> {
                            // We're advertising!
                            Timber.d("Broadcasted successfully");
                            mStatusTextView.setText(R.string.value_broadcasted_successfully);
                        })
                .addOnFailureListener(
                        e -> {
                            Timber.d("Error broadcasting information");
                            mStatusTextView.setText(R.string.value_error_broadcasting);
                        });
    }


    private void addConnectedClient(String endpointName) {
        Timber.d("addConnectedClient");
        mHomeViewModel.addConnectedClient(endpointName);
    }

    private void sendInitialPayload(String endpointId) {
        Timber.d("sendInitialPayload");
        try {
            mConnectionsClient.sendPayload(endpointId,
                    Payload.fromBytes(
                            SerializationUtils.serialize(mHomeViewModel.getInitialPayload())

                    )
            );
        } catch (IOException e) {
            Timber.e(e);
        }
    }

    private void sendFullPayload(String endpointId) {
        Timber.d("sendFullPayload");
        try {
            mConnectionsClient.sendPayload(endpointId,
                    Payload.fromBytes(
                            SerializationUtils.serialize(mHomeViewModel.getMerchantInfo())

                    )
            );
        } catch (IOException e) {
            Timber.e(e);
        }
    }

    private void handleAction(String endpointId, ActionEvent actionEvent) {
        Timber.d("handleAction: %s of client: %s", actionEvent, endpointId);
        switch (actionEvent) {
            case ACTION_FULL_INFO:
                sendFullPayload(endpointId);
                break;
            case ACTION_INITIAL_INFO:
                break;
        }
    }

    private void requestPermission() {
        Timber.d("Requesting permissions");
        Dexter.withActivity(getActivity())
                .withPermission(
                        Manifest.permission.ACCESS_COARSE_LOCATION
                )
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Timber.d("onPermissionGranted");
                        permissionGranted();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Timber.d("onPermissionDenied");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        Timber.d("onPermissionRationaleShouldBeShown");
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_home;
    }

    @Override
    public void onStop() {
        Timber.d("onStop");
        mConnectionsClient.stopAllEndpoints();
        mConnectionsClient.stopAdvertising();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.d("onStart");
        requestPermission();
    }
}
