package com.bawie.moni_yuekao0113.model;

import android.util.Log;

import com.bawie.moni_yuekao0113.bean.ShowProductBean;
import com.bawie.moni_yuekao0113.interfac.IShowPresenter;
import com.bawie.moni_yuekao0113.interfac.SerViceAPI;
import com.bawie.moni_yuekao0113.util.API;
import com.bawie.moni_yuekao0113.util.RetrofitHepler;
import com.google.gson.Gson;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class ShowModel {
    private IShowPresenter iShowPresenter;

    public ShowModel(IShowPresenter iShowPresenter) {
        this.iShowPresenter = iShowPresenter;
    }

    public void getData() {
        SerViceAPI serViceAPI = RetrofitHepler.getSerViceAPI(API.BASE_URL);
        Observable<String> products = serViceAPI.getDataByGet("ad/getAd");
        products.observeOn(AndroidSchedulers.mainThread())
               .subscribeOn(Schedulers.io())
               .subscribe(new Action1<String>() {
                   @Override
                   public void call(String s) {
                       Log.d("showmodel", s);
                       Gson gson = new Gson();
                       ShowProductBean showProductBean = gson.fromJson(s, ShowProductBean.class);
                       //使用接口回调传值
                       iShowPresenter.onSuccess(showProductBean);
                   }
               });
    }
}
