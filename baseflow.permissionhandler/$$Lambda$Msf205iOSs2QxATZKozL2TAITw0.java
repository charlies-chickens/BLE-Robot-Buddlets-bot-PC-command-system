package com.baseflow.permissionhandler;

import com.baseflow.permissionhandler.PermissionManager;
import io.flutter.plugin.common.PluginRegistry;

/* JADX INFO: renamed from: com.baseflow.permissionhandler.-$$Lambda$Msf205iOSs2QxATZKozL2TAITw0 */
/* JADX INFO: compiled from: lambda */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class $$Lambda$Msf205iOSs2QxATZKozL2TAITw0 implements PermissionManager.ActivityRegistry {
    public /* synthetic */ $$Lambda$Msf205iOSs2QxATZKozL2TAITw0() {
    }

    @Override // com.baseflow.permissionhandler.PermissionManager.ActivityRegistry
    public final void addListener(PluginRegistry.ActivityResultListener activityResultListener) {
        registrar.addActivityResultListener(activityResultListener);
    }
}