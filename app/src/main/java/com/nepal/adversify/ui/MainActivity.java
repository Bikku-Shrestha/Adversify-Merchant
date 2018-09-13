package com.nepal.adversify.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.generic.appbase.ui.BaseActivity;
import com.nepal.adversify.R;

import androidx.navigation.Navigation;
import ch.uepaa.p2pkit.AlreadyEnabledException;
import ch.uepaa.p2pkit.P2PKit;
import ch.uepaa.p2pkit.P2PKitStatusListener;
import ch.uepaa.p2pkit.StatusResult;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import timber.log.Timber;

public class MainActivity extends BaseActivity {

    private final P2PKitStatusListener mStatusListener = new P2PKitStatusListener() {
        @Override
        public void onEnabled() {
            // ready to start discovery
            Timber.d("onEnabled");
        }

        @Override
        public void onDisabled() {
            // p2pkit has been disabled
            Timber.d("onDisabled");
        }

        @Override
        public void onError(StatusResult statusResult) {
            // an error occured, handle statusResult
            Timber.d("onError");
        }

        @Override
        public void onException(Throwable throwable) {
            // an exception was thrown, reenable p2pkit
            Timber.d("onException");
        }
    };

    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);
        Timber.d("onViewReady");

        try {
            P2PKit.enable(this, getString(R.string.p2pkit_key), mStatusListener);
        } catch (AlreadyEnabledException e) {
            Timber.e(e);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

}
