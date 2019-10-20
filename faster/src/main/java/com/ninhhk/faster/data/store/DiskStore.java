package com.ninhhk.faster.data.store;

import android.content.Context;

import com.ninhhk.faster.Callback;
import com.ninhhk.faster.Key;

public abstract class DiskStore {
    protected Callback<byte[]> callback;
    protected Context context;

    public DiskStore(Context context) {
        this.context = context;
    }

    public void setCallback(Callback<byte[]> callback) {
        this.callback = callback;
    }

    public abstract byte[] load(Key key);

    protected abstract byte[] loadFromDataSource(Key key);

    protected abstract boolean exists(Key key);
}
