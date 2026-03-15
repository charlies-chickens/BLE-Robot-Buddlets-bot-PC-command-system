package com.example.bluetoothlibrary.bluetooth3;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import com.example.bluetoothlibrary.bluetooth3.bt.AcceptThread;
import com.example.bluetoothlibrary.bluetooth3.bt.ConnectThread;
import com.example.bluetoothlibrary.bluetooth3.bt.ConnectedThread;
import com.example.bluetoothlibrary.broadcastreceiver.BluetoothBroadcastReceiver;
import com.example.bluetoothlibrary.listener.OnBTConnectListener;
import com.example.bluetoothlibrary.listener.OnBindStateChangeListener;
import com.example.bluetoothlibrary.listener.OnBluetoothStateChangeListener;
import com.example.bluetoothlibrary.listener.OnBtWithDeviceConStateListener;
import com.example.bluetoothlibrary.listener.OnDeviceSearchListener;
import com.example.bluetoothlibrary.listener.OnRemoteDeviceConStateListener;
import com.example.bluetoothlibrary.utils.ClsUtils;
import com.example.bluetoothlibrary.utils.FormatConversion;
import com.example.bluetoothlibrary.utils.LogUtil;
import com.example.bluetoothlibrary.utils.TypeConversion;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class BTManager implements IBTManager {
    private static final String TAG = "BTManager";
    private AcceptThread acceptThread;
    private BluetoothAdapter bluetooth3Adapter;
    private BluetoothBroadcastReceiver bluetoothBroadcastReceiver;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private BluetoothDevice curConnDevice;
    private Context mContext;
    private OnBTConnectListener onBTConnectListener;
    private OnDeviceSearchListener onDeviceSearchListener;
    private SystemBtCheck systemBtCheck;
    private boolean curConnState = false;
    private boolean isExeStopScan = false;
    private Handler mHandler = new Handler() { // from class: com.example.bluetoothlibrary.bluetooth3.BTManager.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
        }
    };
    private Runnable stopScanRunnable = new Runnable() { // from class: com.example.bluetoothlibrary.bluetooth3.BTManager.2
        @Override // java.lang.Runnable
        public void run() {
            BTManager.this.cancelDiscoveryDevice();
            if (BTManager.this.onDeviceSearchListener != null) {
                BTManager.this.onDeviceSearchListener.onDiscoveryOutTime();
            }
        }
    };
    private Runnable connectOutTimeRunnable = new Runnable() { // from class: com.example.bluetoothlibrary.bluetooth3.BTManager.4
        @Override // java.lang.Runnable
        public void run() {
            LogUtil.showLogE(BTManager.TAG, "startConnectDevice-->连接超时");
            if (BTManager.this.onBTConnectListener != null) {
                BTManager.this.onBTConnectListener.onConnectOutTime();
            }
            BTManager.this.curConnState = false;
            BTManager.this.clearConnectedThread();
        }
    };

    private static class BTManagerHolder {
        private static BTManager btManager = new BTManager();

        private BTManagerHolder() {
        }
    }

    public static BTManager getInstance() {
        return BTManagerHolder.btManager;
    }

    @Override // com.example.bluetoothlibrary.bluetooth3.IBTManager
    public void setOnBluetoothStateChangeListener(OnBluetoothStateChangeListener onBluetoothStateChangeListener) {
        BluetoothBroadcastReceiver bluetoothBroadcastReceiver = this.bluetoothBroadcastReceiver;
        if (bluetoothBroadcastReceiver == null) {
            LogUtil.showLogE(TAG, "setOnBluetoothStateChangeListener-->bluetoothBroadcastReceiver == null");
        } else {
            bluetoothBroadcastReceiver.setOnBluetoothStateChangeListener(onBluetoothStateChangeListener);
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth3.IBTManager
    public void setOnBindStateChangeListener(OnBindStateChangeListener onBindStateChangeListener) {
        BluetoothBroadcastReceiver bluetoothBroadcastReceiver = this.bluetoothBroadcastReceiver;
        if (bluetoothBroadcastReceiver == null) {
            LogUtil.showLogE(TAG, "setOnBindStateChangeListener-->bluetoothBroadcastReceiver == null");
        } else {
            bluetoothBroadcastReceiver.setOnBindStateChangeListener(onBindStateChangeListener);
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth3.IBTManager
    public void setOnBtWithDeviceConStateListener(OnBtWithDeviceConStateListener onBtWithDeviceConStateListener) {
        BluetoothBroadcastReceiver bluetoothBroadcastReceiver = this.bluetoothBroadcastReceiver;
        if (bluetoothBroadcastReceiver == null) {
            LogUtil.showLogE(TAG, "setOnBtWithDeviceConStateListener-->bluetoothBroadcastReceiver == null");
        } else {
            bluetoothBroadcastReceiver.setOnBtWithDeviceConStateListener(onBtWithDeviceConStateListener);
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth3.IBTManager
    public void setOnRemoteDeviceConStateListener(OnRemoteDeviceConStateListener onRemoteDeviceConStateListener) {
        BluetoothBroadcastReceiver bluetoothBroadcastReceiver = this.bluetoothBroadcastReceiver;
        if (bluetoothBroadcastReceiver == null) {
            LogUtil.showLogE(TAG, "setOnRemoteDeviceConStateListener-->bluetoothBroadcastReceiver == null");
        } else {
            bluetoothBroadcastReceiver.setOnRemoteDeviceConStateListener(onRemoteDeviceConStateListener);
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth3.IBTManager
    public void initBluetooth(Context context) {
        this.mContext = context;
        SystemBtCheck systemBtCheck = SystemBtCheck.getInstance();
        this.systemBtCheck = systemBtCheck;
        systemBtCheck.initBle(this.mContext);
        this.bluetooth3Adapter = this.systemBtCheck.bluetooth3Adapter;
        if (this.bluetoothBroadcastReceiver == null) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.bluetooth.adapter.action.STATE_CHANGED");
            intentFilter.addAction("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED");
            intentFilter.addAction("android.bluetooth.device.action.BOND_STATE_CHANGED");
            intentFilter.addAction("android.bluetooth.device.action.FOUND");
            intentFilter.addAction("android.bluetooth.adapter.action.DISCOVERY_STARTED");
            intentFilter.addAction("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
            intentFilter.addAction("android.bluetooth.adapter.action.SCAN_MODE_CHANGED");
            intentFilter.addAction("android.bluetooth.device.action.PAIRING_REQUEST");
            intentFilter.addAction("android.bluetooth.device.action.ACL_CONNECTED");
            intentFilter.addAction("android.bluetooth.device.action.ACL_DISCONNECTED");
            intentFilter.addAction("android.bluetooth.device.action.NAME_CHANGED");
            BluetoothBroadcastReceiver bluetoothBroadcastReceiver = new BluetoothBroadcastReceiver();
            this.bluetoothBroadcastReceiver = bluetoothBroadcastReceiver;
            this.mContext.registerReceiver(bluetoothBroadcastReceiver, intentFilter);
        }
        this.systemBtCheck.openBluetooth(this.mContext, false);
    }

    @Override // com.example.bluetoothlibrary.bluetooth3.IBTManager
    public void openBluetooth(Context context, boolean z) {
        SystemBtCheck systemBtCheck = this.systemBtCheck;
        if (systemBtCheck == null) {
            LogUtil.showLogE(TAG, "openBluetooth-->systemBtCheck == null");
        } else {
            systemBtCheck.openBluetooth(context, z);
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth3.IBTManager
    public void closeBluetooth() {
        SystemBtCheck systemBtCheck = this.systemBtCheck;
        if (systemBtCheck == null) {
            LogUtil.showLogE(TAG, "closeBluetooth-->systemBtCheck == null");
        } else {
            systemBtCheck.closeBluetooth();
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth3.IBTManager
    public void unRegisterBluetoothReceiver(Context context) {
        BluetoothBroadcastReceiver bluetoothBroadcastReceiver = this.bluetoothBroadcastReceiver;
        if (bluetoothBroadcastReceiver != null) {
            context.unregisterReceiver(bluetoothBroadcastReceiver);
            this.bluetoothBroadcastReceiver = null;
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth3.IBTManager
    public BluetoothAdapter getBluetoothAdapter() {
        BluetoothAdapter bluetoothAdapter = this.bluetooth3Adapter;
        if (bluetoothAdapter != null) {
            return bluetoothAdapter;
        }
        return null;
    }

    private void initStartDiscoveryDevice(OnDeviceSearchListener onDeviceSearchListener) {
        BluetoothBroadcastReceiver bluetoothBroadcastReceiver = this.bluetoothBroadcastReceiver;
        if (bluetoothBroadcastReceiver == null) {
            LogUtil.showLogE(TAG, "initStartDiscovery-->bluetoothBroadcastReceiver == null");
            return;
        }
        this.onDeviceSearchListener = onDeviceSearchListener;
        bluetoothBroadcastReceiver.setOnDeviceSearchListener(onDeviceSearchListener);
        BluetoothAdapter bluetoothAdapter = this.bluetooth3Adapter;
        if (bluetoothAdapter == null) {
            LogUtil.showLogE(TAG, "initStartDiscovery-->bluetooth3Adapter == null");
        } else if (bluetoothAdapter.isDiscovering()) {
            LogUtil.showLogE(TAG, "initStartDiscovery-->正在扫描中...");
        } else {
            LogUtil.showLogD(TAG, "开始扫描设备");
            this.bluetooth3Adapter.startDiscovery();
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth3.IBTManager
    public void startDiscoveryDevice(OnDeviceSearchListener onDeviceSearchListener) {
        initStartDiscoveryDevice(onDeviceSearchListener);
    }

    @Override // com.example.bluetoothlibrary.bluetooth3.IBTManager
    public void startDiscoveryDevice(OnDeviceSearchListener onDeviceSearchListener, long j) {
        initStartDiscoveryDevice(onDeviceSearchListener);
        this.mHandler.postDelayed(this.stopScanRunnable, j);
    }

    @Override // com.example.bluetoothlibrary.bluetooth3.IBTManager
    public void stopDiscoveryDevice() {
        this.mHandler.removeCallbacks(this.stopScanRunnable);
        cancelDiscoveryDevice();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cancelDiscoveryDevice() {
        BluetoothAdapter bluetoothAdapter = this.bluetooth3Adapter;
        if (bluetoothAdapter == null) {
            LogUtil.showLogE(TAG, "cancelDiscoveryDevice-->bluetooth3Adapter == null");
        } else if (!bluetoothAdapter.isDiscovering()) {
            LogUtil.showLogD(TAG, "cancelDiscoveryDevice-->已停止扫描");
        } else {
            LogUtil.showLogD(TAG, "停止扫描设备");
            this.bluetooth3Adapter.cancelDiscovery();
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth3.IBTManager
    public List<BluetoothDevice> getBoundDeviceList() {
        if (this.bluetooth3Adapter == null) {
            LogUtil.showLogE(TAG, "getBoundDeviceList-->bluetooth3Adapter == null");
            return null;
        }
        return new ArrayList(this.bluetooth3Adapter.getBondedDevices());
    }

    @Override // com.example.bluetoothlibrary.bluetooth3.IBTManager
    public boolean boundDevice(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice == null) {
            LogUtil.showLogE(TAG, "boundDevice-->bluetoothDevice == null");
            return false;
        }
        try {
            return ClsUtils.createBond(BluetoothDevice.class, bluetoothDevice);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth3.IBTManager
    public boolean boundDeviceAPI(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice == null) {
            LogUtil.showLogE(TAG, "boundDeviceAPI-->bluetoothDevice == null");
            return false;
        }
        return bluetoothDevice.createBond();
    }

    @Override // com.example.bluetoothlibrary.bluetooth3.IBTManager
    public boolean disBoundDevice(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice == null) {
            LogUtil.showLogE(TAG, "disBoundDevice-->bluetoothDevice == null");
            return false;
        }
        try {
            return ClsUtils.removeBond(BluetoothDevice.class, bluetoothDevice);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    @Override // com.example.bluetoothlibrary.bluetooth3.IBTManager
    public boolean getBluetoothState() {
        BluetoothAdapter bluetoothAdapter = this.bluetooth3Adapter;
        if (bluetoothAdapter == null) {
            LogUtil.showLogE(TAG, "getBluetoothState-->bluetooth3Adapter == null");
            return false;
        }
        return bluetoothAdapter.isEnabled();
    }

    @Override // com.example.bluetoothlibrary.bluetooth3.IBTManager
    public boolean getDeviceBoundState(BluetoothDevice bluetoothDevice) {
        if (bluetoothDevice == null) {
            LogUtil.showLogE(TAG, "getDeviceBoundState-->bluetoothDevice == null");
            return false;
        }
        Iterator<BluetoothDevice> it = getBoundDeviceList().iterator();
        while (it.hasNext()) {
            if (it.next().getAddress().equals(bluetoothDevice.getAddress())) {
                return true;
            }
        }
        return false;
    }

    @Override // com.example.bluetoothlibrary.bluetooth3.IBTManager
    public void startConnectDevice(boolean z, final BluetoothDevice bluetoothDevice, String str, int i, boolean z2, long j, final OnBTConnectListener onBTConnectListener) {
        if (bluetoothDevice == null) {
            LogUtil.showLogE(TAG, "startConnectDevice-->bluetoothDevice == null");
            return;
        }
        BluetoothAdapter bluetoothAdapter = this.bluetooth3Adapter;
        if (bluetoothAdapter == null) {
            LogUtil.showLogE(TAG, "startConnectDevice-->bluetooth3Adapter == null");
            return;
        }
        if (bluetoothAdapter.isDiscovering()) {
            this.bluetooth3Adapter.cancelDiscovery();
        }
        this.curConnState = false;
        this.curConnDevice = bluetoothDevice;
        this.onBTConnectListener = onBTConnectListener;
        ConnectThread connectThread = new ConnectThread(this.bluetooth3Adapter, this.curConnDevice, z, str, i, z2);
        this.connectThread = connectThread;
        connectThread.setOnBluetoothConnectListener(new ConnectThread.OnBluetoothConnectListener() { // from class: com.example.bluetoothlibrary.bluetooth3.BTManager.3
            @Override // com.example.bluetoothlibrary.bluetooth3.bt.ConnectThread.OnBluetoothConnectListener
            public void onStartConn() {
                LogUtil.showLogD(BTManager.TAG, "startConnectDevice-->开始连接..." + bluetoothDevice.getName() + "-->" + bluetoothDevice.getAddress());
                OnBTConnectListener onBTConnectListener2 = onBTConnectListener;
                if (onBTConnectListener2 != null) {
                    onBTConnectListener2.onStartConnect();
                }
            }

            @Override // com.example.bluetoothlibrary.bluetooth3.bt.ConnectThread.OnBluetoothConnectListener
            public void onConnSuccess(BluetoothSocket bluetoothSocket) {
                BTManager.this.mHandler.removeCallbacks(BTManager.this.connectOutTimeRunnable);
                LogUtil.showLogD(BTManager.TAG, "startConnectDevice-->移除连接超时");
                LogUtil.showLogW(BTManager.TAG, "startConnectDevice-->连接成功");
                OnBTConnectListener onBTConnectListener2 = onBTConnectListener;
                if (onBTConnectListener2 != null) {
                    onBTConnectListener2.onConnectSuccess();
                }
                BTManager.this.curConnState = true;
                BTManager.this.managerConnectSendReceiveData(bluetoothSocket);
            }

            @Override // com.example.bluetoothlibrary.bluetooth3.bt.ConnectThread.OnBluetoothConnectListener
            public void onConnFailure(String str2) {
                LogUtil.showLogE(BTManager.TAG, "startConnectDevice-->" + str2);
                OnBTConnectListener onBTConnectListener2 = onBTConnectListener;
                if (onBTConnectListener2 != null) {
                    onBTConnectListener2.onConnectFailure();
                }
                BTManager.this.curConnState = false;
                BTManager.this.clearConnectedThread();
            }
        });
        this.connectThread.start();
        this.mHandler.postDelayed(this.connectOutTimeRunnable, j);
    }

    @Override // com.example.bluetoothlibrary.bluetooth3.IBTManager
    public void clearConnectedThread() {
        LogUtil.showLogD(TAG, "clearConnectedThread-->即将断开");
        ConnectedThread connectedThread = this.connectedThread;
        if (connectedThread == null) {
            LogUtil.showLogE(TAG, "clearConnectedThread-->connectedThread == null");
            return;
        }
        connectedThread.terminalClose(this.connectThread);
        this.mHandler.postDelayed(new Runnable() { // from class: com.example.bluetoothlibrary.bluetooth3.BTManager.5
            @Override // java.lang.Runnable
            public void run() {
                BTManager.this.connectedThread.cancel();
                BTManager.this.connectedThread = null;
            }
        }, 10L);
        LogUtil.showLogW(TAG, "clearConnectedThread-->成功断开连接");
        OnBTConnectListener onBTConnectListener = this.onBTConnectListener;
        if (onBTConnectListener != null) {
            onBTConnectListener.onDisConnectSuccess();
        }
    }

    public void managerConnectSendReceiveData(BluetoothSocket bluetoothSocket) {
        ConnectedThread connectedThread = new ConnectedThread(bluetoothSocket);
        this.connectedThread = connectedThread;
        connectedThread.start();
        this.connectedThread.setOnSendReceiveDataListener(new ConnectedThread.OnSendReceiveDataListener() { // from class: com.example.bluetoothlibrary.bluetooth3.BTManager.6
            @Override // com.example.bluetoothlibrary.bluetooth3.bt.ConnectedThread.OnSendReceiveDataListener
            public void onSendDataSuccess(byte[] bArr) {
                LogUtil.showLogW(BTManager.TAG, "发送数据成功,长度" + bArr.length + "->" + TypeConversion.bytes2HexString(bArr, bArr.length));
                if (BTManager.this.onBTConnectListener != null) {
                    BTManager.this.onBTConnectListener.onSendSuccess(bArr);
                }
            }

            @Override // com.example.bluetoothlibrary.bluetooth3.bt.ConnectedThread.OnSendReceiveDataListener
            public void onSendDataError(byte[] bArr, String str) {
                LogUtil.showLogE(BTManager.TAG, "发送数据出错,长度" + bArr.length + "->" + TypeConversion.bytes2HexString(bArr, bArr.length));
                if (BTManager.this.onBTConnectListener != null) {
                    BTManager.this.onBTConnectListener.onSendError(bArr, str);
                }
            }

            @Override // com.example.bluetoothlibrary.bluetooth3.bt.ConnectedThread.OnSendReceiveDataListener
            public void onReceiveDataSuccess(byte[] bArr) {
                LogUtil.showLogW(BTManager.TAG, "成功接收数据,长度" + bArr.length + "->" + TypeConversion.bytes2HexString(bArr, bArr.length));
                if (BTManager.this.onBTConnectListener != null) {
                    BTManager.this.onBTConnectListener.onReceiveSuccess(bArr);
                }
            }

            @Override // com.example.bluetoothlibrary.bluetooth3.bt.ConnectedThread.OnSendReceiveDataListener
            public void onReceiveDataError(String str) {
                LogUtil.showLogE(BTManager.TAG, "接收数据出错：" + str);
                if (BTManager.this.onBTConnectListener != null) {
                    BTManager.this.onBTConnectListener.onReceiveError(str);
                }
            }
        });
    }

    @Override // com.example.bluetoothlibrary.bluetooth3.IBTManager
    public boolean sendData(String str, boolean z) {
        if (this.connectedThread == null) {
            LogUtil.showLogE(TAG, "sendData:string -->connectedThread == null");
            return false;
        }
        if (str == null || str.length() == 0) {
            LogUtil.showLogE(TAG, "sendData:string-->要发送的数据为空");
            return false;
        }
        if (z) {
            str.replace(" ", "");
            if (str.length() % 2 != 0) {
                str = str.substring(0, str.length() - 2) + ("0" + str.charAt(str.length() - 1));
            }
            LogUtil.showLogD(TAG, "sendData:string -->准备写入：" + FormatConversion.addStringSpace(str));
            return this.connectedThread.write(TypeConversion.hexString2Bytes(str));
        }
        LogUtil.showLogD(TAG, "sendData:string -->准备写入：" + str);
        return this.connectedThread.write(str.getBytes());
    }

    @Override // com.example.bluetoothlibrary.bluetooth3.IBTManager
    public boolean sendData(byte[] bArr) {
        if (this.connectedThread == null) {
            LogUtil.showLogE(TAG, "sendData:byte[]-->connectedThread == null");
            return false;
        }
        if (bArr == null || bArr.length == 0) {
            LogUtil.showLogE(TAG, "sendData:byte[]-->要发送的数据为空");
            return false;
        }
        LogUtil.showLogD(TAG, "sendData:byte[] -->准备写入：" + TypeConversion.bytes2HexString(bArr, bArr.length));
        return this.connectedThread.write(bArr);
    }

    @Override // com.example.bluetoothlibrary.bluetooth3.IBTManager
    public BluetoothDevice getDeviceByAddress(String str) {
        if (str == null || str.equals("")) {
            LogUtil.showLogE(TAG, "getDeviceByAddress-->macAddress == null");
            return null;
        }
        if (this.bluetooth3Adapter == null) {
            this.bluetooth3Adapter = SystemBtCheck.getInstance().bluetooth3Adapter;
        }
        return this.bluetooth3Adapter.getRemoteDevice(str);
    }
}