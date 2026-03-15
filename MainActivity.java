package com.smadom.cpbuddlets;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.bluetoothlibrary.bluetooth.BTBluetooth;
import com.example.bluetoothlibrary.bluetooth.OnConnectListener;
import com.example.bluetoothlibrary.bluetooth.OnSearchListener;
import com.example.bluetoothlibrary.listener.OnBluetoothStateChangeListener;
import com.example.bluetoothlibrary.utils.TypeConversion;
import com.iflytek.cloud.SpeechConstant;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class MainActivity extends FlutterActivity implements RecognitionListener {
    protected static final int BACK_SCAN_DEVICE_OUT_TIME = 14;
    protected static final int BACK_SCAN_DEVICE_START = 16;
    protected static final int BACK_SCAN_DEVICE_STOP = 15;
    private static final String BLE_CHANNEL = "com.buddlets.ble/blemanager";
    protected static final int BT_CLOSED = 12;
    protected static final int BT_CLOSING = 11;
    protected static final int BT_OPENED = 10;
    protected static final int BT_OPENING = 9;
    protected static final int CONNECT_FAILURE = 2;
    protected static final int CONNECT_SUCCESS = 1;
    protected static final int DIS_CONNECT_SUCCESS = 3;
    protected static final int NEW_SCAN_DEVICE_OUT_TIME = 19;
    protected static final int NEW_SCAN_DEVICE_START = 17;
    protected static final int NEW_SCAN_DEVICE_STOP = 18;
    protected static final int RECEIVE_FAILURE = 7;
    protected static final int RECEIVE_SUCCESS = 6;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION = 123;
    private static final int REQUEST_SETTING = 100;
    private static final int REQUEST_STORAGE = 124;
    protected static final int RE_SCAN_DEST_DEVICE = 8;
    protected static final int SEND_FAILURE = 5;
    protected static final int SEND_SUCCESS = 4;
    protected static final int SPEECH_RESTART = 20;
    protected static final int SPEECH_STOP = 21;
    protected static final int VOICE_RECOGNIZE_SUCCESS = 13;
    protected static final String notifyUUID = "0000fff1-0000-1000-8000-00805f9b34fb";
    protected static final String serviceUUID = "0000fff0-0000-1000-8000-00805f9b34fb";
    protected static final String writeUUID = "0000fff2-0000-1000-8000-00805f9b34fb";
    private MethodChannel bleChannel;
    private BTBluetooth btBluetooth;
    private IFlyVoice mIFlyVoice;
    private MethodChannel.Result mStateResult;
    private Intent recognizerIntent;
    private String savePreMac;
    private SpeechRecognizer systemSpeech;
    private final String TAG = "yqy";
    private boolean isShowPermission = false;
    private boolean isCanWrite = true;
    private boolean isSearching = false;
    private boolean isConnected = false;
    private boolean isUserDisCon = false;
    private boolean isDisConPreDevice = false;
    private boolean isReScan = false;
    private boolean isCurSendSuc = true;
    private boolean isCloseBt = false;
    private boolean isCarDisCon = false;
    private int backConnectTime = 0;
    private List<BluetoothDevice> deviceList = new ArrayList();
    private BluetoothDevice curDevice = null;
    private byte[] mSendBytes = new byte[10];
    private boolean isNewData = false;
    private boolean mIsIFlyVoice = false;
    private boolean isSystemSpeechEnable = false;
    private Handler mHandler = new Handler() { // from class: com.smadom.cpbuddlets.MainActivity.1
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            switch (message.what) {
                case 1:
                    MainActivity.this.isConnected = true;
                    MainActivity mainActivity = MainActivity.this;
                    mainActivity.savePreMac = mainActivity.curDevice.getAddress();
                    Log.d("yqy", "连接成功");
                    MainActivity.this.bleChannel.invokeMethod("connected", new BleDevice(MainActivity.this.curDevice.getName(), MainActivity.this.curDevice.getAddress()).toMap());
                    break;
                case 2:
                    Log.d("yqy", "连接失败");
                    MainActivity.this.isConnected = false;
                    Log.e("yqy", ((String) message.obj) + "：" + message.arg1);
                    MainActivity.this.bleChannel.invokeMethod("statusCode", 2);
                    break;
                case 3:
                    MainActivity.this.isConnected = false;
                    Log.d("yqy", "断开成功：status = " + message.arg1);
                    MainActivity.this.isCurSendSuc = true;
                    MainActivity.this.bleChannel.invokeMethod("statusCode", 3);
                    if (MainActivity.this.isUserDisCon) {
                        MainActivity.this.isUserDisCon = false;
                    } else if (!MainActivity.this.isDisConPreDevice) {
                        if (!MainActivity.this.isCloseBt) {
                            MainActivity.this.backConnectTime++;
                            if (MainActivity.this.backConnectTime > 3) {
                                Log.d("yqy", "回连" + (MainActivity.this.backConnectTime - 1) + "次都失败");
                                MainActivity.this.backConnectTime = 0;
                                Toast.makeText(MainActivity.this, "无法与小车建立连接", 0).show();
                            } else {
                                Log.d("yqy", "即将第" + MainActivity.this.backConnectTime + "次回连");
                                MainActivity mainActivity2 = MainActivity.this;
                                mainActivity2.backConnectPreDevice(mainActivity2.savePreMac);
                            }
                        } else {
                            Log.d("yqy", "因系统蓝牙关闭导致断开，在系统蓝牙开启处作回连");
                            MainActivity.this.isCloseBt = false;
                        }
                    } else {
                        Log.d("yqy", "已连接状态下，因重新选择新设备断开，去连接新设备");
                        MainActivity.this.isDisConPreDevice = false;
                        MainActivity.this.connectCurDevice();
                    }
                    break;
                case 4:
                    Log.w("yqy", "发送成功：" + TypeConversion.bytes2HexString((byte[]) message.obj));
                    MainActivity.this.isCanWrite = true;
                    if (MainActivity.this.isNewData) {
                        MainActivity.this.sendBytes();
                    }
                    break;
                case 5:
                    Log.e("yqy", "发送失败：" + TypeConversion.bytes2HexString((byte[]) message.obj));
                    break;
                case 8:
                    if (MainActivity.this.btBluetooth != null) {
                        MainActivity.this.btBluetooth.stopDiscoveryDevice();
                        MainActivity.this.curDevice = (BluetoothDevice) message.obj;
                        Log.d("yqy", "扫描到目标设备：" + MainActivity.this.curDevice.getName() + "-->" + MainActivity.this.curDevice.getAddress());
                        MainActivity.this.isConnected = false;
                        MainActivity.this.btBluetooth.connectBLEDevice(MainActivity.serviceUUID, MainActivity.notifyUUID, MainActivity.writeUUID, MainActivity.this.curDevice, 15000L, MainActivity.this.onConnectListener);
                        break;
                    }
                    break;
                case 9:
                    Log.d("yqy", "系统蓝牙正在打开...");
                    break;
                case 10:
                    Log.d("yqy", "系统蓝牙已打开！");
                    MainActivity.this.isCloseBt = false;
                    MainActivity mainActivity3 = MainActivity.this;
                    mainActivity3.backConnectPreDevice(mainActivity3.savePreMac);
                    break;
                case 11:
                    Log.d("yqy", "系统蓝牙正在关闭...");
                    MainActivity.this.isCloseBt = true;
                    break;
                case 12:
                    Log.d("yqy", "系统蓝牙已关闭！");
                    MainActivity.this.isCloseBt = true;
                    break;
                case 14:
                    if (!MainActivity.this.isConnected) {
                        if (MainActivity.this.deviceList.size() == 0) {
                            MainActivity mainActivity4 = MainActivity.this;
                            Toast.makeText(mainActivity4, mainActivity4.getString(R.string.txt_scan_fail), 1).show();
                        } else {
                            MainActivity mainActivity5 = MainActivity.this;
                            Toast.makeText(mainActivity5, mainActivity5.getString(R.string.txt_scan_out_time), 1).show();
                        }
                    }
                    MainActivity.this.bleChannel.invokeMethod("statusCode", 14);
                    break;
                case 15:
                case 16:
                case 17:
                case 18:
                    MainActivity.this.bleChannel.invokeMethod("statusCode", Integer.valueOf(message.what));
                    break;
                case 19:
                    if (!MainActivity.this.isConnected) {
                        if (MainActivity.this.deviceList.size() == 0) {
                            MainActivity mainActivity6 = MainActivity.this;
                            Toast.makeText(mainActivity6, mainActivity6.getString(R.string.txt_scan_fail), 1).show();
                        } else {
                            MainActivity mainActivity7 = MainActivity.this;
                            Toast.makeText(mainActivity7, mainActivity7.getString(R.string.txt_scan_out_time), 1).show();
                        }
                    }
                    MainActivity.this.bleChannel.invokeMethod("statusCode", 19);
                    break;
                case 20:
                    if (MainActivity.this.isSystemSpeechEnable && !MainActivity.this.mIsIFlyVoice && MainActivity.this.systemSpeech != null) {
                        MainActivity.this.systemSpeech.stopListening();
                        MainActivity.this.systemSpeech.cancel();
                        MainActivity.this.systemSpeech.destroy();
                        MainActivity mainActivity8 = MainActivity.this;
                        mainActivity8.systemSpeech = SpeechRecognizer.createSpeechRecognizer(mainActivity8);
                        MainActivity.this.systemSpeech.setRecognitionListener(MainActivity.this);
                        MainActivity.this.systemSpeech.startListening(MainActivity.this.recognizerIntent);
                        break;
                    }
                    break;
            }
        }
    };
    private OnBluetoothStateChangeListener onBluetoothStateChangeListener = new OnBluetoothStateChangeListener() { // from class: com.smadom.cpbuddlets.MainActivity.4
        @Override // com.example.bluetoothlibrary.listener.OnBluetoothStateChangeListener
        public void onBluetoothClose() {
        }

        @Override // com.example.bluetoothlibrary.listener.OnBluetoothStateChangeListener
        public void onBluetoothOpen() {
        }

        @Override // com.example.bluetoothlibrary.listener.OnBluetoothStateChangeListener
        public void onBluetoothOpening() {
        }

        @Override // com.example.bluetoothlibrary.listener.OnBluetoothStateChangeListener
        public void onBluetoothClosing() {
            MainActivity.this.isCloseBt = true;
        }
    };
    private OnSearchListener onBackSearchListener = new OnSearchListener() { // from class: com.smadom.cpbuddlets.MainActivity.5
        @Override // com.example.bluetoothlibrary.bluetooth.OnSearchListener
        public void onDiscoveryStart() {
            Log.d("yqy", "开始扫描...");
            Message message = new Message();
            message.what = 16;
            MainActivity.this.mHandler.sendMessage(message);
            MainActivity.this.isSearching = true;
        }

        @Override // com.example.bluetoothlibrary.bluetooth.OnSearchListener
        public void onDiscoveryStop() {
            Log.d("yqy", "已停止扫描");
            MainActivity.this.isSearching = false;
            Log.d("yqy", "扫描超时");
            Message message = new Message();
            message.what = 15;
            MainActivity.this.mHandler.sendMessage(message);
        }

        @Override // com.example.bluetoothlibrary.bluetooth.OnSearchListener
        public void onDeviceFound(BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
            Log.e("yqy", "回连扫描到设备：" + bluetoothDevice.getName() + "-->" + bluetoothDevice.getAddress());
            if (!bluetoothDevice.getAddress().equals(MainActivity.this.savePreMac) || MainActivity.this.btBluetooth == null) {
                return;
            }
            MainActivity.this.isSearching = false;
            MainActivity.this.btBluetooth.stopDiscoveryDevice();
            MainActivity.this.curDevice = bluetoothDevice;
            Log.d("yqy", "扫描到目标设备：" + MainActivity.this.curDevice.getName() + "-->" + MainActivity.this.curDevice.getAddress());
            MainActivity.this.isConnected = false;
            MainActivity.this.btBluetooth.connectBLEDevice(MainActivity.serviceUUID, MainActivity.notifyUUID, MainActivity.writeUUID, MainActivity.this.curDevice, 15000L, MainActivity.this.onConnectListener);
        }

        @Override // com.example.bluetoothlibrary.bluetooth.OnSearchListener
        public void onDeviceSearchOutTime() {
            MainActivity.this.isSearching = false;
            Log.d("yqy", "扫描超时");
            Message message = new Message();
            message.what = 14;
            MainActivity.this.mHandler.sendMessage(message);
        }
    };
    private OnSearchListener onNewSearchListener = new OnSearchListener() { // from class: com.smadom.cpbuddlets.MainActivity.6
        @Override // com.example.bluetoothlibrary.bluetooth.OnSearchListener
        public void onDiscoveryStart() {
            Message message = new Message();
            message.what = 17;
            MainActivity.this.mHandler.sendMessage(message);
            MainActivity.this.isSearching = true;
        }

        @Override // com.example.bluetoothlibrary.bluetooth.OnSearchListener
        public void onDiscoveryStop() {
            Message message = new Message();
            message.what = 18;
            MainActivity.this.mHandler.sendMessage(message);
            MainActivity.this.isSearching = false;
        }

        @Override // com.example.bluetoothlibrary.bluetooth.OnSearchListener
        public void onDeviceFound(BluetoothDevice bluetoothDevice, int i, byte[] bArr) {
            Log.d("yqy", "搜索到设备：" + bluetoothDevice.getName() + "-->" + bluetoothDevice.getAddress());
            if (bluetoothDevice.getName() == null || MainActivity.this.deviceList.contains(bluetoothDevice)) {
                return;
            }
            MainActivity.this.deviceList.add(bluetoothDevice);
            MainActivity.this.sendDeviceList();
        }

        @Override // com.example.bluetoothlibrary.bluetooth.OnSearchListener
        public void onDeviceSearchOutTime() {
            Message message = new Message();
            message.what = 19;
            MainActivity.this.mHandler.sendMessage(message);
            MainActivity.this.isSearching = false;
        }
    };
    private OnConnectListener onConnectListener = new OnConnectListener() { // from class: com.smadom.cpbuddlets.MainActivity.7
        @Override // com.example.bluetoothlibrary.bluetooth.OnConnectListener
        public void onMTUSetFailure(String str) {
        }

        @Override // com.example.bluetoothlibrary.bluetooth.OnConnectListener
        public void onMTUSetSuccess(String str, int i) {
        }

        @Override // com.example.bluetoothlibrary.bluetooth.OnConnectListener
        public void onStartConnect() {
        }

        @Override // com.example.bluetoothlibrary.bluetooth.OnConnectListener
        public void onConnectSuccess() {
            MainActivity.this.isCanWrite = true;
            Message message = new Message();
            message.what = 1;
            MainActivity.this.mHandler.sendMessage(message);
        }

        @Override // com.example.bluetoothlibrary.bluetooth.OnConnectListener
        public void onConnectFailure(String str, int i) {
            MainActivity.this.isCanWrite = false;
            Message message = new Message();
            message.what = 2;
            message.obj = str;
            message.arg1 = i;
            MainActivity.this.mHandler.sendMessage(message);
        }

        @Override // com.example.bluetoothlibrary.bluetooth.OnConnectListener
        public void onDisConnectSuccess(int i) {
            MainActivity.this.isCanWrite = false;
            Message message = new Message();
            message.what = 3;
            message.arg1 = i;
            MainActivity.this.mHandler.sendMessage(message);
        }

        @Override // com.example.bluetoothlibrary.bluetooth.OnConnectListener
        public void onSendSuccess(byte[] bArr) {
            Log.e("yqy", "send success");
            Message message = new Message();
            message.what = 4;
            message.obj = bArr;
            MainActivity.this.mHandler.sendMessage(message);
        }

        @Override // com.example.bluetoothlibrary.bluetooth.OnConnectListener
        public void onSendError(byte[] bArr, String str) {
            Log.e("yqy", "send error");
            MainActivity.this.isCanWrite = true;
            Message message = new Message();
            message.what = 5;
            message.obj = bArr;
            MainActivity.this.mHandler.sendMessage(message);
        }

        @Override // com.example.bluetoothlibrary.bluetooth.OnConnectListener
        public void onReceiveSuccess(BluetoothGattCharacteristic bluetoothGattCharacteristic, byte[] bArr) {
            Message message = new Message();
            message.what = 6;
            message.obj = bArr;
            MainActivity.this.mHandler.sendMessage(message);
        }

        @Override // com.example.bluetoothlibrary.bluetooth.OnConnectListener
        public void onReceiveError(String str) {
            Message message = new Message();
            message.what = 7;
            message.obj = str;
            MainActivity.this.mHandler.sendMessage(message);
        }
    };

    @Override // android.speech.RecognitionListener
    public void onRmsChanged(float f) {
    }

    @Override // io.flutter.embedding.android.FlutterActivity, io.flutter.embedding.android.FlutterActivityAndFragmentDelegate.Host, io.flutter.embedding.android.FlutterEngineConfigurator
    public void configureFlutterEngine(FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        MethodChannel methodChannel = new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), BLE_CHANNEL);
        this.bleChannel = methodChannel;
        methodChannel.setMethodCallHandler(new MethodChannel.MethodCallHandler() { // from class: com.smadom.cpbuddlets.MainActivity.2
            @Override // io.flutter.plugin.common.MethodChannel.MethodCallHandler
            public void onMethodCall(MethodCall methodCall, MethodChannel.Result result) {
                try {
                    if (methodCall.method.equals("getBleState")) {
                        MainActivity.this.mStateResult = result;
                        return;
                    }
                    if (methodCall.method.equals("sendBytes")) {
                        for (int i = 0; i < 10; i++) {
                            if (methodCall.hasArgument("byte" + i)) {
                                MainActivity.this.mSendBytes[i] = Integer.valueOf(((Integer) methodCall.argument("byte" + i)).intValue()).byteValue();
                            } else {
                                MainActivity.this.mSendBytes[i] = 0;
                            }
                        }
                        MainActivity.this.isNewData = true;
                        MainActivity.this.sendBytes();
                        result.success(1);
                        return;
                    }
                    if (methodCall.method.equals("startScan")) {
                        result.success(Integer.valueOf(MainActivity.this.refreshScan()));
                        return;
                    }
                    if (methodCall.method.equals("stopScan")) {
                        if (MainActivity.this.btBluetooth != null) {
                            MainActivity.this.btBluetooth.stopDiscoveryDevice();
                        }
                        MainActivity.this.isSearching = false;
                        result.success(1);
                        return;
                    }
                    if (methodCall.method.equals("backConnect")) {
                        if (methodCall.hasArgument("address")) {
                            result.success(Integer.valueOf(MainActivity.this.backConnectPreDevice((String) methodCall.argument("address"))));
                            return;
                        } else {
                            result.success(0);
                            return;
                        }
                    }
                    if (methodCall.method.equals("refreshScan")) {
                        result.success(Integer.valueOf(MainActivity.this.refreshScan()));
                        return;
                    }
                    if (methodCall.method.equals("connectNew")) {
                        if (methodCall.hasArgument("index")) {
                            int iIntValue = ((Integer) methodCall.argument("index")).intValue();
                            if (iIntValue < MainActivity.this.deviceList.size()) {
                                result.success(Integer.valueOf(MainActivity.this.startConnect((BluetoothDevice) MainActivity.this.deviceList.get(iIntValue))));
                                return;
                            } else {
                                result.success(0);
                                return;
                            }
                        }
                        result.success(0);
                        return;
                    }
                    if (methodCall.method.equals("stopConnect")) {
                        MainActivity.this.isUserDisCon = true;
                        MainActivity.this.disConCurDevice();
                        return;
                    }
                    if (methodCall.method.equals("voiceType")) {
                        result.success(Integer.valueOf(MainActivity.this.mIsIFlyVoice ? 0 : 1));
                        return;
                    }
                    if (methodCall.method.equals("startVoice")) {
                        if (MainActivity.this.mIsIFlyVoice) {
                            if (MainActivity.this.mIFlyVoice != null) {
                                if (methodCall.hasArgument("continue")) {
                                    MainActivity.this.mIFlyVoice.isKeepVoiceRecognize = ((Integer) methodCall.argument("continue")).intValue() > 0;
                                } else {
                                    MainActivity.this.mIFlyVoice.isKeepVoiceRecognize = false;
                                }
                                if (MainActivity.this.mIFlyVoice.isSpeechRecognizeInitSuc) {
                                    MainActivity.this.mIFlyVoice.startVoiceRecognize();
                                    result.success(1);
                                    return;
                                } else {
                                    result.success(0);
                                    return;
                                }
                            }
                            return;
                        }
                        if (MainActivity.this.systemSpeech != null) {
                            Locale locale = Locale.US;
                            if (methodCall.hasArgument(SpeechConstant.TYPE_LOCAL) && methodCall.argument(SpeechConstant.TYPE_LOCAL).equals("cn")) {
                                locale = Locale.CHINESE;
                            }
                            MainActivity.this.muteStream();
                            MainActivity.this.isSystemSpeechEnable = true;
                            MainActivity.this.recognizerIntent.putExtra("android.speech.extra.LANGUAGE", locale);
                            MainActivity.this.systemSpeech.startListening(MainActivity.this.recognizerIntent);
                            result.success(2);
                            return;
                        }
                        result.success(0);
                        return;
                    }
                    if (methodCall.method.equals("stopVoice")) {
                        if (MainActivity.this.mIsIFlyVoice) {
                            if (MainActivity.this.mIFlyVoice != null) {
                                MainActivity.this.mIFlyVoice.stopVoiceRecognize();
                                return;
                            }
                            return;
                        } else {
                            MainActivity.this.isSystemSpeechEnable = false;
                            MainActivity.this.systemSpeech.stopListening();
                            MainActivity.this.systemSpeech.cancel();
                            MainActivity.this.unMuteStream();
                            return;
                        }
                    }
                    result.notImplemented();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (Build.VERSION.SDK_INT >= 28) {
            closeAndroidPDialog();
        }
        YCInitData();
        initPermissions();
        initVoice();
    }

    @Override // io.flutter.embedding.android.FlutterActivity, android.app.Activity
    public void onStart() {
        super.onStart();
    }

    @Override // io.flutter.embedding.android.FlutterActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        if (this.mIsIFlyVoice) {
            IFlyVoice iFlyVoice = this.mIFlyVoice;
            if (iFlyVoice != null) {
                iFlyVoice.stopVoiceRecognize();
                return;
            }
            return;
        }
        this.isSystemSpeechEnable = false;
        if (this.systemSpeech != null) {
            unMuteStream();
            this.systemSpeech.stopListening();
            this.systemSpeech.cancel();
        }
    }

    @Override // io.flutter.embedding.android.FlutterActivity, android.app.Activity
    public void onResume() {
        super.onResume();
    }

    @Override // io.flutter.embedding.android.FlutterActivity, android.app.Activity
    public void onDestroy() {
        BTBluetooth bTBluetooth = this.btBluetooth;
        if (bTBluetooth != null) {
            if (this.isSearching) {
                bTBluetooth.stopDiscoveryDevice();
            }
            if (this.isConnected) {
                disConCurDevice();
            }
        }
        SpeechRecognizer speechRecognizer = this.systemSpeech;
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        super.onDestroy();
    }

    private void closeAndroidPDialog() {
        try {
            Class.forName("android.content.pm.PackageParser$Package").getDeclaredConstructor(String.class).setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Class<?> cls = Class.forName("android.app.ActivityThread");
            Method declaredMethod = cls.getDeclaredMethod("currentActivityThread", new Class[0]);
            declaredMethod.setAccessible(true);
            Object objInvoke = declaredMethod.invoke(null, new Object[0]);
            Field declaredField = cls.getDeclaredField("mHiddenApiWarningShown");
            declaredField.setAccessible(true);
            declaredField.setBoolean(objInvoke, true);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private void askBluetoothPermission() {
        this.isShowPermission = true;
        startActivityForResult(new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE"), 1);
    }

    @Override // io.flutter.embedding.android.FlutterActivity, android.app.Activity
    protected void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1 && i == 1) {
            this.isShowPermission = false;
            String str = this.savePreMac;
            if (str == null || str.isEmpty()) {
                return;
            }
            backConnectPreDevice(this.savePreMac);
        }
    }

    private void initPermissions() {
        String[] strArr = {"android.permission.BLUETOOTH", "android.permission.RECORD_AUDIO", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"};
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < 4; i++) {
            String str = strArr[i];
            if (ContextCompat.checkSelfPermission(this, str) != 0) {
                arrayList.add(str);
            }
        }
        String[] strArr2 = new String[arrayList.size()];
        if (arrayList.isEmpty()) {
            return;
        }
        ActivityCompat.requestPermissions(this, (String[]) arrayList.toArray(strArr2), REQUEST_LOCATION);
    }

    private boolean storagePermission() {
        String[] strArr = {"android.permission.READ_EXTERNAL_STORAGE"};
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < 1; i++) {
            String str = strArr[i];
            if (ContextCompat.checkSelfPermission(this, str) != 0) {
                arrayList.add(str);
            }
        }
        String[] strArr2 = new String[arrayList.size()];
        if (arrayList.isEmpty()) {
            return true;
        }
        ActivityCompat.requestPermissions(this, (String[]) arrayList.toArray(strArr2), REQUEST_STORAGE);
        return false;
    }

    @Override // io.flutter.embedding.android.FlutterActivity, android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == REQUEST_LOCATION) {
            if (iArr.length <= 0 || iArr[0] != 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setPositiveButton(R.string.txt_continue, new DialogInterface.OnClickListener() { // from class: com.smadom.cpbuddlets.MainActivity.3
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialogInterface, int i2) {
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.fromParts("package", MainActivity.this.getPackageName(), null));
                        MainActivity.this.startActivity(intent);
                    }
                });
                builder.setNegativeButton(R.string.txt_refuse, (DialogInterface.OnClickListener) null);
                builder.setMessage(R.string.msg_location_permission);
                builder.setTitle(R.string.title_location_permission);
                builder.create().show();
            }
        }
    }

    void initVoice() {
        boolean z;
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            String string = Settings.Secure.getString(getContentResolver(), "voice_recognition_service");
            Log.e("yqy", "===========voice_recognition_service:  " + string);
            if (TextUtils.isEmpty(string)) {
                this.mIsIFlyVoice = true;
            } else {
                ComponentName componentNameUnflattenFromString = ComponentName.unflattenFromString(string);
                if (componentNameUnflattenFromString == null) {
                    Log.e("yqy", "voice_recognition_service component == null");
                    this.mIsIFlyVoice = true;
                } else {
                    ComponentName componentName = null;
                    List<ResolveInfo> listQueryIntentServices = getPackageManager().queryIntentServices(new Intent("android.speech.RecognitionService"), 0);
                    if (listQueryIntentServices.size() != 0) {
                        Iterator<ResolveInfo> it = listQueryIntentServices.iterator();
                        while (true) {
                            if (!it.hasNext()) {
                                z = false;
                                break;
                            }
                            ResolveInfo next = it.next();
                            Log.e("yqy", "\t" + ((Object) next.loadLabel(getPackageManager())) + ": " + next.serviceInfo.packageName + "/" + next.serviceInfo.name);
                            if (next.serviceInfo.packageName.equals(componentNameUnflattenFromString.getPackageName())) {
                                z = true;
                                break;
                            }
                            componentName = new ComponentName(next.serviceInfo.packageName, next.serviceInfo.name);
                        }
                        if (z) {
                            this.systemSpeech = SpeechRecognizer.createSpeechRecognizer(this);
                            Log.e("yqy", "当前系统内置语音识别服务可用");
                        } else {
                            this.systemSpeech = SpeechRecognizer.createSpeechRecognizer(this, componentName);
                            Log.e("yqy", "内置不可用，需要我们使用查找到的可用的");
                        }
                        if (this.systemSpeech != null) {
                            Log.e("yqy", "使用系统语音识别");
                            this.mIsIFlyVoice = false;
                            this.systemSpeech.setRecognitionListener(this);
                            Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
                            this.recognizerIntent = intent;
                            intent.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
                            this.recognizerIntent.putExtra("android.speech.extra.PARTIAL_RESULTS", true);
                            this.recognizerIntent.putExtra("android.speech.extra.MAX_RESULTS", 3);
                        } else {
                            Log.e("yqy", "使用讯飞语音识别");
                            this.mIsIFlyVoice = true;
                            this.mIFlyVoice = new IFlyVoice(this, this.bleChannel);
                        }
                    } else {
                        this.mIsIFlyVoice = true;
                    }
                }
            }
        } else {
            this.mIsIFlyVoice = true;
            Log.e("yqy", "===========SpeechRecognizer.isRecognitionAvailable :  false==========");
        }
        if (this.mIsIFlyVoice) {
            Log.e("yqy", "No recognition services installed");
            Log.e("yqy", "使用讯飞语音识别");
            this.mIsIFlyVoice = true;
            this.mIFlyVoice = new IFlyVoice(this, this.bleChannel);
        }
    }

    void muteStream() {
        AudioManager audioManager = (AudioManager) getSystemService("audio");
        if (audioManager != null) {
            if (Build.VERSION.SDK_INT >= 23) {
                audioManager.adjustStreamVolume(3, -100, 0);
            } else {
                audioManager.setStreamMute(3, true);
            }
        }
    }

    void unMuteStream() {
        AudioManager audioManager = (AudioManager) getSystemService("audio");
        if (audioManager != null) {
            if (Build.VERSION.SDK_INT >= 23) {
                audioManager.adjustStreamVolume(3, 100, 0);
            } else {
                audioManager.setStreamMute(3, false);
            }
        }
    }

    void initVoice3() {
        SpeechRecognizer speechRecognizerCreateSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, ComponentName.unflattenFromString("com.google.android.googlequicksearchbox/com.google.android.voicesearch.serviceapi.GoogleRecognitionService"));
        this.systemSpeech = speechRecognizerCreateSpeechRecognizer;
        if (speechRecognizerCreateSpeechRecognizer != null) {
            Log.e("yqy", "使用系统语音识别");
            this.mIsIFlyVoice = false;
            this.systemSpeech.setRecognitionListener(this);
            Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
            this.recognizerIntent = intent;
            intent.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
            this.recognizerIntent.putExtra("android.speech.extra.PARTIAL_RESULTS", true);
            this.recognizerIntent.putExtra("android.speech.extra.MAX_RESULTS", 3);
            return;
        }
        Log.e("yqy", "使用讯飞语音识别");
        this.mIsIFlyVoice = true;
        this.mIFlyVoice = new IFlyVoice(this, this.bleChannel);
    }

    void YCInitData() {
        this.isDisConPreDevice = false;
        this.isCloseBt = false;
        this.isCurSendSuc = true;
        this.isCarDisCon = false;
        this.backConnectTime = 0;
        BTBluetooth bTBluetooth = BTBluetooth.getInstance();
        this.btBluetooth = bTBluetooth;
        bTBluetooth.init(this, true);
        this.btBluetooth.setOnBluetoothStateChangeListener(this.onBluetoothStateChangeListener);
    }

    int refreshScan() {
        BTBluetooth bTBluetooth = this.btBluetooth;
        if (bTBluetooth == null) {
            return 0;
        }
        if (!bTBluetooth.getBluetoothState()) {
            if (!this.isShowPermission) {
                askBluetoothPermission();
            }
            return 0;
        }
        this.btBluetooth.stopDiscoveryDevice();
        this.deviceList.clear();
        this.isSearching = true;
        this.btBluetooth.discoveryDevice(this.onNewSearchListener, 30000L);
        return 1;
    }

    void sendDeviceList() {
        ArrayList arrayList = new ArrayList();
        for (BluetoothDevice bluetoothDevice : this.deviceList) {
            arrayList.add(new BleDevice(bluetoothDevice.getName(), bluetoothDevice.getAddress()).toMap());
        }
        this.bleChannel.invokeMethod("deviceList", arrayList);
    }

    public boolean isLocationEnabled() {
        if (Build.VERSION.SDK_INT >= 19) {
            try {
                return Settings.Secure.getInt(getContentResolver(), "location_mode") != 0;
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
        }
        return !TextUtils.isEmpty(Settings.Secure.getString(getContentResolver(), "location_providers_allowed"));
    }

    public int startConnect(BluetoothDevice bluetoothDevice) {
        if (!isLocationEnabled()) {
            Toast.makeText(this, getString(R.string.txt_no_location), 0).show();
            return 0;
        }
        BTBluetooth bTBluetooth = this.btBluetooth;
        if (bTBluetooth != null && !bTBluetooth.getBluetoothState()) {
            if (!this.isShowPermission) {
                askBluetoothPermission();
            }
            return 0;
        }
        if (this.curDevice != null && this.isConnected) {
            Log.d("yqy", "已连接状态，选择新设备去连接");
            this.isDisConPreDevice = true;
            disConCurDevice();
        }
        this.curDevice = bluetoothDevice;
        connectCurDevice();
        return 1;
    }

    public void initDeviceToConnect(BluetoothDevice bluetoothDevice) {
        if (this.btBluetooth == null) {
            return;
        }
        if (this.curDevice != null && this.isConnected) {
            Log.d("yqy", "已连接状态，选择新设备去连接");
            this.isDisConPreDevice = true;
            disConCurDevice();
        }
        this.curDevice = bluetoothDevice;
        connectCurDevice();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendBytes() {
        BTBluetooth bTBluetooth = this.btBluetooth;
        if (bTBluetooth == null) {
            return;
        }
        if (this.isCanWrite) {
            this.isCanWrite = false;
            this.isNewData = false;
            bTBluetooth.sendData(this.mSendBytes);
            Log.e("yqy", "do send bytes");
            return;
        }
        Log.e("yqy", "send busy");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int backConnectPreDevice(String str) {
        if (!isLocationEnabled()) {
            Toast.makeText(this, getString(R.string.txt_no_location), 0).show();
            return 0;
        }
        BTBluetooth bTBluetooth = this.btBluetooth;
        if (bTBluetooth == null) {
            return 0;
        }
        if (!bTBluetooth.getBluetoothState()) {
            if (!this.isShowPermission) {
                askBluetoothPermission();
            }
            return 0;
        }
        if (str == null || str.isEmpty()) {
            return 0;
        }
        this.btBluetooth.stopDiscoveryDevice();
        if (this.curDevice != null && this.isConnected) {
            Log.d("yqy", "已连接状态，选择新设备去连接");
            this.isDisConPreDevice = true;
            disConCurDevice();
        }
        this.savePreMac = str;
        Log.d("yqy", "正在回连...");
        this.isConnected = false;
        this.isReScan = true;
        this.isSearching = true;
        this.btBluetooth.discoveryDevice(this.onBackSearchListener, 30000L);
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void connectCurDevice() {
        if (this.curDevice == null) {
            return;
        }
        this.btBluetooth.stopDiscoveryDevice();
        this.isConnected = false;
        this.btBluetooth.connectBLEDevice(serviceUUID, notifyUUID, writeUUID, this.curDevice, 20000L, this.onConnectListener);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void disConCurDevice() {
        BTBluetooth bTBluetooth = this.btBluetooth;
        if (bTBluetooth != null) {
            bTBluetooth.disConnectDevice();
        }
    }

    private Locale getLocale(String str) {
        String[] strArrSplit = str.split("_");
        return new Locale(strArrSplit[0], strArrSplit[1]);
    }

    @Override // android.speech.RecognitionListener
    public void onReadyForSpeech(Bundle bundle) {
        Log.e("yqy", "onReadyForSpeech");
        this.bleChannel.invokeMethod("readyForSystemSpeech", 1);
    }

    @Override // android.speech.RecognitionListener
    public void onBeginningOfSpeech() {
        Log.e("yqy", "onRecognitionStarted");
    }

    @Override // android.speech.RecognitionListener
    public void onBufferReceived(byte[] bArr) {
        Log.e("yqy", "onBufferReceived");
    }

    @Override // android.speech.RecognitionListener
    public void onEndOfSpeech() {
        Log.e("yqy", "onEndOfSpeech");
        this.systemSpeech.stopListening();
    }

    @Override // android.speech.RecognitionListener
    public void onError(int i) {
        Log.e("yqy", "onError : " + i);
        Message message = new Message();
        message.what = 20;
        this.mHandler.sendMessage(message);
    }

    @Override // android.speech.RecognitionListener
    public void onPartialResults(Bundle bundle) {
        Log.e("yqy", "onPartialResults...");
        bundle.getStringArrayList("results_recognition");
    }

    @Override // android.speech.RecognitionListener
    public void onEvent(int i, Bundle bundle) {
        Log.e("yqy", "onEvent : " + i);
    }

    @Override // android.speech.RecognitionListener
    public void onResults(Bundle bundle) {
        Log.e("yqy", "onResults...");
        ArrayList<String> stringArrayList = bundle.getStringArrayList("results_recognition");
        if (stringArrayList != null) {
            Log.d("yqy", "onResults -> " + stringArrayList.get(0));
            this.bleChannel.invokeMethod("voiceCommand", stringArrayList.get(0));
        }
        this.systemSpeech.startListening(this.recognizerIntent);
    }
}