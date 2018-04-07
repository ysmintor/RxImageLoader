package com.yorkyu.rximageloader.cache;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.yorkyu.rximageloader.bean.ImageBean;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class NetworkCacheObservable extends CacheObservable {
    @Override
    public ImageBean getDataFromCache(String url) {
        Log.e("getDataFromCache", "getDataFromNetworkCache");
        Bitmap bitmap = downloadImage(url);
        return new ImageBean(bitmap, url);
    }

    @Override
    public void putDataToCache(ImageBean imageBean) {

    }

    public Bitmap downloadImage(String url) {
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            URL imageUrl = new URL(url);
            URLConnection urlConnection = (HttpURLConnection) imageUrl.openConnection();
            inputStream = urlConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }
}
