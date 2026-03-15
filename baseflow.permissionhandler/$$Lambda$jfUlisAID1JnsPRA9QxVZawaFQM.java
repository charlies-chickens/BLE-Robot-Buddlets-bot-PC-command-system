package com.baseflow.permissionhandler;

import com.baseflow.permissionhandler.PermissionManager;
import io.flutter.plugin.common.PluginRegistry;

/* JADX INFO: renamed from: com.baseflow.permissionhandler.-$$Lambda$jfUlisAID1JnsPRA9QxVZawaFQM */
/* JADX INFO: compiled from: lambda */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class $$Lambda$jfUlisAID1JnsPRA9QxVZawaFQM implements PermissionManager.PermissionRegistry {
    public /* synthetic */ $$Lambda$jfUlisAID1JnsPRA9QxVZawaFQM() {
    }

    @Override // com.baseflow.permissionhandler.PermissionManager.PermissionRegistry
    public final void addListener(PluginRegistry.RequestPermissionsResultListener requestPermissionsResultListener) {
        registrar.addRequestPermissionsResultListener(requestPermissionsResultListener);
    }
}