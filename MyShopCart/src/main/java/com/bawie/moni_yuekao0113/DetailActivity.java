package com.bawie.moni_yuekao0113;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bawie.moni_yuekao0113.bean.MyEventBusMessage;
import com.bawie.moni_yuekao0113.bean.ProductDetailBean;
import com.bawie.moni_yuekao0113.interfac.IDetailView;
import com.bawie.moni_yuekao0113.interfac.SerViceAPI;
import com.bawie.moni_yuekao0113.presenter.DetailPresenter;
import com.bawie.moni_yuekao0113.util.API;
import com.bawie.moni_yuekao0113.util.RetrofitHepler;
import com.facebook.drawee.view.SimpleDraweeView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class DetailActivity extends AppCompatActivity implements IDetailView{

    private TextView title;
    private SimpleDraweeView detail_sdv;
    private TextView detail_title;
    private TextView detail_price;
    private TextView detail_seller;
    private LinearLayout detail_linear;
    private DetailPresenter presenter;
    private int msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initView();
        //注册EventBus
        EventBus.getDefault().register(this);

    }

    private void initView() {
        title = (TextView) findViewById(R.id.title);
        detail_sdv = (SimpleDraweeView) findViewById(R.id.detail_sdv);
        detail_title = (TextView) findViewById(R.id.detail_title);
        detail_price = (TextView) findViewById(R.id.detail_price);
        detail_seller = (TextView) findViewById(R.id.detail_seller);
        detail_linear = (LinearLayout) findViewById(R.id.detail_linear);
    }
    //写一个方法用来接收值,使用注解回到主线程
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void resiverMeassge(MyEventBusMessage myEventBusMessage){
        msg = myEventBusMessage.msg;
        //得到商品id
        Toast.makeText(DetailActivity.this,myEventBusMessage.msg+"",Toast.LENGTH_SHORT).show();
        presenter = new DetailPresenter(this);
        presenter.getData(myEventBusMessage.msg);
    }

    //取消注册EventBus
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onSuccess(ProductDetailBean productDetailBean) {
        if("0".equals(productDetailBean.getCode())){
            String images = productDetailBean.getData().getImages();
            String[] split = images.split("\\|");
            detail_sdv.setImageURI(Uri.parse(split[0]));
            detail_title.setText(productDetailBean.getData().getTitle());
            detail_price.setText("¥:"+productDetailBean.getData().getPrice());
            detail_seller.setText(productDetailBean.getSeller().getName());
        }
    }
    //加入购物车
    public void addCart(View view) {
        SerViceAPI serViceAPI = RetrofitHepler.getSerViceAPI(API.BASE_URL);
        Map<String, String> map = new HashMap<>();
        map.put("uid",String.valueOf(2753));
        map.put("pid",String.valueOf(msg));
        Observable<String> stringObservable = serViceAPI.getDataByPost("product/addCart", map);
        stringObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d("detail",s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            String code = (String) jsonObject.get("code");
                            String msg = (String) jsonObject.get("msg");
                            if("0".equals(code)){
                                Toast.makeText(DetailActivity.this,msg,Toast.LENGTH_LONG).show();
                                startActivity(new Intent(DetailActivity.this,ShowCartsActivity.class));
                            }else{
                                Toast.makeText(DetailActivity.this,msg,Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

    }
}
