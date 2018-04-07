package com.yorkyu.rximageloader.loader;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.yorkyu.rximageloader.bean.ImageBean;
import com.yorkyu.rximageloader.creator.RequestCreator;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


public class RxImageLoader {
    static RxImageLoader singleton;
    private String mUrl;
    private RequestCreator requestCreator;

    private RxImageLoader(Builder builder) {
        requestCreator = new RequestCreator(builder.mContext);
    }

    public static RxImageLoader with(Context context) {
        if (singleton == null) {
            synchronized (RxImageLoader.class) {
                if (singleton == null) {
                    singleton = new Builder(context).build();
                }
            }
        }
        return singleton;
    }

    public RxImageLoader load(String url) {
        this.mUrl = url;
        return singleton;
    }

    public void into(final ImageView imageView) {
        Observable
                .concat(
                     requestCreator.getImageFromMemory(mUrl),
                     requestCreator.getImageFromDisk(mUrl),
                     requestCreator.getImageFromNetwork(mUrl)
                )
                .first(new ImageBean(null, mUrl))
                .toObservable()
                .subscribe(new Observer<ImageBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ImageBean imageBean) {
                        if (imageBean.getBitmap() != null) {
                            imageView.setImageBitmap(imageBean.getBitmap());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        Log.e("onComplete", "onComplete");
                    }
                });
    }

    public static class Builder {
        private Context mContext;

        public Builder(Context context) {
            this.mContext = context;
        }

        public RxImageLoader build() {
            return new RxImageLoader(this);
        }
    }
}
