package io.scalac.warsjawa;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import io.scalac.warsjawa.utils.Utils;


public class MainActivity extends FragmentActivity {
    private static final String ARG_NFC_STATE = "nfcState";
    private NfcAdapter mNfcAdapter;
    private Utils.NfcState lastNfcState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (savedInstanceState == null) {
            lastNfcState = Utils.getNfcState(mNfcAdapter);
            setupFragment();
        } else {
            lastNfcState = Utils.NfcState.valueOf(savedInstanceState.getString(ARG_NFC_STATE), Utils.getNfcState(mNfcAdapter));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(ARG_NFC_STATE, lastNfcState.name());
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
        Utils.NfcState nfcState = Utils.getNfcState(mNfcAdapter);
        if (nfcState != lastNfcState) {
            lastNfcState = nfcState;
            setupFragment();
        }
    }

    public void setChildFragment(Fragment fragment, boolean addToBackStack) {
        Fragment fragmentContainer = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragmentContainer != null) {
            MainFragment mainFragment = (MainFragment) fragmentContainer;
            mainFragment.setChildFragment(fragment, addToBackStack);
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
