package com.baseflow.permissionhandler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import io.flutter.plugin.common.PluginRegistry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
final class PermissionManager {
    private boolean ongoing = false;

    @FunctionalInterface
    interface ActivityRegistry {
        void addListener(PluginRegistry.ActivityResultListener activityResultListener);
    }

    @FunctionalInterface
    interface CheckPermissionsSuccessCallback {
        void onSuccess(int i);
    }

    @FunctionalInterface
    interface PermissionRegistry {
        void addListener(PluginRegistry.RequestPermissionsResultListener requestPermissionsResultListener);
    }

    @FunctionalInterface
    interface RequestPermissionsSuccessCallback {
        void onSuccess(Map<Integer, Integer> map);
    }

    @FunctionalInterface
    interface ShouldShowRequestPermissionRationaleSuccessCallback {
        void onSuccess(boolean z);
    }

    PermissionManager() {
    }

    void checkPermissionStatus(int i, Context context, Activity activity, CheckPermissionsSuccessCallback checkPermissionsSuccessCallback, ErrorCallback errorCallback) {
        checkPermissionsSuccessCallback.onSuccess(determinePermissionStatus(i, context, activity));
    }

    void requestPermissions(List<Integer> list, Activity activity, ActivityRegistry activityRegistry, PermissionRegistry permissionRegistry, final RequestPermissionsSuccessCallback requestPermissionsSuccessCallback, ErrorCallback errorCallback) {
        if (this.ongoing) {
            errorCallback.onError("PermissionHandler.PermissionManager", "A request for permissions is already running, please wait for it to finish before doing another request (note that you can request multiple permissions at the same time).");
            return;
        }
        if (activity == null) {
            Log.d("permissions_handler", "Unable to detect current Activity.");
            errorCallback.onError("PermissionHandler.PermissionManager", "Unable to detect current Android Activity.");
            return;
        }
        HashMap map = new HashMap();
        ArrayList arrayList = new ArrayList();
        for (Integer num : list) {
            if (determinePermissionStatus(num.intValue(), activity, activity) == 1) {
                if (!map.containsKey(num)) {
                    map.put(num, 1);
                }
            } else {
                List<String> manifestNames = PermissionUtils.getManifestNames(activity, num.intValue());
                if (manifestNames == null || manifestNames.isEmpty()) {
                    if (!map.containsKey(num)) {
                        if (num.intValue() == 16 && Build.VERSION.SDK_INT < 23) {
                            map.put(num, 2);
                        } else {
                            map.put(num, 0);
                        }
                    }
                } else if (Build.VERSION.SDK_INT >= 23 && num.intValue() == 16) {
                    activityRegistry.addListener(new ActivityResultListener(requestPermissionsSuccessCallback));
                    String packageName = activity.getPackageName();
                    Intent intent = new Intent();
                    intent.setAction("android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS");
                    intent.setData(Uri.parse("package:" + packageName));
                    activity.startActivityForResult(intent, 5672353);
                } else {
                    arrayList.addAll(manifestNames);
                }
            }
        }
        String[] strArr = (String[]) arrayList.toArray(new String[0]);
        if (arrayList.size() > 0) {
            permissionRegistry.addListener(new RequestPermissionsListener(activity, map, new RequestPermissionsSuccessCallback() { // from class: com.baseflow.permissionhandler.-$$Lambda$PermissionManager$C1Lc2eGN38XZcT6FjxSuqfMM4XU
                @Override // com.baseflow.permissionhandler.PermissionManager.RequestPermissionsSuccessCallback
                public final void onSuccess(Map map2) {
                    this.f$0.lambda$requestPermissions$0$PermissionManager(requestPermissionsSuccessCallback, map2);
                }
            }));
            this.ongoing = true;
            ActivityCompat.requestPermissions(activity, strArr, 24);
        } else {
            this.ongoing = false;
            if (map.size() > 0) {
                requestPermissionsSuccessCallback.onSuccess(map);
            }
        }
    }

    public /* synthetic */ void lambda$requestPermissions$0$PermissionManager(RequestPermissionsSuccessCallback requestPermissionsSuccessCallback, Map map) {
        this.ongoing = false;
        requestPermissionsSuccessCallback.onSuccess(map);
    }

    private int determinePermissionStatus(int i, Context context, Activity activity) {
        if (i == 17) {
            return checkNotificationPermissionStatus(context);
        }
        if (i == 21) {
            return checkBluetoothPermissionStatus(context);
        }
        List<String> manifestNames = PermissionUtils.getManifestNames(context, i);
        if (manifestNames == null) {
            Log.d("permissions_handler", "No android specific permissions needed for: " + i);
            return 1;
        }
        if (manifestNames.size() == 0) {
            Log.d("permissions_handler", "No permissions found in manifest for: " + i);
            return (i != 16 || Build.VERSION.SDK_INT >= 23) ? 0 : 2;
        }
        boolean z = context.getApplicationInfo().targetSdkVersion >= 23;
        for (String str : manifestNames) {
            if (z) {
                if (i == 16) {
                    String packageName = context.getPackageName();
                    PowerManager powerManager = (PowerManager) context.getSystemService("power");
                    if (Build.VERSION.SDK_INT >= 23) {
                        return (powerManager == null || !powerManager.isIgnoringBatteryOptimizations(packageName)) ? 0 : 1;
                    }
                    return 2;
                }
                if (ContextCompat.checkSelfPermission(context, str) != 0) {
                    return 0;
                }
            }
        }
        return 1;
    }

    void shouldShowRequestPermissionRationale(int i, Activity activity, ShouldShowRequestPermissionRationaleSuccessCallback shouldShowRequestPermissionRationaleSuccessCallback, ErrorCallback errorCallback) {
        if (activity == null) {
            Log.d("permissions_handler", "Unable to detect current Activity.");
            errorCallback.onError("PermissionHandler.PermissionManager", "Unable to detect current Android Activity.");
            return;
        }
        List<String> manifestNames = PermissionUtils.getManifestNames(activity, i);
        if (manifestNames == null) {
            Log.d("permissions_handler", "No android specific permissions needed for: " + i);
            shouldShowRequestPermissionRationaleSuccessCallback.onSuccess(false);
            return;
        }
        if (manifestNames.isEmpty()) {
            Log.d("permissions_handler", "No permissions found in manifest for: " + i + " no need to show request rationale");
            shouldShowRequestPermissionRationaleSuccessCallback.onSuccess(false);
            return;
        }
        shouldShowRequestPermissionRationaleSuccessCallback.onSuccess(ActivityCompat.shouldShowRequestPermissionRationale(activity, manifestNames.get(0)));
    }

    private int checkNotificationPermissionStatus(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled() ? 1 : 0;
    }

    private int checkBluetoothPermissionStatus(Context context) {
        List<String> manifestNames = PermissionUtils.getManifestNames(context, 21);
        if (!(manifestNames == null || manifestNames.isEmpty())) {
            return 1;
        }
        Log.d("permissions_handler", "Bluetooth permission missing in manifest");
        return 0;
    }

    static final class ActivityResultListener implements PluginRegistry.ActivityResultListener {
        boolean alreadyCalled = false;
        final RequestPermissionsSuccessCallback callback;

        ActivityResultListener(RequestPermissionsSuccessCallback requestPermissionsSuccessCallback) {
            this.callback = requestPermissionsSuccessCallback;
        }

        @Override // io.flutter.plugin.common.PluginRegistry.ActivityResultListener
        public boolean onActivityResult(int i, int i2, Intent intent) {
            if (this.alreadyCalled || i != 5672353) {
                return false;
            }
            this.alreadyCalled = true;
            int i3 = i2 == -1 ? 1 : 0;
            HashMap map = new HashMap();
            map.put(16, Integer.valueOf(i3));
            this.callback.onSuccess(map);
            return true;
        }
    }

    static final class RequestPermissionsListener implements PluginRegistry.RequestPermissionsResultListener {
        final Activity activity;
        boolean alreadyCalled = false;
        final RequestPermissionsSuccessCallback callback;
        final Map<Integer, Integer> requestResults;

        RequestPermissionsListener(Activity activity, Map<Integer, Integer> map, RequestPermissionsSuccessCallback requestPermissionsSuccessCallback) {
            this.activity = activity;
            this.callback = requestPermissionsSuccessCallback;
            this.requestResults = map;
        }

        @Override // io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener
        public boolean onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
            if (this.alreadyCalled || i != 24) {
                return false;
            }
            this.alreadyCalled = true;
            for (int i2 = 0; i2 < strArr.length; i2++) {
                String str = strArr[i2];
                int manifestName = PermissionUtils.parseManifestName(str);
                if (manifestName != 20) {
                    int i3 = iArr[i2];
                    if (manifestName == 7) {
                        if (!this.requestResults.containsKey(7)) {
                            this.requestResults.put(7, Integer.valueOf(PermissionUtils.toPermissionStatus(this.activity, str, i3)));
                        }
                        if (!this.requestResults.containsKey(14)) {
                            this.requestResults.put(14, Integer.valueOf(PermissionUtils.toPermissionStatus(this.activity, str, i3)));
                        }
                    } else if (manifestName == 4) {
                        int permissionStatus = PermissionUtils.toPermissionStatus(this.activity, str, i3);
                        if (!this.requestResults.containsKey(4)) {
                            this.requestResults.put(4, Integer.valueOf(permissionStatus));
                        }
                    } else if (manifestName == 3) {
                        int permissionStatus2 = PermissionUtils.toPermissionStatus(this.activity, str, i3);
                        if (Build.VERSION.SDK_INT < 29 && !this.requestResults.containsKey(4)) {
                            this.requestResults.put(4, Integer.valueOf(permissionStatus2));
                        }
                        if (!this.requestResults.containsKey(5)) {
                            this.requestResults.put(5, Integer.valueOf(permissionStatus2));
                        }
                        this.requestResults.put(Integer.valueOf(manifestName), Integer.valueOf(permissionStatus2));
                    } else if (!this.requestResults.containsKey(Integer.valueOf(manifestName))) {
                        this.requestResults.put(Integer.valueOf(manifestName), Integer.valueOf(PermissionUtils.toPermissionStatus(this.activity, str, i3)));
                    }
                    PermissionUtils.updatePermissionShouldShowStatus(this.activity, manifestName);
                }
            }
            this.callback.onSuccess(this.requestResults);
            return true;
        }
    }
}