package com.baseflow.permissionhandler;

import com.baseflow.permissionhandler.PermissionManager;

/* JADX INFO: renamed from: com.baseflow.permissionhandler.-$$Lambda$WNQCVwak-kfCyfzR026kfI0-xwI */
/* JADX INFO: compiled from: lambda */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class $$Lambda$WNQCVwakkfCyfzR026kfI0xwI implements PermissionManager.ShouldShowRequestPermissionRationaleSuccessCallback {
    public /* synthetic */ $$Lambda$WNQCVwakkfCyfzR026kfI0xwI() {
    }

    @Override // com.baseflow.permissionhandler.PermissionManager.ShouldShowRequestPermissionRationaleSuccessCallback
    public final void onSuccess(boolean z) {
        result.success(Boolean.valueOf(z));
    }
}