package com.example.bluetoothlibrary.utils;

import java.text.SimpleDateFormat;

/* JADX INFO: loaded from: classes.dex */
public class FormatConversion {
    public static String timeMillis2Data(long j) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Long.valueOf(j)).substring(11);
    }

    public static String simplifyTimeStr(String str) {
        return str.trim().replace("-", "").replace(":", "").replace(" ", "").substring(2);
    }

    public static String cancelTimeStrSpace(String str) {
        return str.trim().replace("-", "").replace(":", "").replace(" ", "");
    }

    public static String addStringSpace(String str) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (true) {
            int i2 = i + 2;
            if (i2 > str.length()) {
                break;
            }
            sb.append(str.substring(i, i2));
            sb.append(" ");
            i = i2;
        }
        int i3 = i + 1;
        if (i3 == str.length()) {
            sb.append(str.substring(i, i3));
            sb.append(" ");
            return sb.toString();
        }
        return sb.toString();
    }
}