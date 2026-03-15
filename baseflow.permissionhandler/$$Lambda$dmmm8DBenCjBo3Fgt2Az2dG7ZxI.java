package com.baseflow.permissionhandler;

import com.baseflow.permissionhandler.PermissionManager;

/* JADX INFO: renamed from: com.baseflow.permissionhandler.-$$Lambda$dmmm8DBenCjBo3Fgt2Az2dG7ZxI */
/* JADX INFO: compiled from: lambda */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class $$Lambda$dmmm8DBenCjBo3Fgt2Az2dG7ZxI implements PermissionManager.CheckPermissionsSuccessCallback {
    public /* synthetic */ $$Lambda$dmmm8DBenCjBo3Fgt2Az2dG7ZxI() {
    }

    @Override // com.baseflow.permissionhandler.PermissionManager.CheckPermissionsSuccessCallback
    public final void onSuccess(int i) {
        result.success(Integer.valueOf(i));
    }
}