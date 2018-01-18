package com.bawie.moni_yuekao0113.model;

import com.bawie.moni_yuekao0113.bean.QueryCarts;
import com.bawie.moni_yuekao0113.interfac.IShowCartsPresenter;
import com.bawie.moni_yuekao0113.interfac.SerViceAPI;
import com.bawie.moni_yuekao0113.util.API;
import com.bawie.moni_yuekao0113.util.RetrofitHepler;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;



public class ShowCartModel {
    private IShowCartsPresenter iShowCartsPresenter;

    public ShowCartModel(IShowCartsPresenter iShowCartsPresenter) {
        this.iShowCartsPresenter = iShowCartsPresenter;
    }

    public void getData() {
        SerViceAPI serViceAPI = RetrofitHepler.getSerViceAPI(API.BASE_URL);
        Map<String,String> map = new HashMap<>();
        map.put("uid",String.valueOf(2753));
        Observable<String> beanObservable = serViceAPI.getDataByGet("product/getCarts",map);
        beanObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Gson gson = new Gson();
                        QueryCarts queryCarts = gson.fromJson(s, QueryCarts.class);
                        //使用接口回调传值
                        iShowCartsPresenter.onSuccess(queryCarts);
                    }
                });
    }
}
