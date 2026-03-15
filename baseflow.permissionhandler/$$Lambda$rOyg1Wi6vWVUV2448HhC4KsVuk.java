package com.baseflow.permissionhandler;

import com.baseflow.permissionhandler.PermissionManager;
import io.flutter.plugin.common.PluginRegistry;

/* JADX INFO: renamed from: com.baseflow.permissionhandler.-$$Lambda$rOyg1Wi6vWVUV2448-HhC4KsVuk */
/* JADX INFO: compiled from: lambda */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class $$Lambda$rOyg1Wi6vWVUV2448HhC4KsVuk implements PermissionManager.PermissionRegistry {
    public /* synthetic */ $$Lambda$rOyg1Wi6vWVUV2448HhC4KsVuk() {
    }

    @Override // com.baseflow.permissionhandler.PermissionManager.PermissionRegistry
    public final void addListener(PluginRegistry.RequestPermissionsResultListener requestPermissionsResultListener) {
        activityPluginBinding.addRequestPermissionsResultListener(requestPermissionsResultListener);
    }
}