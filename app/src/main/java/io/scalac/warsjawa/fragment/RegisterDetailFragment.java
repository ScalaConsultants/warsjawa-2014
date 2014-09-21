package io.scalac.warsjawa.fragment;


import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import io.scalac.warsjawa.Constants;
import io.scalac.warsjawa.R;
import io.scalac.warsjawa.model.Contact;
import io.scalac.warsjawa.utils.Utils;

public class RegisterDetailFragment extends BaseFragment {

    private String email;
    private String name;
    private TextView textViewName;
    private TextView textViewEmail;
    private TextView textViewRegInfo;
    private ProgressBar progressBar;

    public static RegisterDetailFragment newInstance(Contact contact) {
        RegisterDetailFragment fragment = new RegisterDetailFragment();
        Bundle args = new Bundle();
        args.putString(Constants.ARG_EMAIL, contact.getEmail());
        args.putString(Constants.ARG_NAME, contact.getName());
        fragment.setArguments(args);
        return fragment;
    }

    public RegisterDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            email = getArguments().getString(Constants.ARG_EMAIL);
            name = getArguments().getString(Constants.ARG_NAME);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        textViewEmail.setText(email);
        textViewName.setText(name);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register_detail, container, false);
        textViewName = (TextView) rootView.findViewById(R.id.textName);
        textViewEmail = (TextView) rootView.findViewById(R.id.textEmail);
        textViewRegInfo = (TextView) rootView.findViewById(R.id.textViewRegInfo);
        textViewRegInfo.setVisibility(View.GONE);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        return rootView;
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            registerTagId(Utils.getTagId(intent));
        }
    }

    private void registerTagId(final String tagId) {
        textViewRegInfo.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                int statusCode = 0;
                try {
                    HttpClient httpclient = new DefaultHttpClient();

                    //HttpGet request = new HttpGet();
                    HttpPut request = new HttpPut();
                    request.addHeader(Utils.getCredentials(getActivity().getApplicationContext()));
                    URI website = new URI(Constants.API_ADDRESS + "contact/" + email + "/" + tagId);
                    request.setURI(website);
                    HttpResponse response = httpclient.execute(request);
                    statusCode = response.getStatusLine().getStatusCode();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                boolean success = (200 <= statusCode && statusCode < 300);
                final String statusText;
                if (success)
                    statusText = getString(R.string.register_success);
                else
                    statusText = getString(R.string.register_failed) + ((statusCode > 0) ? ": " + statusCode : "");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        textViewRegInfo.setVisibility(View.VISIBLE);
                        textViewRegInfo.setText(statusText);
                    }
                });
            }
        }).start();
    }

}
