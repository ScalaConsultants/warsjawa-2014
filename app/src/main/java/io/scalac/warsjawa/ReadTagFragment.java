package io.scalac.warsjawa;


import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

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
                String payloadString = new String(payload);
                try {
                    JSONArray jsonArray = new JSONArray(payloadString);
                    String text = "e-mail: " + jsonArray.getString(0);
                    text += "\nName: " + jsonArray.getString(1);
                    nfcValueTextView.setText(getString(R.string.nfc_value) + ":\n" + text);
                    createAndInsertContact(jsonArray.getString(0), jsonArray.getString(1));
                } catch (JSONException e) {
                    e.printStackTrace();
                    nfcValueTextView.setText(getString(R.string.nfc_value) + ":\n" + payloadString);
                }
            }
        }
    }

    private void createAndInsertContact(String email, String name) {
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE)
                .putExtra(ContactsContract.Intents.Insert.EMAIL, email)
                .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .putExtra(ContactsContract.Intents.Insert.NAME, name);
        startActivity(intent);
    }

}
