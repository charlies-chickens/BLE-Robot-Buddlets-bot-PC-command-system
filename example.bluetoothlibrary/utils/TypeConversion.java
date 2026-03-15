package com.example.bluetoothlibrary.utils;

/* JADX INFO: loaded from: classes.dex */
public class TypeConversion {
    public static String bytes2HexString(byte[] bArr, int i) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i2 = 0; i2 < i; i2++) {
            String hexString = Integer.toHexString(bArr[i2] & 255);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            stringBuffer.append(hexString.toUpperCase());
            stringBuffer.append(" ");
        }
        return stringBuffer.toString();
    }

    public static byte[] hexString2Bytes(String str) {
        int length = str.length() / 2;
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = Integer.valueOf(str.substring(i2, i2 + 2), 16).byteValue();
        }
        return bArr;
    }

    public static String string2HexString(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            stringBuffer.append(Integer.toHexString(str.charAt(i)));
        }
        return stringBuffer.toString();
    }

    public static String hexString2String(String str) {
        String string = "";
        for (int i = 0; i < str.length() / 2; i++) {
            StringBuilder sb = new StringBuilder();
            sb.append(string);
            int i2 = i * 2;
            sb.append((char) Integer.valueOf(str.substring(i2, i2 + 2), 16).byteValue());
            string = sb.toString();
        }
        return string;
    }

    public static Byte char2Byte(Character ch) {
        return Byte.valueOf(Integer.valueOf(ch.charValue()).byteValue());
    }

    public static String intToHexString(int i, int i2) {
        String hexString = Integer.toHexString(i);
        int length = (i2 << 1) - hexString.length();
        if (length > 0) {
            for (int i3 = 0; i3 < length; i3++) {
                hexString = "0" + hexString;
            }
        }
        return hexString.toUpperCase();
    }

    public static byte[] stringToByteArray(String str) {
        int length = str.length();
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            bArr[i] = (byte) str.charAt(i);
        }
        return bArr;
    }

    public static String bytes2HexString(byte[] bArr) {
        String upperCase;
        String str = "";
        if (bArr == null) {
            return "";
        }
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b);
            int length = hexString.length();
            if (length > 2) {
                upperCase = hexString.substring(length - 2).toUpperCase();
            } else if (length == 1) {
                upperCase = "0" + hexString.toUpperCase();
            } else {
                upperCase = hexString.toUpperCase();
            }
            str = str + upperCase;
        }
        return str;
    }

    public static String bytes20xHexString(byte[] bArr) {
        String str;
        String str2 = "";
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b);
            int length = hexString.length();
            if (length > 2) {
                str = "0x" + hexString.substring(length - 2).toUpperCase();
            } else if (length == 1) {
                str = "0x0" + hexString.toUpperCase();
            } else {
                str = "0x" + hexString.toUpperCase();
            }
            str2 = str2 + str + " ";
        }
        return str2;
    }

    public static String asciiStr2HexStr(String str) {
        StringBuilder sb = new StringBuilder();
        for (char c : str.toCharArray()) {
            sb.append(intToHexString(c, 1));
        }
        return sb.toString();
    }
}