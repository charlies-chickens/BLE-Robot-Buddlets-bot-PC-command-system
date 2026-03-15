package com.baseflow.permissionhandler;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/* JADX INFO: loaded from: classes.dex */
final class PermissionConstants {
    static final String LOG_TAG = "permissions_handler";
    static final int PERMISSION_CODE = 24;
    static final int PERMISSION_CODE_IGNORE_BATTERY_OPTIMIZATIONS = 5672353;
    static final int PERMISSION_GROUP_ACCESS_MEDIA_LOCATION = 18;
    static final int PERMISSION_GROUP_ACTIVITY_RECOGNITION = 19;
    static final int PERMISSION_GROUP_BLUETOOTH = 21;
    static final int PERMISSION_GROUP_CALENDAR = 0;
    static final int PERMISSION_GROUP_CAMERA = 1;
    static final int PERMISSION_GROUP_CONTACTS = 2;
    static final int PERMISSION_GROUP_IGNORE_BATTERY_OPTIMIZATIONS = 16;
    static final int PERMISSION_GROUP_LOCATION = 3;
    static final int PERMISSION_GROUP_LOCATION_ALWAYS = 4;
    static final int PERMISSION_GROUP_LOCATION_WHEN_IN_USE = 5;
    static final int PERMISSION_GROUP_MEDIA_LIBRARY = 6;
    static final int PERMISSION_GROUP_MICROPHONE = 7;
    static final int PERMISSION_GROUP_NOTIFICATION = 17;
    static final int PERMISSION_GROUP_PHONE = 8;
    static final int PERMISSION_GROUP_PHOTOS = 9;
    static final int PERMISSION_GROUP_PHOTOS_ADD_ONLY = 10;
    static final int PERMISSION_GROUP_REMINDERS = 11;
    static final int PERMISSION_GROUP_SENSORS = 12;
    static final int PERMISSION_GROUP_SMS = 13;
    static final int PERMISSION_GROUP_SPEECH = 14;
    static final int PERMISSION_GROUP_STORAGE = 15;
    static final int PERMISSION_GROUP_UNKNOWN = 20;
    static final int PERMISSION_STATUS_DENIED = 0;
    static final int PERMISSION_STATUS_GRANTED = 1;
    static final int PERMISSION_STATUS_LIMITED = 3;
    static final int PERMISSION_STATUS_NEVER_ASK_AGAIN = 4;
    static final int PERMISSION_STATUS_RESTRICTED = 2;
    static final int SERVICE_STATUS_DISABLED = 0;
    static final int SERVICE_STATUS_ENABLED = 1;
    static final int SERVICE_STATUS_NOT_APPLICABLE = 2;

    @Retention(RetentionPolicy.SOURCE)
    @interface PermissionGroup {
    }

    @Retention(RetentionPolicy.SOURCE)
    @interface PermissionStatus {
    }

    @Retention(RetentionPolicy.SOURCE)
    @interface ServiceStatus {
    }

    PermissionConstants() {
    }
}