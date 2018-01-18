package com.bawie.moni_yuekao0113.presenter;

import com.bawie.moni_yuekao0113.bean.ShowProductBean;
import com.bawie.moni_yuekao0113.interfac.IShowPresenter;
import com.bawie.moni_yuekao0113.interfac.IShowView;
import com.bawie.moni_yuekao0113.model.ShowModel;



public class ShowPresenter implements IShowPresenter {
    private IShowView iShowView;
    private final ShowModel showModel;

    public ShowPresenter(IShowView iShowView) {
        this.iShowView = iShowView;
        showModel = new ShowModel(this);
    }

    public void getData() {
        showModel.getData();
    }
    @Override
    public void onSuccess(ShowProductBean showProductBean) {
        iShowView.onSuccess(showProductBean);
    }


}
