package io.scalac.warsjawa.utils;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;

import java.math.BigInteger;

public class Utils {

    public static String getTagId(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        return Utils.bin2hex(tag.getId());
    }

    public static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1, data));
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

    public static NfcState getNfcState(NfcAdapter mNfcAdapter) {
        if (mNfcAdapter != null) {
            return mNfcAdapter.isEnabled() ? Utils.NfcState.ENABLED : Utils.NfcState.DISABLED;
        } else
            return Utils.NfcState.NOT_SUPPORTED;
    }
}
