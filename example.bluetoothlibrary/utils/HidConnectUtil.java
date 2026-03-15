package com.example.bluetoothlibrary.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class HidConnectUtil {
    Context context;
    private BluetoothDevice device;
    GetHidConnectListListener getHidConnectListListener;
    ArrayList<BluetoothDevice> hidConnectList = new ArrayList<>();
    private BluetoothProfile.ServiceListener getList = new BluetoothProfile.ServiceListener() { // from class: com.example.bluetoothlibrary.utils.HidConnectUtil.1
        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int i) {
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            try {
                if (i == HidConnectUtil.getInputDeviceHiddenConstant()) {
                    HidConnectUtil.this.hidConnectList.clear();
                    Iterator<BluetoothDevice> it = bluetoothProfile.getConnectedDevices().iterator();
                    while (it.hasNext()) {
                        HidConnectUtil.this.hidConnectList.add(it.next());
                    }
                }
                HidConnectUtil.this.getHidConnectListListener.getSuccess(HidConnectUtil.this.hidConnectList);
            } catch (Exception unused) {
            }
        }
    };
    private BluetoothProfile.ServiceListener connect = new BluetoothProfile.ServiceListener() { // from class: com.example.bluetoothlibrary.utils.HidConnectUtil.2
        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int i) {
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            try {
                if (i != HidConnectUtil.getInputDeviceHiddenConstant() || HidConnectUtil.this.device == null) {
                    return;
                }
                bluetoothProfile.getClass().getMethod("connect", BluetoothDevice.class).invoke(bluetoothProfile, HidConnectUtil.this.device);
            } catch (Exception unused) {
            }
        }
    };
    private BluetoothProfile.ServiceListener disConnect = new BluetoothProfile.ServiceListener() { // from class: com.example.bluetoothlibrary.utils.HidConnectUtil.3
        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceDisconnected(int i) {
        }

        @Override // android.bluetooth.BluetoothProfile.ServiceListener
        public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
            try {
                if (i == HidConnectUtil.getInputDeviceHiddenConstant()) {
                    Iterator<BluetoothDevice> it = bluetoothProfile.getConnectedDevices().iterator();
                    while (it.hasNext()) {
                        HidConnectUtil.this.hidConnectList.add(it.next());
                    }
                    if (HidConnectUtil.this.device != null) {
                        bluetoothProfile.getClass().getMethod("disconnect", BluetoothDevice.class).invoke(bluetoothProfile, HidConnectUtil.this.device);
                    }
                }
            } catch (Exception unused) {
            }
        }
    };

    public interface GetHidConnectListListener {
        void getSuccess(ArrayList<BluetoothDevice> arrayList);
    }

    public HidConnectUtil(Context context) {
        this.context = context;
    }

    public static int getInputDeviceHiddenConstant() {
        Field[] fields = BluetoothProfile.class.getFields();
        int length = fields.length;
        for (int i = 0; i < length; i++) {
            Field field = fields[i];
            int modifiers = field.getModifiers();
            if (Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers) && Modifier.isFinal(modifiers)) {
                try {
                    if (field.getName().equals("INPUT_DEVICE")) {
                        return field.getInt(null);
                    }
                    continue;
                } catch (Exception unused) {
                    continue;
                }
            }
        }
        return -1;
    }

    public void connect(BluetoothDevice bluetoothDevice) {
        this.device = bluetoothDevice;
        try {
            BluetoothAdapter.getDefaultAdapter().getProfileProxy(this.context, this.connect, getInputDeviceHiddenConstant());
        } catch (Exception unused) {
        }
    }

    public void disConnect(BluetoothDevice bluetoothDevice) {
        this.device = bluetoothDevice;
        try {
            BluetoothAdapter.getDefaultAdapter().getProfileProxy(this.context, this.disConnect, getInputDeviceHiddenConstant());
        } catch (Exception unused) {
        }
    }

    public void pair(BluetoothDevice bluetoothDevice) {
        this.device = bluetoothDevice;
        try {
            BluetoothDevice.class.getMethod("createBond", new Class[0]).invoke(this.device, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unPair(BluetoothDevice bluetoothDevice) {
        this.device = bluetoothDevice;
        try {
            BluetoothDevice.class.getMethod("removeBond", new Class[0]).invoke(this.device, new Object[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean rename(BluetoothDevice bluetoothDevice, String str) {
        this.device = bluetoothDevice;
        try {
            return (Boolean) BluetoothDevice.class.getMethod("setAlias", String.class).invoke(this.device, str);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void getHidConnectList(GetHidConnectListListener getHidConnectListListener) {
        this.getHidConnectListListener = getHidConnectListListener;
        try {
            BluetoothAdapter.getDefaultAdapter().getProfileProxy(this.context, this.getList, getInputDeviceHiddenConstant());
        } catch (Exception unused) {
        }
    }
}