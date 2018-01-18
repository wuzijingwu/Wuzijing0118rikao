package com.bawie.moni_yuekao0113.model;

import android.util.Log;

import com.bawie.moni_yuekao0113.bean.LoginBean;
import com.bawie.moni_yuekao0113.interfac.IMainPresenter;
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



public class MainModel {
    private IMainPresenter iMainPresenter;

    public MainModel(IMainPresenter iMainPresenter) {
        this.iMainPresenter = iMainPresenter;
    }

    public void getData(String tel,String pwd) {
        //请求网络数据
        SerViceAPI serViceAPI = RetrofitHepler.getSerViceAPI(API.BASEUSER_URL);
        Map<String,String> map = new HashMap<>();
        map.put("mobile",tel);
        map.put("password",pwd);
        Observable<String> userLogin = serViceAPI.getDataByPost("user/login",map);
        userLogin.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d("mainmodel",s);
                        Gson gson = new Gson();
                        LoginBean loginBean = gson.fromJson(s, LoginBean.class);
                        //使用接口回调传值
                        iMainPresenter.onSuccess(loginBean);
                    }
                });
    }
}
