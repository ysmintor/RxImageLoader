package com.yorkyu.rximageloader.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.jakewharton.disklrucache.DiskLruCache;
import com.yorkyu.rximageloader.bean.ImageBean;
import com.yorkyu.rximageloader.utiils.DiskCacheUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

public class DiskCacheObservable extends CacheObservable {

    private Context mContext;
    private DiskLruCache mDiskLruCache;
    private final int maxSize = 10 * 1024 * 1024;

    public DiskCacheObservable(Context mContext) {
        this.mContext = mContext;
        initDiskLruCache();
    }

    private void initDiskLruCache() {
        File cacheDir = DiskCacheUtils.getDiskCacheDir(mContext, "our_cache");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        int versionCode = DiskCacheUtils.getAppVersion(mContext);
        try {
            // 这里需要注意参数二：缓存版本号，只要不同版本号，缓存都会被清除，重新使用新的
            mDiskLruCache = DiskLruCache.open(cacheDir, versionCode, 1, maxSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ImageBean getDataFromCache(String url) {
        Log.e("getDataFromCache", "getDataFromDiskCache");
        Bitmap bitmap = getDataFromDiskLruCache(url);
        return new ImageBean(bitmap, url);
    }

    @Override
    public void putDataToCache(final ImageBean imageBean) {
        //写文件在子线程中执行
        Observable.create(new ObservableOnSubscribe<ImageBean>() {
            @Override
            public void subscribe(ObservableEmitter<ImageBean> emitter) throws Exception {
                putDataToDiskLruCache(imageBean);
            }
        }).subscribeOn(Schedulers.io())
                .subscribe();
    }

    /**
     * retrieve data from file
     *
     * @param url
     * @return
     */
    private Bitmap getDataFromDiskLruCache(String url) {
        Bitmap bitmap = null;
        FileDescriptor fileDescriptor = null;
        FileInputStream fileInputStream = null;
        try {
            final String key = DiskCacheUtils.getMD5String(url);
            DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
            if (snapshot != null) {
                fileInputStream = (FileInputStream) snapshot.getInputStream(0);
                fileDescriptor = fileInputStream.getFD();
            }
            if (fileDescriptor != null) {
                bitmap = BitmapFactory.decodeStream(fileInputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }


    /**
     * cache data into file
     *
     * @param imageBean
     */
    private void putDataToDiskLruCache(ImageBean imageBean) {
        try {
            String key = DiskCacheUtils.getMD5String(imageBean.getUrl());
            DiskLruCache.Editor editor = mDiskLruCache.edit(key);
            if (editor != null) {
                OutputStream outputStream = editor.newOutputStream(0);
                boolean isSuccess = downloadUrlToStream(imageBean.getUrl(), outputStream);
                if (isSuccess) {
                    editor.commit();
                } else {
                    editor.abort();
                }
                mDiskLruCache.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * download image data
     *
     * @param url
     * @param outputStream
     * @return
     */
    private boolean downloadUrlToStream(String url, OutputStream outputStream) {
        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        try {
            final URL urlString = new URL(url);
            urlConnection = (HttpURLConnection) urlString.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);
            out = new BufferedOutputStream(outputStream, 8 * 1024);
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
