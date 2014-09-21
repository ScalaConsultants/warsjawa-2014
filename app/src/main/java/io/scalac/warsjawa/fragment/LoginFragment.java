package io.scalac.warsjawa.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import io.scalac.warsjawa.MainActivity;
import io.scalac.warsjawa.R;
import io.scalac.warsjawa.utils.Utils;

public class LoginFragment extends BaseFragment {

    EditText editTextLogin;
    EditText editTextPass;
    Button buttonLogin;

    public LoginFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Utils.clearUserCredentials(getActivity().getApplicationContext());
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = editTextLogin.getText().toString();
                String pass = editTextPass.getText().toString();
                if (login != null && !login.isEmpty() && pass != null && !pass.isEmpty()) {
                    Utils.storeUserCredentials(getActivity().getApplicationContext(), login, pass);
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.setChildFragment(new RegisterFragment(), true, true);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.enter_login_pass, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        editTextLogin = (EditText) rootView.findViewById(R.id.editTextLogin);
        editTextPass = (EditText) rootView.findViewById(R.id.editTextPass);
        buttonLogin = (Button) rootView.findViewById(R.id.buttonLogin);
        return rootView;
    }

}
