package com.baseflow.permissionhandler;

import com.baseflow.permissionhandler.PermissionManager;
import io.flutter.plugin.common.PluginRegistry;

/* JADX INFO: renamed from: com.baseflow.permissionhandler.-$$Lambda$sBX9xOXZL-IyslP1RzvV3CSTujA */
/* JADX INFO: compiled from: lambda */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class $$Lambda$sBX9xOXZLIyslP1RzvV3CSTujA implements PermissionManager.ActivityRegistry {
    public /* synthetic */ $$Lambda$sBX9xOXZLIyslP1RzvV3CSTujA() {
    }

    @Override // com.baseflow.permissionhandler.PermissionManager.ActivityRegistry
    public final void addListener(PluginRegistry.ActivityResultListener activityResultListener) {
        activityPluginBinding.addActivityResultListener(activityResultListener);
    }
}