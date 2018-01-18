package com.bawie.moni_yuekao0113;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bawie.moni_yuekao0113.adapter.MyExpanableAdapter;
import com.bawie.moni_yuekao0113.bean.QueryCarts;
import com.bawie.moni_yuekao0113.custom.MyExpanableListView;
import com.bawie.moni_yuekao0113.interfac.IShowCartsView;
import com.bawie.moni_yuekao0113.interfac.SerViceAPI;
import com.bawie.moni_yuekao0113.presenter.ShowCartPresenter;
import com.bawie.moni_yuekao0113.util.API;
import com.bawie.moni_yuekao0113.util.RetrofitHepler;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ShowCartsActivity extends AppCompatActivity implements IShowCartsView, View.OnClickListener {

    private ShowCartPresenter showCartPresenter;
    private TextView showcart_biaoti;
    private MyExpanableListView elv;
    private CheckBox ck_all;
    private TextView sum;
    private TextView buy;
    private QueryCarts queryCarts;
    private MyExpanableAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_carts);
        initView();
        showCartPresenter = new ShowCartPresenter(this);
        showCartPresenter.getData();
    }

    @Override
    public void onSuccess(QueryCarts queryCarts) {
        this.queryCarts = queryCarts;
        List<QueryCarts.DataBean> data = queryCarts.getData();
        //设置适配器
        adapter = new MyExpanableAdapter(ShowCartsActivity.this, queryCarts, showCartPresenter);
        elv.setAdapter(adapter);
        //展开所有组
        if(data != null){
            for(int i = 0; i < data.size();i++){
                elv.expandGroup(i);
            }
            //去除二级列表的样式
            elv.setGroupIndicator(null);
        }
        //1.根据组中子条目是否选中,,,决定该组是否选中...初始化一下每一组中isGroupCheck这个数据
        for(int j = 0;j < data.size();j++){
            data.get(j).setGroupCheck(childItemIsChecked(j));
        }
        //计算金额和数量
        reCountAndNum();
        //设置全选框选中状态
        ck_all.setChecked(groupItemIsChecked());
    }
    //重新计算金额和数量
    private void reCountAndNum() {
        int price = 0;
        int num = 0;
        List<QueryCarts.DataBean> data = queryCarts.getData();
        for (int i = 0; i < data.size(); i++) {
            List<QueryCarts.DataBean.ListBean> list = data.get(i).getList();
            for (int j = 0; j < list.size(); j++) {
                if (list.get(j).getSelected() == 1) {
                    price += list.get(j).getBargainPrice() * list.get(j).getNum();
                    num += list.get(j).getNum();
                }
            }
        }
        //保留精准的价格
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String formatprice = decimalFormat.format(price);
        sum.setText("合计￥：" + formatprice);
        buy.setText("去购买("+num+")");
    }

    private void initView() {
        showcart_biaoti = (TextView) findViewById(R.id.showcart_biaoti);
        elv = (MyExpanableListView) findViewById(R.id.elv);
        ck_all = (CheckBox) findViewById(R.id.ck_all);
        sum = (TextView) findViewById(R.id.sum);
        buy = (TextView) findViewById(R.id.buy);
        ck_all.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        setAllItemCheck(ck_all.isChecked());
    }
    //全选
    private void setAllItemCheck(boolean checked) {
        //创建一个集合用来存放所有的子条目
        List<QueryCarts.DataBean.ListBean> allitem = new ArrayList<>();

        for(int i = 0;i < queryCarts.getData().size();i++){
            List<QueryCarts.DataBean.ListBean> list = queryCarts.getData().get(i).getList();
            for(int j = 0; j < list.size(); j++){
                allitem.add(list.get(j));
            }
        }
        updateAllItemCheck(allitem,checked);
    }
    //请求更新数据的接口
    private void updateAllItemCheck(final List<QueryCarts.DataBean.ListBean> allitem, final boolean checked) {
        List<Observable> observables = new ArrayList<>();
        for(int j = 0; j < allitem.size();j++){
            QueryCarts.DataBean.ListBean listBean = allitem.get(j);
            SerViceAPI serViceAPI = RetrofitHepler.getSerViceAPI(API.BASE_URL);

            Map<String, String> map = new HashMap<>();
            map.put("sellerid", listBean.getSellerid() + "");
            map.put("pid", listBean.getPid() + "");
            map.put("uid", "2753");
            map.put("num", listBean.getNum() + "");
            map.put("selected", String.valueOf(checked ? 1 : 0));
            //调用接口中更新购物车的方法
            Observable<String> stringObservable = serViceAPI.getDataByPost("product/updateCarts", map);
            observables.add(stringObservable);
        }
        Observable.merge(observables.toArray(new Observable[allitem.size()]))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber() {
                    @Override
                    public void onCompleted() {
                        //重新请求数据,查询购物车
                        showCartPresenter.getData();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });

    }
    //判断一级列表是否全部选中
    private boolean groupItemIsChecked() {
        List<QueryCarts.DataBean> data = queryCarts.getData();
        for(int j = 0; j < data.size(); j++){
            if(!data.get(j).isGroupCheck()){
                return false;
            }
        }
        return true;
    }

    //判断二级列表的条目是否全部选中
    private boolean childItemIsChecked(int groupindex) {
        List<QueryCarts.DataBean.ListBean> list = queryCarts.getData().get(groupindex).getList();
        for(int j = 0; j < list.size(); j++){
            //有一个条目没选中就返回false,否则返回true
            if(list.get(j).getSelected() == 0){
                return false;
            }
        }
        return true;
    }
    //解除绑定
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (showCartPresenter != null) {
            showCartPresenter.onUnBind();
        }
    }
}
