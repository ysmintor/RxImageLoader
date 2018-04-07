package com.yorkyu.rximageloader.creator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.yorkyu.rximageloader.bean.ImageBean;
import com.yorkyu.rximageloader.cache.DiskCacheObservable;
import com.yorkyu.rximageloader.cache.MemoryCacheObservable;
import com.yorkyu.rximageloader.cache.NetworkCacheObservable;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class RequestCreator {
    public MemoryCacheObservable memoryCacheObservable;
    public DiskCacheObservable diskCacheObservable;
    public NetworkCacheObservable networkCacheObservable;

    public RequestCreator(Context context) {
        memoryCacheObservable = new MemoryCacheObservable();
        diskCacheObservable = new DiskCacheObservable(context);
        networkCacheObservable = new NetworkCacheObservable();
    }

    public Observable<ImageBean> getImageFromMemory(String url) {
        return memoryCacheObservable.getImage(url)
                .filter(new Predicate<ImageBean>() {
                    @Override
                    public boolean test(ImageBean imageBean) throws Exception {
                        Bitmap bitmap = imageBean.getBitmap();
                        return bitmap != null;
                    }
                });
    }

    public Observable<ImageBean> getImageFromDisk(String url) {
        return diskCacheObservable.getImage(url)
                .filter(new Predicate<ImageBean>() {
                    @Override
                    public boolean test(ImageBean imageBean) throws Exception {
                        Bitmap bitmap = imageBean.getBitmap();
                        return bitmap != null;
                    }
                }).doOnNext(new Consumer<ImageBean>() {
                    @Override
                    public void accept(ImageBean imageBean) throws Exception {
                        // clear memory cache
                        memoryCacheObservable.putDataToCache(imageBean);
                    }
                });
    }

    public Observable<ImageBean> getImageFromNetwork(String url) {
        return networkCacheObservable.getImage(url)
                .filter(new Predicate<ImageBean>() {
                    @Override
                    public boolean test(ImageBean imageBean) throws Exception {
                        Bitmap bitmap = imageBean.getBitmap();
                        return bitmap != null;
                    }
                })
                .doOnNext(new Consumer<ImageBean>() {
                    @Override
                    public void accept(ImageBean imageBean) throws Exception {
                        // clear disk and memory cache
                        diskCacheObservable.putDataToCache(imageBean);
                        memoryCacheObservable.putDataToCache(imageBean);
                    }
                });
    }

}
