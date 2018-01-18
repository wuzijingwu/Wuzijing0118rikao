package com.bawie.moni_yuekao0113;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bawie.moni_yuekao0113.bean.LoginBean;
import com.bawie.moni_yuekao0113.interfac.IMainView;
import com.bawie.moni_yuekao0113.presenter.MainPresenter;

public class MainActivity extends AppCompatActivity implements IMainView{

    private EditText main_tel;
    private EditText main_pwd;
    private String tel;
    private String pwd;
    private TextView main_reg;
    private MainPresenter mainPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    //登录
    public void onLogin(View view) {
        tel = main_tel.getText().toString().trim();
        pwd = main_pwd.getText().toString().trim();

        mainPresenter = new MainPresenter(this);
        mainPresenter.getData(tel,pwd);
    }

    private void initView() {
        main_tel = (EditText) findViewById(R.id.main_tel);
        main_pwd = (EditText) findViewById(R.id.main_pwd);
        main_reg = (TextView) findViewById(R.id.main_reg);
        //新用户注册
        main_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转到注册页面进行注册
                startActivity(new Intent(MainActivity.this,RegsitActivity.class));
            }
        });
    }

    @Override
    public void onSuccess(LoginBean loginBean) {
        if("0".equals(loginBean.getCode())){
            //登录成功后跳转到展示页面
            startActivity(new Intent(MainActivity.this,ShowActivity.class));
        }else if("1".equals(loginBean.getCode())){
            Toast.makeText(MainActivity.this,loginBean.getMsg(),Toast.LENGTH_LONG).show();
        }
    }
}
