package com.bawie.moni_yuekao0113;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bawie.moni_yuekao0113.bean.RegistBean;
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

public class RegsitActivity extends AppCompatActivity {

    private EditText reg_tel;
    private EditText reg_pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regsit);
        initView();
    }

    //新用户和注册
    public void onRegsit(View view) {
        String tel = reg_tel.getText().toString().trim();
        String pwd = reg_pwd.getText().toString().trim();
        //请求注册接口
        SerViceAPI serViceAPI = RetrofitHepler.getSerViceAPI(API.BASEUSER_URL);
        Map<String, String> map = new HashMap<>();
        map.put("mobile",tel);
        map.put("password",pwd);
        final Observable<String> regsitBeanObservable = serViceAPI.getDataByPost("user/reg",map);
        regsitBeanObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d("regac", s);
                        Gson gson = new Gson();
                        RegistBean registBean = gson.fromJson(s, RegistBean.class);
                        if ("0".equals(registBean.getCode())) {
                            Toast.makeText(RegsitActivity.this, registBean.getMsg(), Toast.LENGTH_LONG).show();
                            //注册模块手机号验证注册成功，完成跳转到首页
                            finish();
                        } else if ("1".equals(registBean.getCode())) {
                            Toast.makeText(RegsitActivity.this, registBean.getMsg(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void initView() {
        reg_tel = (EditText) findViewById(R.id.reg_tel);
        reg_pwd = (EditText) findViewById(R.id.reg_pwd);
    }
}
