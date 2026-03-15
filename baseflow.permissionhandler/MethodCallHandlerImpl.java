package com.baseflow.permissionhandler;

import android.app.Activity;
import android.content.Context;
import com.baseflow.permissionhandler.PermissionManager;
import io.flutter.plugin.common.MethodChannel;

/* JADX INFO: loaded from: classes.dex */
final class MethodCallHandlerImpl implements MethodChannel.MethodCallHandler {
    private Activity activity;
    private PermissionManager.ActivityRegistry activityRegistry;
    private final AppSettingsManager appSettingsManager;
    private final Context applicationContext;
    private final PermissionManager permissionManager;
    private PermissionManager.PermissionRegistry permissionRegistry;
    private final ServiceManager serviceManager;

    MethodCallHandlerImpl(Context context, AppSettingsManager appSettingsManager, PermissionManager permissionManager, ServiceManager serviceManager) {
        this.applicationContext = context;
        this.appSettingsManager = appSettingsManager;
        this.permissionManager = permissionManager;
        this.serviceManager = serviceManager;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public void setActivityRegistry(PermissionManager.ActivityRegistry activityRegistry) {
        this.activityRegistry = activityRegistry;
    }

    public void setPermissionRegistry(PermissionManager.PermissionRegistry permissionRegistry) {
        this.permissionRegistry = permissionRegistry;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0040  */
    @Override // io.flutter.plugin.common.MethodChannel.MethodCallHandler
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public void onMethodCall(io.flutter.plugin.common.MethodCall r8, final io.flutter.plugin.common.MethodChannel.Result r9) {
        /*
            Method dump skipped, instruction units count: 252
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baseflow.permissionhandler.MethodCallHandlerImpl.onMethodCall(io.flutter.plugin.common.MethodCall, io.flutter.plugin.common.MethodChannel$Result):void");
    }
}