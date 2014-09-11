package io.scalac.warsjawa;


import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ReadTagFragment extends Fragment {

    private NfcAdapter mNfcAdapter;
    private TextView nfcStatusTextView;
    private TextView nfcValueTextView;
    private Button settingButton;
    private Context context;

    public ReadTagFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getActivity().getApplicationContext();
        mNfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
        if (mNfcAdapter == null) {
            nfcStatusTextView.setText(R.string.nfc_unavailable);
        }
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNfcAdapter != null)
            nfcStatusTextView.setText(mNfcAdapter.isEnabled() ? R.string.nfc_enabled : R.string.nfc_disabled);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_read_tag, container, false);
        nfcStatusTextView = (TextView) rootView.findViewById(R.id.text_nfc_status);
        settingButton = (Button) rootView.findViewById(R.id.button_nfc_settings);
        nfcValueTextView = (TextView) rootView.findViewById(R.id.text_nfc_value);
        return rootView;
    }


    public void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            NdefMessage[] messages = null;
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMsgs != null) {
                messages = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    messages[i] = (NdefMessage) rawMsgs[i];
                }
            }
            if (messages[0] != null) {
                byte[] payload = messages[0].getRecords()[0].getPayload();
                String jsonString = new String(payload);
                nfcValueTextView.setText(getString(R.string.nfc_value)+"\n" + jsonString);
            }
        }
    }

}
