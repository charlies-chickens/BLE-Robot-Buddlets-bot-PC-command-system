package com.baseflow.permissionhandler;

import android.app.Activity;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class PermissionUtils {
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    /* JADX WARN: Removed duplicated region for block: B:89:0x015c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    static int parseManifestName(java.lang.String r14) {
        /*
            Method dump skipped, instruction units count: 540
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baseflow.permissionhandler.PermissionUtils.parseManifestName(java.lang.String):int");
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:95:0x0162  */
    /* JADX WARN: Removed duplicated region for block: B:98:0x016d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    static java.util.List<java.lang.String> getManifestNames(android.content.Context r3, int r4) {
        /*
            Method dump skipped, instruction units count: 486
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baseflow.permissionhandler.PermissionUtils.getManifestNames(android.content.Context, int):java.util.List");
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0021 A[Catch: Exception -> 0x001d, TryCatch #0 {Exception -> 0x001d, blocks: (B:4:0x0006, B:5:0x000a, B:7:0x0010, B:13:0x0021, B:15:0x0027, B:17:0x0037, B:19:0x003d, B:20:0x004c, B:22:0x0052), top: B:27:0x0006 }] */
    /* JADX WARN: Removed duplicated region for block: B:15:0x0027 A[Catch: Exception -> 0x001d, TryCatch #0 {Exception -> 0x001d, blocks: (B:4:0x0006, B:5:0x000a, B:7:0x0010, B:13:0x0021, B:15:0x0027, B:17:0x0037, B:19:0x003d, B:20:0x004c, B:22:0x0052), top: B:27:0x0006 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static boolean hasPermissionInManifest(android.content.Context r4, java.util.ArrayList<java.lang.String> r5, java.lang.String r6) {
        /*
            r0 = 1
            r1 = 0
            java.lang.String r2 = "permissions_handler"
            if (r5 == 0) goto L1f
            java.util.Iterator r5 = r5.iterator()     // Catch: java.lang.Exception -> L1d
        La:
            boolean r3 = r5.hasNext()     // Catch: java.lang.Exception -> L1d
            if (r3 == 0) goto L1f
            java.lang.Object r3 = r5.next()     // Catch: java.lang.Exception -> L1d
            java.lang.String r3 = (java.lang.String) r3     // Catch: java.lang.Exception -> L1d
            boolean r3 = r3.equals(r6)     // Catch: java.lang.Exception -> L1d
            if (r3 == 0) goto La
            return r0
        L1d:
            r4 = move-exception
            goto L5f
        L1f:
            if (r4 != 0) goto L27
            java.lang.String r4 = "Unable to detect current Activity or App Context."
            android.util.Log.d(r2, r4)     // Catch: java.lang.Exception -> L1d
            return r1
        L27:
            android.content.pm.PackageManager r5 = r4.getPackageManager()     // Catch: java.lang.Exception -> L1d
            java.lang.String r4 = r4.getPackageName()     // Catch: java.lang.Exception -> L1d
            r3 = 4096(0x1000, float:5.74E-42)
            android.content.pm.PackageInfo r4 = r5.getPackageInfo(r4, r3)     // Catch: java.lang.Exception -> L1d
            if (r4 != 0) goto L3d
            java.lang.String r4 = "Unable to get Package info, will not be able to determine permissions to request."
            android.util.Log.d(r2, r4)     // Catch: java.lang.Exception -> L1d
            return r1
        L3d:
            java.util.ArrayList r5 = new java.util.ArrayList     // Catch: java.lang.Exception -> L1d
            java.lang.String[] r4 = r4.requestedPermissions     // Catch: java.lang.Exception -> L1d
            java.util.List r4 = java.util.Arrays.asList(r4)     // Catch: java.lang.Exception -> L1d
            r5.<init>(r4)     // Catch: java.lang.Exception -> L1d
            java.util.Iterator r4 = r5.iterator()     // Catch: java.lang.Exception -> L1d
        L4c:
            boolean r5 = r4.hasNext()     // Catch: java.lang.Exception -> L1d
            if (r5 == 0) goto L64
            java.lang.Object r5 = r4.next()     // Catch: java.lang.Exception -> L1d
            java.lang.String r5 = (java.lang.String) r5     // Catch: java.lang.Exception -> L1d
            boolean r5 = r5.equals(r6)     // Catch: java.lang.Exception -> L1d
            if (r5 == 0) goto L4c
            return r0
        L5f:
            java.lang.String r5 = "Unable to check manifest for permission: "
            android.util.Log.d(r2, r5, r4)
        L64:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.baseflow.permissionhandler.PermissionUtils.hasPermissionInManifest(android.content.Context, java.util.ArrayList, java.lang.String):boolean");
    }

    static int toPermissionStatus(Activity activity, String str, int i) {
        if (i == -1) {
            return (Build.VERSION.SDK_INT < 23 || !isNeverAskAgainSelected(activity, str)) ? 0 : 4;
        }
        return 1;
    }

    static void updatePermissionShouldShowStatus(Activity activity, int i) {
        List<String> manifestNames;
        if (activity != null && (manifestNames = getManifestNames(activity, i)) != null && manifestNames.isEmpty()) {
        }
    }

    static boolean isNeverAskAgainSelected(Activity activity, String str) {
        if (activity == null) {
            return false;
        }
        return !ActivityCompat.shouldShowRequestPermissionRationale(activity, str);
    }
}