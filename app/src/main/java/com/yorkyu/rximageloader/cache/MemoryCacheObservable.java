package com.yorkyu.rximageloader.cache;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.yorkyu.rximageloader.bean.ImageBean;

public class MemoryCacheObservable extends CacheObservable {

    private int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    private int cacheSize = maxMemory / 4;
    private LruCache<String, Bitmap> mLruCache = new LruCache<String, Bitmap>(cacheSize) {
        @Override
        protected int sizeOf(String key, Bitmap bitmap) {
            return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
        }
    };

    @Override
    public ImageBean getDataFromCache(String url) {
        Log.e("getDataFromCache", "getDataFromMemoryCache");
        Bitmap bitmap = mLruCache.get(url);
        return new ImageBean(bitmap, url);
    }

    @Override
    public void putDataToCache(ImageBean imageBean) {
        mLruCache.put(imageBean.getUrl(), imageBean.getBitmap());
    }
}
