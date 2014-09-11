package io.scalac.warsjawa;


import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.scalac.warsjawa.view.SlidingTabLayout;

public class MainFragment extends Fragment {

    int currentTabPosition = 0;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
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
        IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter techDetected = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        // Intent filters for writing to a tag
        mWriteTagFilters = new IntentFilter[]{discovery};

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mViewPager = (ViewPager) getView().findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mSlidingTabLayout = (SlidingTabLayout) getView().findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentTabPosition = position;
            }
        });
        mViewPager.setCurrentItem(currentTabPosition);
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

    public void onNewIntent(Intent intent) {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(getFragmentTag(R.id.pager, currentTabPosition));
        if (fragment != null) {
            MenuTab tab = MenuTab.values()[currentTabPosition];
            switch (tab) {
                case READ:
                    ((ReadTagFragment) fragment).onNewIntent(intent);
                    break;
                case WRITE:
                    ((WriteTagFragment) fragment).onNewIntent(intent);
                    break;
                default:
                    break;
            }
        }
    }

    private String getFragmentTag(int viewPagerId, int fragmentPosition) {
        return "android:switcher:" + viewPagerId + ":" + fragmentPosition;
    }

    private enum MenuTab {
        READ, WRITE
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private CharSequence[] titles;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            setupTitles();
        }

        private void setupTitles() {
            titles = new CharSequence[MenuTab.values().length];
            for (MenuTab menuTab : MenuTab.values()) {
                switch (menuTab) {
                    case READ:
                        titles[menuTab.ordinal()] = getString(R.string.tab_read);
                        break;
                    case WRITE:
                        titles[menuTab.ordinal()] = getString(R.string.tab_write);
                        break;
                    default:
                        throw new RuntimeException(menuTab.name() + " tab title not implemented");
                }
            }
        }

        @Override
        public Fragment getItem(int position) {
            MenuTab tab = MenuTab.values()[position];
            switch (tab) {
                case READ:
                    return new ReadTagFragment();
                case WRITE:
                    return new WriteTagFragment();
                default:
                    throw new RuntimeException(tab.name() + " tab fragment not implemented");
            }
        }

        @Override
        public int getCount() {
            return MenuTab.values().length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

}
