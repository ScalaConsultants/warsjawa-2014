package io.scalac.warsjawa;

import android.annotation.SuppressLint;
import android.os.Build;
import android.provider.Settings;

public class Constants {
    public final static String API_ADDRESS = "http://warsjawa.pl/api/";
    public static final String ARG_EMAIL = "email";
    public static final String ARG_NAME = "name";
    public static final String ARG_NFC_STATE = "nfcState";
    @SuppressLint("InlinedApi")
    public static final String ACTION_NFC_SETTINGS = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) ? Settings.ACTION_NFC_SETTINGS : Settings.ACTION_WIRELESS_SETTINGS;
}
