package io.scalac.warsjawa.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.util.Base64;

import org.apache.http.Header;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.auth.BasicScheme;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

public class Utils {
    private static final String USER_PREFS_NAME = "user";
    private static final String USER_LOGIN_KEY = "login";
    private static final String USER_PASSWORD_KEY = "password";

    public static String getTagId(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        return Utils.bin2hex(tag.getId());
    }

    public static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
    }

    public static NfcState getNfcState(NfcAdapter mNfcAdapter) {
        if (mNfcAdapter != null) {
            return mNfcAdapter.isEnabled() ? Utils.NfcState.ENABLED : Utils.NfcState.DISABLED;
        } else
            return Utils.NfcState.NOT_SUPPORTED;
    }

    public static String retrievePass(Context context) {
        SharedPreferences settings = context.getSharedPreferences(USER_PREFS_NAME, Context.MODE_PRIVATE);
        String encoded = settings.getString(USER_PASSWORD_KEY, null);
        return decode(encoded);
    }

    public static String retrieveLogin(Context context) {
        SharedPreferences settings = context.getSharedPreferences(USER_PREFS_NAME, Context.MODE_PRIVATE);
        String encoded = settings.getString(USER_LOGIN_KEY, null);
        return decode(encoded);
    }

    public static Header getCredentials(Context context) {
        return BasicScheme.authenticate(new UsernamePasswordCredentials(retrieveLogin(context),
                retrievePass(context)), "UTF-8", false);
    }

    public static boolean isLoginDataStored(Context context) {
        return (retrieveLogin(context) != null && retrievePass(context) != null);
    }

    /**
     * Store user login and password for later user
     *
     * @param login    String plaintext username
     * @param password String plaintext password
     */
    public static void storeUserCredentials(Context context, String login, String password) {
        SharedPreferences settings = context.getSharedPreferences(USER_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor store = settings.edit();
        try {
            store.putString(USER_LOGIN_KEY, encode(login));
            store.putString(USER_PASSWORD_KEY, encode(password));
            store.commit();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void clearUserCredentials(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(USER_PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor store = prefs.edit();
        store.remove(USER_LOGIN_KEY);
        store.remove(USER_PASSWORD_KEY);
        store.commit();
    }

    private static String decode(String encoded) {
        String decoded = null;
        if (encoded != null) {
            byte[] data = Base64.decode(encoded, Base64.DEFAULT);
            try {
                decoded = new String(data, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return decoded;
    }

    private static String encode(String input) throws UnsupportedEncodingException {
        return Base64.encodeToString(input.getBytes("UTF-8"), Base64.DEFAULT);
    }

    public enum NfcState {
        NOT_SUPPORTED, ENABLED, DISABLED;

        public static NfcState valueOf(String name, NfcState fallback) {
            try {
                return Utils.NfcState.valueOf(name);
            } catch (IllegalArgumentException e) {
                return fallback;
            }
        }
    }
}
