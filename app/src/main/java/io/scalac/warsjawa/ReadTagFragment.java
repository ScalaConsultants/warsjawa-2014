package io.scalac.warsjawa;


import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.scalac.warsjawa.model.Contact;
import io.scalac.warsjawa.utils.Utils;

public class ReadTagFragment extends Fragment {

    TextView textViewInfo;
    ProgressBar progressBar;
    ImageButton imageButtonScalac;

    public ReadTagFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageButtonScalac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(getString(R.string.scalac_link)));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_read_tag, container, false);
        imageButtonScalac = (ImageButton) rootView.findViewById(R.id.imageButtonScalac);
        textViewInfo = (TextView) rootView.findViewById(R.id.textViewInfo);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        return rootView;
    }

    private Contact getContact(String tagId) {
        return new Contact("test@example.com", "Test NFC", tagId);
    }

    public void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            downloadAndInsertContact(Utils.getTagId(intent));
        }
    }

    private void downloadAndInsertContact(final String tagId) {
        textViewInfo.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                final Contact contact = getContact(tagId);
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        createAndInsertContact(contact);
                        progressBar.setVisibility(View.GONE);
                        textViewInfo.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();
    }

    private void createAndInsertContact(Contact contact) {
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE)
                .putExtra(ContactsContract.Intents.Insert.EMAIL, contact.getEmail())
                .putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                .putExtra(ContactsContract.Intents.Insert.NAME, contact.getName());
        startActivity(intent);
    }

}
