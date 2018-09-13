package com.nepal.adversify.ui.home;


import android.Manifest;
import android.os.Bundle;
import android.view.View;

import com.generic.appbase.ui.BaseFragment;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.nepal.adversify.R;

import androidx.fragment.app.Fragment;
import ch.uepaa.p2pkit.P2PKit;
import ch.uepaa.p2pkit.discovery.DiscoveryInfoTooLongException;
import ch.uepaa.p2pkit.discovery.DiscoveryListener;
import ch.uepaa.p2pkit.discovery.DiscoveryPowerMode;
import ch.uepaa.p2pkit.discovery.Peer;
import ch.uepaa.p2pkit.messaging.MessageDeliveryStatusHandler;
import ch.uepaa.p2pkit.messaging.MessageListener;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {


    private final MessageListener mMessageListener = (message, peer) -> {
        Timber.d("onMessageReceived from peer: %s with message: %s", peer.getPeerId().toString(), new String(message));
        showToast(new String(message));
    };
    private final DiscoveryListener mDiscoveryListener = new DiscoveryListener() {
        @Override
        public void onStateChanged(final int state) {
            Timber.d("State changed: %d", state);
        }

        @Override
        public void onPeerDiscovered(final Peer peer) {
            Timber.d("Peer discovered: %s with info: %s", peer.getPeerId().toString(),
                    new String(peer.getDiscoveryInfo()));
        }

        @Override
        public void onPeerLost(final Peer peer) {
            Timber.d("Peer lost: %s", peer.getPeerId().toString());
        }

        @Override
        public void onPeerUpdatedDiscoveryInfo(Peer peer) {
            Timber.d("Peer updated: %s  with new info: %s", peer.getPeerId().toString(),
                    new String(peer.getDiscoveryInfo()));
        }

        @Override
        public void onProximityStrengthChanged(Peer peer) {
            Timber.d("Peer %s changed proximity strength: %d", peer.getPeerId().toString(),
                    peer.getProximityStrength());
        }
    };

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    protected View onViewReady(View view, Bundle savedInstanceState) {
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

        return view;
    }

    private void permissionGranted() {
        getView().findViewById(R.id.discoverButton).setOnClickListener((v) -> {
            Timber.d("Discover button clicked");
            try {
                P2PKit.startDiscovery("Programmer Sujan".getBytes(), DiscoveryPowerMode.HIGH_PERFORMANCE, null);
                P2PKit.addMessageListener(mMessageListener);
            } catch (DiscoveryInfoTooLongException e) {
                Timber.e(e);
            }
        });
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_home;
    }

    private void sendMessageToPeer(Peer peer, byte[] message) {
        P2PKit.sendMessageToNearbyPeer(message, peer, new MessageDeliveryStatusHandler() {
            @Override
            public void onMessageSendSucceeded() {
                Timber.d("Message sent to peer: %s", peer.getPeerId().toString());
            }

            @Override
            public void onMessageSendFailed(int errorCode) {
                Timber.d("Failed to send the message with error code: %d", errorCode);
            }
        });
    }

    @Override
    public void onPause() {
        P2PKit.removeMessageListener(mMessageListener);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
