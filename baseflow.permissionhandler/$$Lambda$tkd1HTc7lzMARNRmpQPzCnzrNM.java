package com.baseflow.permissionhandler;

import com.baseflow.permissionhandler.PermissionManager;
import java.util.Map;

/* JADX INFO: renamed from: com.baseflow.permissionhandler.-$$Lambda$tkd1HTc7lzMA-RNRmpQPzCnzrNM */
/* JADX INFO: compiled from: lambda */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class $$Lambda$tkd1HTc7lzMARNRmpQPzCnzrNM implements PermissionManager.RequestPermissionsSuccessCallback {
    public /* synthetic */ $$Lambda$tkd1HTc7lzMARNRmpQPzCnzrNM() {
    }

    @Override // com.baseflow.permissionhandler.PermissionManager.RequestPermissionsSuccessCallback
    public final void onSuccess(Map map) {
        result.success(map);
    }
}