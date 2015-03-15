package com.gnuploteditor;

import java.util.prefs.Preferences;

/**
 * Created by HondaDai on 2015/03/14.
 */
public class UserPref {

    private static Preferences mPref = Preferences.userNodeForPackage(GnuplotEditor.class);

    public static void putInt(String key, int value) {
        mPref.putInt(key, value);
    }

    public static int getInt(String key, int def) {
        return mPref.getInt(key, def);
    }

    public static void putBoolean(String key, boolean value) {
        mPref.putBoolean(key, value);
    }

    public static boolean getBoolean(String key, boolean def) {
        return mPref.getBoolean(key, def);
    }

    public static void putDouble(String key, double value) {
        mPref.putDouble(key, value);
    }

    public static double getDouble(String key, double def) {
        return mPref.getDouble(key, def);
    }

    public static void put(String key, String value) {
        mPref.put(key, value);
    }

    public static String get(String key, String def) {
        return mPref.get(key, def);
    }
}
