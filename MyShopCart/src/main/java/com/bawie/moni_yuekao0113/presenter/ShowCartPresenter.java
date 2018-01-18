package com.bawie.moni_yuekao0113.presenter;

import com.bawie.moni_yuekao0113.bean.QueryCarts;
import com.bawie.moni_yuekao0113.interfac.IShowCartsPresenter;
import com.bawie.moni_yuekao0113.interfac.IShowCartsView;
import com.bawie.moni_yuekao0113.model.ShowCartModel;



public class ShowCartPresenter implements IShowCartsPresenter {
    private IShowCartsView iShowCartsView;
    private final ShowCartModel showCartModel;

    public ShowCartPresenter(IShowCartsView iShowCartsView) {
        this.iShowCartsView = iShowCartsView;
        showCartModel = new ShowCartModel(this);
    }

    public void getData() {
        showCartModel.getData();
    }
    @Override
    public void onSuccess(QueryCarts showCartaBean) {
        iShowCartsView.onSuccess(showCartaBean);
    }

    //取消与activity的绑定
    public void onUnBind() {
        if(iShowCartsView != null){
            iShowCartsView = null;
        }
    }
}
