package io.scalac.warsjawa;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import io.scalac.warsjawa.fragment.BaseFragment;
import io.scalac.warsjawa.fragment.HandleNoNfcFragment;
import io.scalac.warsjawa.fragment.MainFragment;
import io.scalac.warsjawa.utils.Utils;

public class MainActivity extends FragmentActivity {
    private NfcAdapter mNfcAdapter;
    private Utils.NfcState lastNfcState;
    private Runnable checkNfcRunnable = new Runnable() {
        @Override
        public void run() {
            Utils.NfcState nfcState = Utils.getNfcState(mNfcAdapter);
            if (nfcState != lastNfcState) {
                lastNfcState = nfcState;
                setupFragment();
            }
        }
    };
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        handler = new Handler();
        if (savedInstanceState == null) {
            lastNfcState = Utils.getNfcState(mNfcAdapter);
            setupFragment();
        } else {
            lastNfcState = Utils.NfcState.valueOf(savedInstanceState.getString(Constants.ARG_NFC_STATE), Utils.getNfcState(mNfcAdapter));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(Constants.ARG_NFC_STATE, lastNfcState.name());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        Fragment fragmentContainer = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragmentContainer != null) {
            BaseFragment baseFragment = (BaseFragment) fragmentContainer;
            if (!baseFragment.onBackPressed()) {
                super.onBackPressed();
            }
        } else
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkNfcRunnable.run();
        handler.postDelayed(checkNfcRunnable, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(checkNfcRunnable);
    }

    public void setChildFragment(Fragment fragment, boolean addToBackStack, boolean popCurrent) {
        Fragment fragmentContainer = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragmentContainer != null) {
            MainFragment mainFragment = (MainFragment) fragmentContainer;
            mainFragment.setChildFragment(fragment, addToBackStack, popCurrent);
        }
    }

    private void setupFragment() {
        Fragment fragment = (lastNfcState == Utils.NfcState.ENABLED) ? new MainFragment() : HandleNoNfcFragment.newInstance(lastNfcState);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment != null) {
            BaseFragment baseFragment = (BaseFragment) fragment;
            baseFragment.onNewIntent(intent);
        }
    }
}
