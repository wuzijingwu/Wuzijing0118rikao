package com.bawie.moni_yuekao0113.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.bawie.moni_yuekao0113.R;
import com.bawie.moni_yuekao0113.bean.QueryCarts;
import com.bawie.moni_yuekao0113.custom.NumberAddSubView;
import com.bawie.moni_yuekao0113.interfac.SerViceAPI;
import com.bawie.moni_yuekao0113.presenter.ShowCartPresenter;
import com.bawie.moni_yuekao0113.util.API;
import com.bawie.moni_yuekao0113.util.RetrofitHepler;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;



public class MyExpanableAdapter extends BaseExpandableListAdapter {
    Context context;
    private ShowCartPresenter showCartPresenter;

    private final List<QueryCarts.DataBean> data;
    private List<QueryCarts.DataBean.ListBean> allchildlist = new ArrayList<>();

    public MyExpanableAdapter(Context context, QueryCarts queryCarts, ShowCartPresenter showCartPresenter) {
        this.context = context;
        this.showCartPresenter = showCartPresenter;
        data = queryCarts.getData();
    }

    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return data.get(i).getList().size();
    }

    @Override
    public Object getGroup(int i) {
        return data.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return data.get(i).getList().get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        final GroupHolder gh;
        if(view == null){
            gh = new GroupHolder();
            view = View.inflate(context, R.layout.layout_group,null);
            gh.group_check = (CheckBox) view.findViewById(R.id.group_check);
            gh.seller_name = (TextView) view.findViewById(R.id.seller_name);
            view.setTag(gh);
        }else{
            gh = (GroupHolder) view.getTag();
        }
        final QueryCarts.DataBean dataBean = data.get(i);
        gh.group_check.setChecked(dataBean.isGroupCheck());
        gh.seller_name.setText(dataBean.getSellerName());
        //设置点击事件
        gh.group_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<QueryCarts.DataBean.ListBean> list = dataBean.getList();
                allchildlist.addAll(list);
                //根据组条目的状态，去更改子条目的状态
                updateCartByGroup(gh.group_check.isChecked());

            }
        });
        return view;
    }

    private void updateCartByGroup(final boolean groupCheck) {
        List<Observable> observables = new ArrayList<>();
        //得到第一个子条目对象
        for(int j = 0;j < allchildlist.size();j++){
            Map<String,String> map = new HashMap<>();
            QueryCarts.DataBean.ListBean childbean = allchildlist.get(j);
            SerViceAPI serViceAPI = RetrofitHepler.getSerViceAPI(API.BASE_URL);

            map.put("sellerid",childbean.getSellerid()+"");
            map.put("pid",childbean.getPid()+"");
            map.put("uid","2753");
            map.put("num",childbean.getNum()+"");
            map.put("selected",String.valueOf(groupCheck?1:0));

            Observable<String> stringObservable = serViceAPI.getDataByPost("product/updateCarts", map);
            observables.add(stringObservable);
        }

        Observable.merge(observables.toArray(new Observable[observables.size()]))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1() {
                    @Override
                    public void call(Object o) {
                        //更新购物车
                        showCartPresenter.getData();
                    }
                });

    }

    @Override
    public View getChildView(final int i, final int i1, boolean b, View view, ViewGroup viewGroup) {
        final ChildHolder ch;
        if(view == null){
            ch = new ChildHolder();
            view = View.inflate(context,R.layout.layout_child,null);
            ch.child_check = (CheckBox) view.findViewById(R.id.child_check);
            ch.child_spv = (SimpleDraweeView) view.findViewById(R.id.child_spv);
            ch.child_title = (TextView) view.findViewById(R.id.child_title);
            ch.child_price = (TextView) view.findViewById(R.id.child_price);
            ch.child_bargainprice = (TextView) view.findViewById(R.id.child_bargainprice);
            ch.numberaddsubview = view.findViewById(R.id.numberaddsubview);
            ch.child_del = view.findViewById(R.id.child_del);
            view.setTag(ch);
        }else{
            ch = (ChildHolder) view.getTag();
        }
        final QueryCarts.DataBean.ListBean listBean = data.get(i).getList().get(i1);
        //使用fresco设置图片
        String images = listBean.getImages();
        String[] split = images.split("\\|");
        ch.child_spv.setImageURI(Uri.parse(split[0]));

        ch.child_check.setChecked(listBean.getSelected() == 0 ? false:true);
        ch.child_price.setText("￥："+listBean.getPrice());
        ch.child_bargainprice.setText("￥："+listBean.getBargainPrice());
        ch.child_title.setText(listBean.getTitle());
        ch.numberaddsubview.setValue(listBean.getNum());


        //设置子条目点击事件，点击子条目时，，如果所有的子条目都选中，组的状态也选中
        ch.child_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allchildlist.add(listBean);
                //更新接口
                updateCartByChild(listBean);
            }
        });
        //给自定义view设置点击事件,重写添加数量和减少的方法
        ch.numberaddsubview.setOnButtonClickListenter(new NumberAddSubView.OnButtonClickListenter() {
            @Override
            public void onButtonAddClick(View view, int value) {
                allchildlist.add(listBean);
                addOrJian(listBean,ch.numberaddsubview.getValue());
            }

            @Override
            public void onButtonSubClick(View view, int value) {
                allchildlist.add(listBean);
                addOrJian(listBean,ch.numberaddsubview.getValue());
            }
        });
        //删除
        ch.child_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listBean.getSelected() == 1){
                    Map<String,String> map = new HashMap<String, String>();
                    map.put("uid","2753");
                    map.put("pid",listBean.getPid()+"");
                    RetrofitHepler.getSerViceAPI(API.BASE_URL).getDataByPost("product/deleteCart",map)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<String>() {
                                @Override
                                public void call(String s) {
                                    //删除后更新购物车
                                    showCartPresenter.getData();
                                }
                            });
                }else{
                    Toast.makeText(context,"请勾选",Toast.LENGTH_LONG).show();
                }

            }
        });
        return view;
    }

    //num的加减
    private void addOrJian(QueryCarts.DataBean.ListBean listBean,int num) {
        List<Observable> observables = new ArrayList<>();
        for(int j = 0;j < allchildlist.size();j++){
            //请求更新数据的接口
            SerViceAPI serViceAPI = RetrofitHepler.getSerViceAPI(API.BASE_URL);
            Map<String,String> map = new HashMap<>();

            map.put("sellerid",String.valueOf(listBean.getSellerid()));
            map.put("pid",listBean.getPid()+"");
            map.put("uid","2753");
            map.put("selected",String.valueOf(listBean.getSelected()));
            map.put("num",String.valueOf(num));
            //调用接口中更新购物车的方法
            Observable<String> stringObservable = serViceAPI.getDataByPost("product/updateCarts", map);
            observables.add(stringObservable);
        }
        Observable.merge(observables.toArray(new Observable[allchildlist.size()]))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Action1() {
                    @Override
                    public void call(Object o) {
                        //更新购物车
                        showCartPresenter.getData();
                    }
                });


    }

    //更新购物车，通过点击子条目
    private void updateCartByChild(final QueryCarts.DataBean.ListBean listBean) {
        List<Observable> observables = new ArrayList<>();
        //请求更新数据的接口
        for(int j = 0;j < allchildlist.size();j++){
            SerViceAPI serViceAPI = RetrofitHepler.getSerViceAPI(API.BASE_URL);

            Map<String,String> map = new HashMap<>();
            map.put("sellerid",String.valueOf(listBean.getSellerid()));
            map.put("pid",listBean.getPid()+"");
            map.put("uid","2753");
            map.put("num",listBean.getNum()+"");
            map.put("selected",String.valueOf(listBean.getSelected() == 0?1:0));
            //调用接口中更新购物车的方法
            Observable<String> stringObservable = serViceAPI.getDataByPost("product/updateCarts", map);
            observables.add(stringObservable);
        }

        Observable.merge(observables.toArray(new Observable[observables.size()]))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1() {
                    @Override
                    public void call(Object o) {
                        //更新购物车
                        showCartPresenter.getData();
                    }
                });


    }

    class GroupHolder{
        CheckBox group_check;
        TextView seller_name;
    }
    class ChildHolder{
        Button child_del;
        TextView child_title;
        TextView child_price;
        TextView child_bargainprice;
        CheckBox child_check;
        SimpleDraweeView child_spv;
        NumberAddSubView numberaddsubview;
    }

}
