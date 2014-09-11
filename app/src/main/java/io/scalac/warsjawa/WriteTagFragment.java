package io.scalac.warsjawa;

import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.IOException;

public class WriteTagFragment extends Fragment {

    private NfcAdapter mNfcAdapter;
    private TextView nfcStatusTextView;
    private Button settingButton;
    private Context context;

    public WriteTagFragment() {
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

    public void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            // validate that this tag can be written
            Tag detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (supportedTechs(detectedTag.getTechList())) {
                // check if tag is writable (to the extent that we can
                if (writableTag(detectedTag)) {
                    //writeTag here
                    WriteResponse wr = writeTag(getTagAsNdef(), detectedTag);
                    String message = (wr.getStatus() == 1 ? "Success: " : "Failed: ") + wr.getMessage();
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "This tag is not writable", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "This tag type is not supported", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_write_tag, container, false);
        nfcStatusTextView = (TextView) rootView.findViewById(R.id.text_nfc_status);
        settingButton = (Button) rootView.findViewById(R.id.button_nfc_settings);
        return rootView;
    }

    public WriteResponse writeTag(NdefMessage message, Tag tag) {
        int size = message.toByteArray().length;
        String mess = "";
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    return new WriteResponse(0, "Tag is read-only");
                }
                if (ndef.getMaxSize() < size) {
                    mess = "Tag capacity is " + ndef.getMaxSize() + " bytes, message is " + size
                            + " bytes.";
                    return new WriteResponse(0, mess);
                }
                ndef.writeNdefMessage(message);
                mess = "Wrote message to pre-formatted tag.";
                return new WriteResponse(1, mess);
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        mess = "Formatted tag and wrote message";
                        return new WriteResponse(1, mess);
                    } catch (IOException e) {
                        mess = "Failed to format tag.";
                        return new WriteResponse(0, mess);
                    }
                } else {
                    mess = "Tag doesn't support NDEF.";
                    return new WriteResponse(0, mess);
                }
            }
        } catch (Exception e) {
            mess = "Failed to write tag";
            return new WriteResponse(0, mess);
        }
    }

    private class WriteResponse {
        int status;
        String message;

        WriteResponse(int Status, String Message) {
            this.status = Status;
            this.message = Message;
        }

        public int getStatus() {
            return status;
        }

        public String getMessage() {
            return message;
        }
    }

    public static boolean supportedTechs(String[] techs) {
        boolean ultralight = false;
        boolean nfcA = false;
        boolean ndef = false;
        for (String tech : techs) {
            if (tech.equals("android.nfc.tech.MifareUltralight")) {
                ultralight = true;
            } else if (tech.equals("android.nfc.tech.NfcA")) {
                nfcA = true;
            } else if (tech.equals("android.nfc.tech.Ndef") || tech.equals("android.nfc.tech.NdefFormatable")) {
                ndef = true;
            }
        }
        if (ultralight && nfcA && ndef) {
            return true;
        } else {
            return false;
        }
    }

    private boolean writableTag(Tag tag) {
        try {
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();
                if (!ndef.isWritable()) {
                    Toast.makeText(context, "Tag is read-only.", Toast.LENGTH_SHORT).show();
                    ndef.close();
                    return false;
                }
                ndef.close();
                return true;
            }
        } catch (Exception e) {
            Toast.makeText(context, "Failed to read tag", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private NdefMessage getTagAsNdef() {
        boolean addAAR = false;
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("test@example.com");
        jsonArray.put("Test One");
        NdefRecord jsonRecord = new NdefRecord(
                NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], jsonArray.toString().getBytes());
        if (addAAR) {
            return new NdefMessage(new NdefRecord[]{
                    jsonRecord, NdefRecord.createApplicationRecord(context.getPackageName())
            });
        } else {
            return new NdefMessage(new NdefRecord[]{
                    jsonRecord});
        }
    }
}
