package com.bawie.moni_yuekao0113.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bawie.moni_yuekao0113.R;
import com.bawie.moni_yuekao0113.bean.ShowProductBean;
import com.bawie.moni_yuekao0113.holder.MyHolder;

import java.util.List;



public class MyRVAdapter extends RecyclerView.Adapter<MyHolder> implements View.OnClickListener{
    Context context;
    List<ShowProductBean.TuijianBean.ListBean> list;
    //2.声明接口
    private OnItemClickListener mOnItemClickListener = null;
    //暴露一个方法给调用者

    public void setMOnItemClickLisenter(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public MyRVAdapter(Context context, List<ShowProductBean.TuijianBean.ListBean> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_rlvchild,parent,false);
        //3.为每个视图都添加点击事件
        view.setOnClickListener(this);
        MyHolder myHolder = new MyHolder(view);

        return myHolder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        ShowProductBean.TuijianBean.ListBean listBean = list.get(position);
        String images = listBean.getImages();
        String[] split = images.split("\\|");
        Uri uri = Uri.parse(split[0]);
        holder.rlvchild_sdv.setImageURI(uri);
        holder.rlvchild_price.setText("¥:"+listBean.getPrice());
        holder.rlvchild_title.setText(listBean.getTitle());
        //5.设置每个条目设置position
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    //4.将点击事件转移给外面的调用者
    @Override
    public void onClick(View view) {
        if(mOnItemClickListener != null){
            mOnItemClickListener.onItemClick(view, (int) view.getTag());
        }
    }

    //1.定义接口
    public static interface OnItemClickListener {
        void onItemClick(View view , int position);
    }
}
