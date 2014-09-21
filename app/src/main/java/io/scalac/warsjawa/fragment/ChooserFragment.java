package io.scalac.warsjawa.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import io.scalac.warsjawa.MainActivity;
import io.scalac.warsjawa.R;
import io.scalac.warsjawa.utils.Utils;

public class ChooserFragment extends BaseFragment {

    private Button buttonRead;
    private Button buttonRegister;

    public ChooserFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                switch (v.getId()) {
                    case R.id.buttonRead:
                        mainActivity.setChildFragment(new ReadTagFragment(), true, false);
                        break;
                    case R.id.buttonRegister:
                        if (Utils.isLoginDataStored(getActivity().getApplicationContext())) {
                            mainActivity.setChildFragment(new RegisterFragment(), true, false);
                        } else {
                            mainActivity.setChildFragment(new LoginFragment(), true, false);
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        buttonRead.setOnClickListener(onClickListener);
        buttonRegister.setOnClickListener(onClickListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chooser, container, false);
        buttonRead = (Button) rootView.findViewById(R.id.buttonRead);
        buttonRegister = (Button) rootView.findViewById(R.id.buttonRegister);
        return rootView;
    }


}
