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
import com.nepal.adversify.domain.binder.ConnectedClientBinder;
import com.nepal.adversify.domain.model.ClientModel;
import com.nepal.adversify.manager.AdvertiseManager;
import com.nepal.adversify.viewmodel.HomeViewModel;
import com.nepal.adversify.viewmodel.HomeViewModelFactory;
import com.nepal.adversify.viewmodel.MerchantViewModel;
import com.nepal.adversify.viewmodel.MerchantViewModelFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment implements
        OnItemClickCallback<ClientModel> {

    @Inject
    HomeViewModelFactory mHomeViewModelFactory;
    @Inject
    MerchantViewModelFactory merchantViewModelFactory;
    @Inject
    ConnectedClientBinder mBinder;
    @Inject
    ConnectionsClient mConnectionsClient;

    private AdvertiseManager mAdvertiseManager;
    private HomeViewModel mHomeViewModel;
    private MerchantViewModel mMerchantViewModel;
    private SimpleRecyclerAdapter<ClientModel, ConnectedClientBinder> mConnectedAdapter;

    private FloatingActionButton mAdvertiseFloatingActionButton;

    private boolean isMerchantInfoAvailable = false;
    private boolean isPermissionGranted = false;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMerchantViewModel = ViewModelProviders.of(getActivity(), merchantViewModelFactory).get(MerchantViewModel.class);
        mHomeViewModel = ViewModelProviders.of(getActivity(), mHomeViewModelFactory).get(HomeViewModel.class);
        mAdvertiseManager = new AdvertiseManager(getContext(), mHomeViewModel, mConnectionsClient,
                mMerchantViewModel);
        observeData();
        loadData();
    }

    @Override
    protected View onViewReady(View view, Bundle savedInstanceState) {
        Timber.d("onViewReady");

        Toolbar mToolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        NavigationUI.setupWithNavController(mToolbar, Navigation.findNavController(view));

        mAdvertiseFloatingActionButton = view.findViewById(R.id.discoverButton);
        FloatingActionButton mManageFloatingActionButton = view.findViewById(R.id.manageButton);
        mManageFloatingActionButton.setOnClickListener((v) -> {
            Timber.d("Manage button clicked");
            NavOptions navOptions = new NavOptions.Builder()
                    .setEnterAnim(R.anim.enter_from_right)
                    .setExitAnim(R.anim.exit_to_left)
                    .setPopEnterAnim(R.anim.enter_from_left)
                    .setPopExitAnim(R.anim.exit_to_right)
                    .build();

            Navigation.findNavController(v).navigate(R.id.manageFragment, null, navOptions);
        });

        mAdvertiseFloatingActionButton.setOnClickListener((v) -> {
            Timber.d("Broadcast button clicked");
            if (isPermissionGranted && isMerchantInfoAvailable) {
                mAdvertiseFloatingActionButton.setEnabled(false);
                mAdvertiseManager.startAdvertising();
            } else {
                showToast("First update information before advertising.");
            }
        });

        RecyclerView mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mConnectedAdapter = new SimpleRecyclerAdapter<>(mBinder);
        mRecyclerView.setAdapter(mConnectedAdapter);

        return view;
    }

    private void observeData() {
        Timber.d("Observing livedata");

        mMerchantViewModel.getMerchantLiveData().observe(this, data -> {
            if (data == null) {
                isMerchantInfoAvailable = false;
            } else {
                isMerchantInfoAvailable = true;
                Timber.d("Merchant title: %s", data.title);
            }
        });

        mHomeViewModel.getConnectedClient().observe(this, data -> {
            Timber.d("Total connected clients: %d", data.size());
            mConnectedAdapter.setData(new ArrayList<>(data.values()));
        });

        mHomeViewModel.getStatusLiveData().observe(this, data -> {
            Timber.d(data);
            showToast(data);
        });
    }

    private void loadData() {
        Timber.d("loadData");
        mMerchantViewModel.loadMerchantData();
    }

    private void requestPermission() {
        Timber.d("Requesting permissions");
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Timber.d("onPermissionGranted");
                            isPermissionGranted = true;
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
            mMerchantViewModel.updateLocation(location.getLatitude(), location.getLongitude());
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
    public void onStart() {
        super.onStart();
        Timber.d("onStart");
        requestPermission();
    }

    @Override
    public void onItemClick(View v, int position, ClientModel clientModel) {

    }

    @Override
    public void onDestroy() {
        mAdvertiseManager.stopAdvertising();
        super.onDestroy();
    }
}
