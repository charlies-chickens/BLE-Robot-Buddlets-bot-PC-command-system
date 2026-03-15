package com.baseflow.permissionhandler;

import com.baseflow.permissionhandler.AppSettingsManager;

/* JADX INFO: renamed from: com.baseflow.permissionhandler.-$$Lambda$TR2UE6xvkpeRG17hPoBKZeEIKug */
/* JADX INFO: compiled from: lambda */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class $$Lambda$TR2UE6xvkpeRG17hPoBKZeEIKug implements AppSettingsManager.OpenAppSettingsSuccessCallback {
    public /* synthetic */ $$Lambda$TR2UE6xvkpeRG17hPoBKZeEIKug() {
    }

    @Override // com.baseflow.permissionhandler.AppSettingsManager.OpenAppSettingsSuccessCallback
    public final void onSuccess(boolean z) {
        result.success(Boolean.valueOf(z));
    }
}