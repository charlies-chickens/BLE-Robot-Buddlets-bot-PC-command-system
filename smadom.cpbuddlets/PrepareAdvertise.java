package com.smadom.cpbuddlets;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.os.Handler;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class PrepareAdvertise {
    public static final int BLE_ERROR_CODE_OK = 100;
    public static final int BLE_ERROR_CODE_UNENABLE = 101;
    public static final int BLE_ERROR_CODE_UNKNOW = 0;
    public static final int BLE_ERROR_CODE_UNSURPORT = 102;
    private static PrepareAdvertise mInstance;
    private BluetoothAdapter adapter;
    private BluetoothLeAdvertiser advertiser;
    private AdvertiseCallback callback;
    private int errorCode;
    private AdvertiseSettings settings;
    Handler mHandler = new Handler();
    private boolean isOn = true;
    private Runnable mStopAdvertiseRunnable = new Runnable() { // from class: com.smadom.cpbuddlets.PrepareAdvertise.1
        @Override // java.lang.Runnable
        public void run() {
            Log.e("yqy", "handle runnable");
            PrepareAdvertise.this.stopAdvertising();
        }
    };

    public static PrepareAdvertise getInstance() {
        if (mInstance == null) {
            synchronized (PrepareAdvertise.class) {
                if (mInstance == null) {
                    PrepareAdvertise prepareAdvertise = new PrepareAdvertise();
                    mInstance = prepareAdvertise;
                    return prepareAdvertise;
                }
            }
        }
        return mInstance;
    }

    public void destroyInstance() {
        mInstance = null;
    }

    private PrepareAdvertise() {
        this.advertiser = null;
        this.errorCode = 0;
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        this.adapter = defaultAdapter;
        if (defaultAdapter != null && defaultAdapter.isEnabled()) {
            BluetoothLeAdvertiser bluetoothLeAdvertiser = this.adapter.getBluetoothLeAdvertiser();
            this.advertiser = bluetoothLeAdvertiser;
            if (bluetoothLeAdvertiser == null) {
                this.errorCode = BLE_ERROR_CODE_UNSURPORT;
            }
        } else {
            this.errorCode = BLE_ERROR_CODE_UNENABLE;
        }
        initSettings();
    }

    public void setErrorCode(int i) {
        this.errorCode = i;
    }

    private void initSettings() {
        this.settings = new AdvertiseSettings.Builder().setAdvertiseMode(2).setConnectable(true).setTimeout(0).setTxPowerLevel(3).build();
    }

    public void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled() && this.advertiser == null) {
            BluetoothLeAdvertiser bluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
            this.advertiser = bluetoothLeAdvertiser;
            if (bluetoothLeAdvertiser == null) {
                this.errorCode = BLE_ERROR_CODE_UNSURPORT;
            }
            this.adapter = bluetoothAdapter;
            return;
        }
        this.errorCode = BLE_ERROR_CODE_UNENABLE;
    }

    public BluetoothAdapter getAdapter() {
        return this.adapter;
    }

    public int startAdvertising(int[] iArr, int i) {
        if (this.advertiser == null) {
            return this.errorCode;
        }
        try {
            stopAll();
            byte[] bArr = new byte[iArr.length];
            for (int i2 = 0; i2 < iArr.length; i2++) {
                bArr[i2] = (byte) iArr[i2];
            }
            AdvertiseData advertiseDataBuild = new AdvertiseData.Builder().addManufacturerData(2402, bArr).build();
            BleAdvertiseCallback bleAdvertiseCallback = new BleAdvertiseCallback();
            this.callback = bleAdvertiseCallback;
            this.advertiser.startAdvertising(this.settings, advertiseDataBuild, bleAdvertiseCallback);
            this.mHandler.postDelayed(this.mStopAdvertiseRunnable, i);
            return 100;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void stopAdvertising() {
        try {
            if (this.callback == null || this.advertiser == null) {
                return;
            }
            this.advertiser.stopAdvertising(this.callback);
            this.callback = null;
            Log.e("yqy", "stopAdvertising");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopAll() {
        stopAdvertising();
        this.mHandler.removeCallbacks(this.mStopAdvertiseRunnable);
    }

    public static String byteArrayToHexString(byte[] bArr) {
        if (bArr == null) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b : bArr) {
            int i = b & 255;
            if (i < 16) {
                stringBuffer.append("0");
            }
            stringBuffer.append(Integer.toHexString(i));
            stringBuffer.append(" ");
        }
        return stringBuffer.toString().trim();
    }

    public final class BleAdvertiseCallback extends AdvertiseCallback {
        public BleAdvertiseCallback() {
        }

        @Override // android.bluetooth.le.AdvertiseCallback
        public void onStartFailure(int i) {
            super.onStartFailure(i);
        }

        @Override // android.bluetooth.le.AdvertiseCallback
        public void onStartSuccess(AdvertiseSettings advertiseSettings) {
            super.onStartSuccess(advertiseSettings);
        }
    }
}