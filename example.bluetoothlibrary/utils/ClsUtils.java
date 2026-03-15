package com.example.bluetoothlibrary.utils;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public class ClsUtils {
    public static BluetoothDevice remoteDevice;

    public static boolean createBond(Class cls, BluetoothDevice bluetoothDevice) throws Exception {
        return ((Boolean) cls.getMethod("createBond", new Class[0]).invoke(bluetoothDevice, new Object[0])).booleanValue();
    }

    public static boolean autoBond(Class cls, BluetoothDevice bluetoothDevice, String str) throws Exception {
        return ((Boolean) cls.getMethod("setPin", byte[].class).invoke(bluetoothDevice, str.getBytes())).booleanValue();
    }

    public static void setPairingConfirmation(BluetoothDevice bluetoothDevice) {
        try {
            Field declaredField = bluetoothDevice.getClass().getDeclaredField("sService");
            declaredField.setAccessible(true);
            Object obj = declaredField.get(bluetoothDevice);
            Method declaredMethod = obj.getClass().getDeclaredMethod("setPairingConfirmation", BluetoothDevice.class, Boolean.TYPE);
            declaredMethod.setAccessible(true);
            declaredMethod.invoke(obj, bluetoothDevice, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e2) {
            e2.printStackTrace();
        } catch (NoSuchMethodException e3) {
            e3.printStackTrace();
        } catch (InvocationTargetException e4) {
            e4.printStackTrace();
        }
    }

    public static boolean removeBond(Class cls, BluetoothDevice bluetoothDevice) throws Exception {
        return ((Boolean) cls.getMethod("removeBond", new Class[0]).invoke(bluetoothDevice, new Object[0])).booleanValue();
    }

    public static boolean setPin(Class cls, BluetoothDevice bluetoothDevice, String str) throws Exception {
        try {
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
        return true;
    }

    public static boolean cancelPairingUserInput(Class cls, BluetoothDevice bluetoothDevice) throws Exception {
        return ((Boolean) cls.getMethod("cancelPairingUserInput", new Class[0]).invoke(bluetoothDevice, new Object[0])).booleanValue();
    }

    public static boolean cancelBondProcess(Class cls, BluetoothDevice bluetoothDevice) throws Exception {
        return ((Boolean) cls.getMethod("cancelBondProcess", new Class[0]).invoke(bluetoothDevice, new Object[0])).booleanValue();
    }

    public static void printAllInform(Class cls) {
        try {
            for (int i = 0; i < cls.getMethods().length; i++) {
            }
            for (int i2 = 0; i2 < cls.getFields().length; i2++) {
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e2) {
            e2.printStackTrace();
        } catch (Exception e3) {
            e3.printStackTrace();
        }
    }

    public static boolean refreshDeviceCache(BluetoothGatt bluetoothGatt) {
        if (bluetoothGatt != null) {
            try {
                Method method = bluetoothGatt.getClass().getMethod("refresh", new Class[0]);
                if (method != null) {
                    return ((Boolean) method.invoke(bluetoothGatt, new Object[0])).booleanValue();
                }
            } catch (Exception unused) {
                Log.i("Config", "An exception occured while refreshing device");
            }
        }
        return false;
    }
}