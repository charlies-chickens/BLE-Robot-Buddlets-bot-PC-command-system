package com.example.bluetoothlibrary.bluetooth4;

/* JADX INFO: loaded from: classes.dex */
public class AdSetting {
    private int advertiseMode;
    private boolean isConnectable;
    private int timeout;
    private int txPowerLevel;

    public int getAdvertiseMode() {
        return this.advertiseMode;
    }

    public void setAdvertiseMode(int i) {
        this.advertiseMode = i;
    }

    public int getTxPowerLevel() {
        return this.txPowerLevel;
    }

    public void setTxPowerLevel(int i) {
        this.txPowerLevel = i;
    }

    public boolean isConnectable() {
        return this.isConnectable;
    }

    public void setConnectable(boolean z) {
        this.isConnectable = z;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(int i) {
        this.timeout = i;
    }

    public String toString() {
        return "AdSetting{advertiseMode=" + this.advertiseMode + ", txPowerLevel=" + this.txPowerLevel + ", isConnectable=" + this.isConnectable + ", timeout=" + this.timeout + '}';
    }
}