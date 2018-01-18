package com.bawie.moni_yuekao0113.model;

import android.util.Log;

import com.bawie.moni_yuekao0113.bean.ProductDetailBean;
import com.bawie.moni_yuekao0113.interfac.IDetailPresenter;
import com.bawie.moni_yuekao0113.interfac.SerViceAPI;
import com.bawie.moni_yuekao0113.util.API;
import com.bawie.moni_yuekao0113.util.RetrofitHepler;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class DetailModel {
    private IDetailPresenter iDetailPresenter;

    public DetailModel(IDetailPresenter iDetailPresenter) {
        this.iDetailPresenter = iDetailPresenter;
    }

    public void getData(int pid) {
        SerViceAPI serViceAPI = RetrofitHepler.getSerViceAPI(API.BASE_URL);

        Map<String, String> map = new HashMap<>();
        map.put("pid",String.valueOf(pid));
        serViceAPI.getDataByPost("product/getProductDetail",map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d("detailmodel",s);
                        Gson gson = new Gson();
                        ProductDetailBean productDetailBean = gson.fromJson(s, ProductDetailBean.class);
                        iDetailPresenter.onSuccess(productDetailBean);
                    }
                });
    }
}
