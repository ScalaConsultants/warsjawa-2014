package io.scalac.warsjawa;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.scalac.warsjawa.model.Contact;

public class RegisterFragment extends BaseFragment {
    private EditText editText;
    private View mainLayout;
    private ProgressBar progressBar;
    private ListView listView;
    private ItemAdapter listAdapter = new ItemAdapter();
    private List<Contact> contactsList = new ArrayList<Contact>();
    private List<Contact> contactsAdapterList = new ArrayList<Contact>();
    boolean contactsDownloaded = false;

    public RegisterFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!contactsDownloaded) {
            mainLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HttpClient httpclient = new DefaultHttpClient();

                        HttpGet request = new HttpGet();
                        URI website = new URI("http://phansrv.ddns.net/warsjawa/contacts.json");
                        request.setURI(website);
                        HttpResponse response = httpclient.execute(request);
                        final String responseStr = EntityUtils.toString(response.getEntity(), "UTF-8");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONArray jsonArray = new JSONArray(responseStr);
                                    contactsList.clear();
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        contactsList.add(new Contact(jsonObject.getString("email"), jsonObject.getString("name")));
                                    }
                                    contactsAdapterList.clear();
                                    contactsAdapterList.addAll(contactsList);
                                    listAdapter.notifyDataSetChanged();
                                    contactsDownloaded = true;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                progressBar.setVisibility(View.GONE);
                                mainLayout.setVisibility(View.VISIBLE);
                            }
                        });
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editText.clearFocus();
                Contact contact = contactsAdapterList.get(position);
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.setChildFragment(RegisterDetailFragment.newInstance(contact), true);
            }
        });
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                String text = editText.getText().toString().toLowerCase(Locale.getDefault());
                listAdapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                }
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        if (editText.hasFocus()) {
            editText.clearFocus();
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);
        listView = (ListView) rootView.findViewById(R.id.listView);
        listView.setAdapter(listAdapter);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        mainLayout = rootView.findViewById(R.id.mainLayout);
        editText = (EditText) rootView.findViewById(R.id.editText);
        return rootView;
    }


    class ItemAdapter extends BaseAdapter {

        private class ViewHolder {
            public TextView textName;
            public TextView textEmail;
        }

        @Override
        public int getCount() {
            return contactsAdapterList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View viewItem;
            ViewHolder holder;

            if (convertView == null) {
                viewItem = getActivity().getLayoutInflater().inflate(R.layout.register_list_item, parent, false);
                holder = new ViewHolder();
                holder.textName = (TextView) viewItem.findViewById(R.id.textName);
                holder.textEmail = (TextView) viewItem.findViewById(R.id.textEmail);
                viewItem.setTag(holder);
            } else {
                viewItem = convertView;
                holder = (ViewHolder) viewItem.getTag();
            }

            Contact contact = contactsAdapterList.get(position);
            holder.textName.setText(contact.getName());
            holder.textEmail.setText(contact.getEmail());

            return viewItem;
        }

        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            contactsAdapterList.clear();
            if (charText.length() == 0) {
                contactsAdapterList.addAll(contactsList);
            } else {
                for (Contact contact : contactsList) {
                    if (contact.getName().toLowerCase(Locale.getDefault()).contains(charText) || contact.getEmail().toLowerCase(Locale.getDefault()).contains(charText)) {
                        contactsAdapterList.add(contact);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

}
