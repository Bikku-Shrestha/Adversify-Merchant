package com.nepal.adversify.ui.home;


import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.ahamed.multiviewadapter.SimpleRecyclerAdapter;
import com.generic.appbase.domain.event.OnItemClickCallback;
import com.generic.appbase.manager.GPSLocationManager;
import com.generic.appbase.ui.BaseFragment;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.nepal.adversify.R;
import com.nepal.adversify.data.ConnectedClient;
import com.nepal.adversify.data.PreferenceHelper;
import com.nepal.adversify.domain.binder.ConnectedClientBinder;
import com.nepal.adversify.manager.AdvertiseManager;
import com.nepal.adversify.viewmodel.HomeViewModel;
import com.nepal.adversify.viewmodel.HomeViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment implements
        OnItemClickCallback<ConnectedClient> {

    @Inject
    HomeViewModelFactory mHomeViewModelFactory;
    @Inject
    ConnectedClientBinder mBinder;
    @Inject
    ConnectionsClient mConnectionsClient;
    @Inject
    PreferenceHelper mPreferenceHelper;

    private AdvertiseManager mAdvertiseManager;
    private HomeViewModel mHomeViewModel;
    private SimpleRecyclerAdapter<ConnectedClient, ConnectedClientBinder> mConnectedAdapter;

    private Toolbar mToolbar;
    private FloatingActionButton mAdvertiseFloatingActionButton;
    private RecyclerView mRecyclerView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mHomeViewModel = ViewModelProviders.of(getActivity(), mHomeViewModelFactory).get(HomeViewModel.class);
        mAdvertiseManager = new AdvertiseManager(getContext(), mHomeViewModel, mConnectionsClient,
                mPreferenceHelper);
        observeData();
        loadData();
    }

    @Override
    protected View onViewReady(View view, Bundle savedInstanceState) {
        Timber.d("onViewReady");

        mToolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        NavigationUI.setupWithNavController(mToolbar, Navigation.findNavController(view));

        mAdvertiseFloatingActionButton = view.findViewById(R.id.discoverButton);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mConnectedAdapter = new SimpleRecyclerAdapter<>(mBinder);
        mRecyclerView.setAdapter(mConnectedAdapter);

        return view;
    }

    private void observeData() {
        Timber.d("Observing livedata");

        mHomeViewModel.getMerchantLiveData().observe(getActivity(), data -> {
            Timber.d("Merchant title: %s", data.title);
        });

        mHomeViewModel.getConnectedClient().observe(getActivity(), data -> {
            Timber.d("Total connected clients: %d", data.size());
            mConnectedAdapter.setData(new ArrayList<>(data.values()));
        });

        mHomeViewModel.getLocationLiveData().observe(getActivity(), data -> {
            Timber.d("Location received: lat- %.0f, lon- %.0f", data.lat, data.lon);
        });

        mHomeViewModel.getStatusLiveData().observe(getActivity(), data -> {
            Timber.d(data);
            showToast(data);
        });
    }


    private void loadData() {
        Timber.d("loadData");
        mHomeViewModel.loadMerchantInfo();
    }

    private void registerForAdvertising() {
        mAdvertiseFloatingActionButton.setOnClickListener((v) -> {
            Timber.d("Broadcast button clicked");
            mAdvertiseFloatingActionButton.setEnabled(false);
            mAdvertiseManager.startAdvertising();
        });
    }

    private void requestPermission() {
        Timber.d("Requesting permissions");
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Timber.d("onPermissionGranted");
                            fetchLocation();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    private void fetchLocation() {
        Timber.d("fetchLocation");
        GPSLocationManager.getLocation(getContext(), location -> {
            mHomeViewModel.setLocation(location.getLatitude(),
                    location.getLongitude());
            registerForAdvertising();
        });
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_home;
    }

    @Override
    public void onStop() {
        Timber.d("onStop");
        mAdvertiseManager.stopAdvertising();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        Timber.d("onStart");
        requestPermission();
    }

    @Override
    public void onItemClick(View v, int position, ConnectedClient connectedClient) {

    }
}
