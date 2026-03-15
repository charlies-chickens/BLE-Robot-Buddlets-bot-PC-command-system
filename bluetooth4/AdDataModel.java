package com.example.bluetoothlibrary.bluetooth4;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class AdDataModel {
    private List<AdData> adDataList = new ArrayList();
    private boolean isIncludeDeviceName;
    private boolean isIncludeTxPowerLevel;

    public List<AdData> getAdDataList() {
        return this.adDataList;
    }

    public void setAdDataList(List<AdData> list) {
        this.adDataList = list;
    }

    public boolean isIncludeDeviceName() {
        return this.isIncludeDeviceName;
    }

    public void setIncludeDeviceName(boolean z) {
        this.isIncludeDeviceName = z;
    }

    public boolean isIncludeTxPowerLevel() {
        return this.isIncludeTxPowerLevel;
    }

    public void setIncludeTxPowerLevel(boolean z) {
        this.isIncludeTxPowerLevel = z;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("adDataList = \n");
        Iterator<AdData> it = this.adDataList.iterator();
        while (it.hasNext()) {
            sb.append(it.next().toString() + '\n');
        }
        sb.append("isIncludeDeviceName=" + this.isIncludeDeviceName + "\nisIncludeTxPowerLevel=" + this.isIncludeTxPowerLevel);
        return sb.toString();
    }

    public static class AdData {
        private String data;
        private String id;
        private int type;

        public AdData(int i, String str, String str2) {
            this.type = i;
            this.id = str;
            this.data = str2;
        }

        public int getType() {
            return this.type;
        }

        public void setType(int i) {
            this.type = i;
        }

        public String getId() {
            return this.id;
        }

        public void setId(String str) {
            this.id = str;
        }

        public String getData() {
            return this.data;
        }

        public void setData(String str) {
            this.data = str;
        }

        public String toString() {
            if (this.type == 0) {
                return "AdData{制造商 type=" + this.type + ", id='" + this.id + "', data='" + this.data + "'}";
            }
            return "AdData{服务 type=" + this.type + ", id='" + this.id + "', data='" + this.data + "'}";
        }
    }
}