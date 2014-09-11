package io.scalac.warsjawa;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;


public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment != null) {
            MainFragment mainFragment = (MainFragment) fragment;
            mainFragment.onNewIntent(intent);
        }
    }
}
