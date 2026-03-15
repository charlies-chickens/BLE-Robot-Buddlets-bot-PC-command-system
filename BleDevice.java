package com.smadom.cpbuddlets;

import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class BleDevice {
    String address;
    String name;

    public BleDevice(String str, String str2) {
        this.name = str;
        this.address = str2;
    }

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.address;
    }

    HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("name", this.name);
        map.put("address", this.address);
        return map;
    }
}