package com.baseflow.permissionhandler;

import com.baseflow.permissionhandler.ServiceManager;

/* JADX INFO: renamed from: com.baseflow.permissionhandler.-$$Lambda$kvb3eDz1QeqVBPF6VqNqPbsSWUA */
/* JADX INFO: compiled from: lambda */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class $$Lambda$kvb3eDz1QeqVBPF6VqNqPbsSWUA implements ServiceManager.SuccessCallback {
    public /* synthetic */ $$Lambda$kvb3eDz1QeqVBPF6VqNqPbsSWUA() {
    }

    @Override // com.baseflow.permissionhandler.ServiceManager.SuccessCallback
    public final void onSuccess(int i) {
        result.success(Integer.valueOf(i));
    }
}