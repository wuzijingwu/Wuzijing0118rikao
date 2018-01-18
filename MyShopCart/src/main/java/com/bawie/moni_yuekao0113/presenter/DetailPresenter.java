package com.bawie.moni_yuekao0113.presenter;

import com.bawie.moni_yuekao0113.bean.ProductDetailBean;
import com.bawie.moni_yuekao0113.interfac.IDetailPresenter;
import com.bawie.moni_yuekao0113.interfac.IDetailView;
import com.bawie.moni_yuekao0113.model.DetailModel;



public class DetailPresenter implements IDetailPresenter{
    private IDetailView iDetailView;
    private  DetailModel detailModel;

    public DetailPresenter(IDetailView iDetailView) {
        this.iDetailView = iDetailView;
        detailModel = new DetailModel(this);
    }
    public void getData(int pid) {
        detailModel.getData(pid);
    }
    @Override
    public void onSuccess(ProductDetailBean productDetailBean) {
        iDetailView.onSuccess(productDetailBean);
    }


}
