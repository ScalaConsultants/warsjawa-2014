package io.scalac.warsjawa.fragment;


import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import io.scalac.warsjawa.Constants;
import io.scalac.warsjawa.R;
import io.scalac.warsjawa.model.Contact;
import io.scalac.warsjawa.utils.Utils;

public class ReadTagFragment extends BaseFragment {

    private TextView textViewInfo;
    private ProgressBar progressBar;
    private ImageButton imageButtonScalac;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_read_tag, container, false);
        imageButtonScalac = (ImageButton) rootView.findViewById(R.id.imageButtonScalac);
        textViewInfo = (TextView) rootView.findViewById(R.id.textViewInfo);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        return rootView;
    }

    @Override
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
                boolean success = false;
                try {
                    HttpClient httpclient = new DefaultHttpClient();

                    HttpGet request = new HttpGet();
                    URI website = new URI(Constants.API_ADDRESS + "contact/" + tagId);
                    request.setURI(website);
                    HttpResponse response = httpclient.execute(request);
                    final String responseStr = EntityUtils.toString(response.getEntity());
                    try {
                        JSONObject jsonObject = new JSONObject(responseStr);
                        final Contact contact = new Contact(jsonObject.getString("email"), jsonObject.getString("name"));
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                createAndInsertContact(contact);
                                progressBar.setVisibility(View.GONE);
                                textViewInfo.setVisibility(View.VISIBLE);
                            }
                        });
                        success = true;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (!success)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(), R.string.contact_download_failed, Toast.LENGTH_SHORT).show();
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
