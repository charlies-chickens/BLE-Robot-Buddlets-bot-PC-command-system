package com.example.bluetoothlibrary.permission;

import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface PermissionListener {
    void onDenied(List<String> list);

    void onGranted();
}