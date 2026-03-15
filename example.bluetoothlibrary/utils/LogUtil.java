package com.example.bluetoothlibrary.utils;

import android.util.Log;
import com.example.bluetoothlibrary.constant.ConsConfig;

/* JADX INFO: loaded from: classes.dex */
public class LogUtil {
    public static void showLogD(String str, String str2) {
        if (ConsConfig.isDebug) {
            Log.d(str, str2);
        }
    }

    public static void showLogI(String str, String str2) {
        if (ConsConfig.isDebug) {
            Log.i(str, str2);
        }
    }

    public static void showLogW(String str, String str2) {
        if (ConsConfig.isDebug) {
            Log.w(str, str2);
        }
    }

    public static void showLogE(String str, String str2) {
        if (ConsConfig.isDebug) {
            Log.e(str, str2);
        }
    }

    public static void showLogV(String str, String str2) {
        if (ConsConfig.isDebug) {
            Log.v(str, str2);
        }
    }
}