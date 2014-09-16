package io.scalac.warsjawa;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import io.scalac.warsjawa.utils.Utils;


public class HandleNoNfcFragment extends Fragment {

    private static final String ARG_NFC_STATE = "nfcState";

    @SuppressLint("InlinedApi")
    private static final String ACTION_NFC_SETTINGS = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) ? Settings.ACTION_NFC_SETTINGS : Settings.ACTION_WIRELESS_SETTINGS;

    private TextView nfcStatusTextView;
    private Button settingButton;

    private Utils.NfcState nfcState;

    public static HandleNoNfcFragment newInstance(Utils.NfcState nfcState) {
        HandleNoNfcFragment fragment = new HandleNoNfcFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NFC_STATE, nfcState.name());
        fragment.setArguments(args);
        return fragment;
    }

    public HandleNoNfcFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nfcState = Utils.NfcState.valueOf(getArguments().getString(ARG_NFC_STATE), Utils.NfcState.NOT_SUPPORTED);
        } else
            nfcState = Utils.NfcState.NOT_SUPPORTED;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (nfcState == Utils.NfcState.DISABLED) {
            nfcStatusTextView.setText(R.string.nfc_disabled);
            settingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ACTION_NFC_SETTINGS));
                }
            });
        } else {
            nfcStatusTextView.setText(R.string.nfc_unavailable);
            settingButton.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_handle_no_nfc, container, false);
        nfcStatusTextView = (TextView) rootView.findViewById(R.id.text_nfc_status);
        settingButton = (Button) rootView.findViewById(R.id.button_nfc_settings);
        return rootView;
    }

}
