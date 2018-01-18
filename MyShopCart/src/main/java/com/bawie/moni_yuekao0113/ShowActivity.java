package com.bawie.moni_yuekao0113;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bawie.moni_yuekao0113.adapter.MyRVAdapter;
import com.bawie.moni_yuekao0113.bean.MyEventBusMessage;
import com.bawie.moni_yuekao0113.bean.ShowProductBean;
import com.bawie.moni_yuekao0113.interfac.IShowView;
import com.bawie.moni_yuekao0113.presenter.ShowPresenter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ShowActivity extends AppCompatActivity implements IShowView {

    private ShowPresenter showPresenter;
    private RecyclerView show_rlv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        initView();
        showPresenter = new ShowPresenter(this);
        showPresenter.getData();
    }

    @Override
    public void onSuccess(ShowProductBean showProductBean) {
        if ("0".equals(showProductBean.getCode())) {
            //设置适配器
            final List<ShowProductBean.TuijianBean.ListBean> list = showProductBean.getTuijian().getList();
            MyRVAdapter myRVAdapter = new MyRVAdapter(ShowActivity.this,list);
            show_rlv.setAdapter(myRVAdapter);
            //点击条目跳转详情
            myRVAdapter.setMOnItemClickLisenter(new MyRVAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    //使用EventBus进行传值
                    EventBus.getDefault().postSticky(new MyEventBusMessage(list.get(position).getPid()));
                    Intent intent = new Intent(ShowActivity.this, DetailActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void initView() {
        show_rlv = (RecyclerView) findViewById(R.id.show_rlv);
        //设置线性管理器
        show_rlv.setLayoutManager(new LinearLayoutManager(ShowActivity.this));
    }

}
