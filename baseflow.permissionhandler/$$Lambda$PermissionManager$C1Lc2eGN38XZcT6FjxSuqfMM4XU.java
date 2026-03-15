package com.baseflow.permissionhandler;

import com.baseflow.permissionhandler.PermissionManager;
import java.util.Map;

/* JADX INFO: renamed from: com.baseflow.permissionhandler.-$$Lambda$PermissionManager$C1Lc2eGN38XZcT6FjxSuqfMM4XU */
/* JADX INFO: compiled from: lambda */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class $$Lambda$PermissionManager$C1Lc2eGN38XZcT6FjxSuqfMM4XU implements PermissionManager.RequestPermissionsSuccessCallback {
    private final /* synthetic */ PermissionManager.RequestPermissionsSuccessCallback f$1;

    public /* synthetic */ $$Lambda$PermissionManager$C1Lc2eGN38XZcT6FjxSuqfMM4XU(PermissionManager.RequestPermissionsSuccessCallback requestPermissionsSuccessCallback) {
        requestPermissionsSuccessCallback = requestPermissionsSuccessCallback;
    }

    @Override // com.baseflow.permissionhandler.PermissionManager.RequestPermissionsSuccessCallback
    public final void onSuccess(Map map) {
        this.f$0.lambda$requestPermissions$0$PermissionManager(requestPermissionsSuccessCallback, map);
    }
}