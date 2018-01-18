package com.bawie.moni_yuekao0113.presenter;

import com.bawie.moni_yuekao0113.bean.LoginBean;
import com.bawie.moni_yuekao0113.interfac.IMainPresenter;
import com.bawie.moni_yuekao0113.interfac.IMainView;
import com.bawie.moni_yuekao0113.model.MainModel;



public class MainPresenter implements IMainPresenter {
    private IMainView iMainView;
    private final MainModel mainModel;

    public MainPresenter(IMainView iMainView) {
        this.iMainView = iMainView;
        mainModel = new MainModel(this);
    }

    public void getData(String tel,String pwd) {
        mainModel.getData(tel,pwd);
    }
    @Override
    public void onSuccess(LoginBean loginBean) {
        iMainView.onSuccess(loginBean);
    }
}
