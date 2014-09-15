package io.scalac.warsjawa;

import android.content.Intent;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

    public void onNewIntent(Intent intent) {
    }

    public boolean onBackPressed() {
        return false;
    }
}
