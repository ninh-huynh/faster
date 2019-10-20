package com.ninhhk.faster.data.store;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.ninhhk.faster.Callback;
import com.ninhhk.faster.Key;
import com.ninhhk.faster.Request;
import com.ninhhk.faster.RequestManager;
import com.ninhhk.faster.RequestOption;
import com.ninhhk.faster.decoder.ImageDecoder;
import com.ninhhk.faster.decoder.MatchTargetDimensionImageDecoder;

import java.util.Objects;

public class MemStoreImp extends MemoryStore {

    public static final String TAG = MemStoreImp.class.getSimpleName();

    private final int MAX_SIZE;

    public MemStoreImp(DiskStore diskStore, Context context) {
        super(diskStore);
        MAX_SIZE = getSuitableSize(context);
        memCache = new LruCache<>(MAX_SIZE);
        imageDecoder = new MatchTargetDimensionImageDecoder();
        requestManager = RequestManager.getInstance();
    }

    private LruCache<Key, Bitmap> memCache;
    private ImageDecoder imageDecoder;
    private RequestManager requestManager;

    private static int getSuitableSize(Context context) {
        int memoryClass = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        int maxSize;
        if (memoryClass >= 192) {
            maxSize = 30;
        } else {
            maxSize = 15;
        }
        int cacheSize = Math.min(maxSize, memoryClass / 7) * 1024 * 1024;
        return cacheSize;
    }

    @Override
    public Bitmap load(Key key) {
        Objects.requireNonNull(this.callback);
        Bitmap bm = memCache.get(key);

        if (existInRepo(key)){
            Log.i(TAG, "Bitmap with key " + key.toString() + "has read from mem");
            callback.onReady(bm);
            return null;
        }
        loadFromDisk(key);
        return null;
    }

    @Override
    public byte[] loadFromDisk(Key key) {

        Callback<byte[]> callback = originBytes -> {
            Bitmap bitmap = decodeFromBytes(originBytes, key);
            saveToMemCache(key, bitmap);
            MemStoreImp.this.callback.onReady(bitmap);
        };

        diskStore.setCallback(callback);
        diskStore.load(key);
        return new byte[0];
    }

    private void saveToMemCache(Key key, Bitmap bitmap) {
        this.memCache.put(key, bitmap);
        Log.i(TAG, "Bitmap with key " + key.toString() + " has been cached!" );
    }

    private Bitmap decodeFromBytes(byte[] bytes, Key key) {
        Request request = requestManager.getRequest(key);
        RequestOption requestOption = request.getRequestOption();

        Bitmap bitmap;
        bitmap = imageDecoder.decode(bytes, requestOption);
        return bitmap;
    }

    @Override
    protected boolean existInRepo(Key key) {
        return memCache.get(key) != null;
    }
}
