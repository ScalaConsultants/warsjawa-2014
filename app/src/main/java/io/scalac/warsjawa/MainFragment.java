package io.scalac.warsjawa;


import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends BaseFragment {

    private NfcAdapter mNfcAdapter;
    private IntentFilter[] mWriteTagFilters;
    private PendingIntent mNfcPendingIntent;

    public MainFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
        mNfcPendingIntent = PendingIntent.getActivity(getActivity(), 0, new Intent(getActivity(),
                getActivity().getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TOP), 0);
        IntentFilter discovery = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        mWriteTagFilters = new IntentFilter[]{discovery};

        Fragment fragment = new ChooserFragment();
        getChildFragmentManager().beginTransaction()
                .replace(R.id.mainContent, fragment)
                .commit();
    }

    public void setChildFragment(Fragment fragment, boolean addToBackStack) {
        getChildFragmentManager().beginTransaction()
                .replace(R.id.mainContent, fragment)
                .addToBackStack("backStack")
                .commit();
    }

    @Override
    public boolean onBackPressed() {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.mainContent);
        if (fragment != null) {
            BaseFragment baseFragment = (BaseFragment) fragment;
            if (baseFragment.onBackPressed())
                return true;
        }
        return getChildFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mNfcAdapter != null)
            mNfcAdapter.enableForegroundDispatch(getActivity(), mNfcPendingIntent, mWriteTagFilters, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mNfcAdapter != null)
            mNfcAdapter.disableForegroundDispatch(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onNewIntent(Intent intent) {
        Fragment fragment = getChildFragmentManager().findFragmentById(R.id.mainContent);
        if (fragment != null) {
            BaseFragment baseFragment = (BaseFragment) fragment;
            baseFragment.onNewIntent(intent);
        }
    }

}
