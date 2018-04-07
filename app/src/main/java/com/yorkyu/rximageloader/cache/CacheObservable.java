package com.yorkyu.rximageloader.cache;

import com.yorkyu.rximageloader.bean.ImageBean;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public abstract class CacheObservable {
    /**
     *  get cache date
     * @param url
     * @return
     */
    public Observable<ImageBean> getImage(final String url) {
        return Observable.create(new ObservableOnSubscribe<ImageBean>() {
            @Override
            public void subscribe(ObservableEmitter<ImageBean> emitter) throws Exception {
                if (!emitter.isDisposed()) {
                    ImageBean imageBean = getDataFromCache(url);
                    emitter.onNext(imageBean);
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * retrieve cached data
     * @param url
     * @return
     */
    public abstract ImageBean getDataFromCache(String url);

    /**
     * cache image data
     * @param imageBean
     */
    public abstract void putDataToCache(ImageBean imageBean);
}
