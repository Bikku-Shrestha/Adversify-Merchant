package com.nepal.adversify.ui.home;


import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.generic.appbase.domain.dto.UserInfo;
import com.generic.appbase.ui.BaseFragment;
import com.generic.appbase.utils.CommonUtils;
import com.generic.appbase.utils.SerializationUtils;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.nepal.adversify.R;
import com.nepal.adversify.data.PreferenceHelper;
import com.nepal.adversify.domain.callback.ConnectionCallback;
import com.nepal.adversify.domain.callback.PayloadCallback;
import com.nepal.adversify.domain.handler.ConnectionHandler;
import com.nepal.adversify.domain.handler.PayloadHandler;
import com.nepal.adversify.viewmodel.HomeViewModel;
import com.nepal.adversify.viewmodel.HomeViewModelFactory;

import java.io.IOException;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment implements ConnectionCallback,
        PayloadCallback {

    private static final Strategy STRATEGY = Strategy.P2P_STAR;

    @Inject
    HomeViewModelFactory mHomeViewModelFactory;
    @Inject
    PayloadHandler mPayloadHandler;
    @Inject
    ConnectionHandler mConnectionHandler;
    @Inject
    ConnectionsClient mConnectionsClient;
    @Inject
    PreferenceHelper mPreferenceHelper;

    private HomeViewModel mHomeViewModel;

    private TextView mStatusTextView;
    private Toolbar mToolbar;
    private FloatingActionButton mAdvertiseFloatingActionButton;

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

        mToolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        NavigationUI.setupWithNavController(mToolbar, Navigation.findNavController(view));
        mToolbar.setTitle(R.string.app_name);

        mStatusTextView = view.findViewById(R.id.connectedStatus);
        mAdvertiseFloatingActionButton = view.findViewById(R.id.discoverButton);

        mStatusTextView.setText(getString(R.string.value_broadcast_dummy_info));

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

        mHomeViewModel.getStatusLiveData().observe(this, data -> {
            Timber.d(data);
            mStatusTextView.setText(data);
        });
    }

    private void loadData() {
        Timber.d("loadData");
        mHomeViewModel.loadUserInfo();
        mHomeViewModel.loadMerchantInfo();
    }

    private void permissionGranted() {
        mAdvertiseFloatingActionButton.setOnClickListener((v) -> {
            Timber.d("Broadcast button clicked");
            mAdvertiseFloatingActionButton.setEnabled(false);
            startAdvertising();
        });
    }

    private void startAdvertising() {
        // We were unable to start advertising.
        UserInfo userInfo = mHomeViewModel.getUserInfo();
        mConnectionsClient.startAdvertising(
                userInfo.username,
                com.generic.appbase.connection.ConnectionInfo.NEARBY_CONNECTION_SERVICE_ID,
                mConnectionHandler,
                new AdvertisingOptions.Builder()
                        .setStrategy(STRATEGY)
                        .build()
        )
                .addOnSuccessListener(
                        unusedResult -> {
                            // We're advertising!
                            Timber.d("Broadcasted successfully");
                            mHomeViewModel.setStatusMessage(
                                    String.format(
                                            getString(R.string.value_broadcasted_merchant_info),
                                            mHomeViewModel.getMerchantInfo().title
                                    )
                            );
                        })
                .addOnFailureListener(
                        e -> {
                            Timber.d("Error broadcasting information");
                            mHomeViewModel.setStatusMessage(getString(R.string.value_error_broadcasting));
                        });
    }

    @Override
    public void acceptConnection(@NonNull String endpointId, @NonNull ConnectionInfo connectionInfo) {
        Timber.d("acceptConnection");
        mConnectionsClient.acceptConnection(endpointId, mPayloadHandler);
    }

    @Override
    public void onClientConnected(String endpointId, String endpointName) {
        Timber.d("onClientConnected");
        mHomeViewModel.addConnectedClient(endpointId, endpointName);
    }

    @Override
    public void onClientConnectionRejected(String endpointId, String endpointName) {
        Timber.d("onClientConnectionRejected");
    }

    @Override
    public void onClientConnectionError(String endpointId, String endpointName) {
        Timber.d("onClientConnectionError");
    }

    @Override
    public void onClientDisconnected(String endpointId) {
        Timber.d("onClientDisconnected");
        mHomeViewModel.removeConnectedClient(endpointId);
    }

    @Override
    public int extractCategoryId(String endpointName) {
        Timber.d("extractCategoryId");
        String[] split = endpointName.split(":");
        return Integer.parseInt(split[0]);
    }

    @Override
    public void rejectConnection(String endpointId, ConnectionInfo connectionInfo) {
        Timber.d("rejectConnection");
        mConnectionsClient.rejectConnection(endpointId);
    }

    @Override
    public boolean doesMerchantCategoryMatch(int catId) {
        Timber.d("doesMerchantCategoryMatch");
        int merchantCategoryId = mPreferenceHelper.getMerchantCategoryId();
        return catId == merchantCategoryId;
    }


    @Override
    public void sendInitialPayload(String endpointId) {
        Timber.d("sendInitialPayload");
        try {
            mPayloadHandler.sendPayload(endpointId, Payload.fromBytes(
                    SerializationUtils.serialize(mHomeViewModel.getInitialPayload())));
        } catch (IOException e) {
            Timber.e(e);
        }

    }

    @Override
    public void sendFullInfoPayload(String endpointId) {
        Timber.d("sendFullInfoPayload");
        try {
            mPayloadHandler.sendPayload(endpointId, Payload.fromBytes(
                    SerializationUtils.serialize(mHomeViewModel.getMerchantInfo())));
        } catch (IOException e) {
            Timber.e(e);
        }
    }

    @Override
    public void onClientPayloadSent(String endpointId) {
        Timber.d("onClientPayloadSent");
    }

    @Override
    public void onSendPayload(String endpointId, Payload payload) {
        mConnectionsClient.sendPayload(endpointId, payload);
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
        CommonUtils.setBluetooth(false);
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.d("onStart");
        requestPermission();
    }
}
