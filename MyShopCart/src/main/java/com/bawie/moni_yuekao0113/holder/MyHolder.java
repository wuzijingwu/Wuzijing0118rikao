package com.bawie.moni_yuekao0113.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.bawie.moni_yuekao0113.R;
import com.facebook.drawee.view.SimpleDraweeView;



public class MyHolder extends RecyclerView.ViewHolder {

    public SimpleDraweeView rlvchild_sdv;
    public TextView rlvchild_price;
    public TextView rlvchild_title;

    public MyHolder(View itemView) {
        super(itemView);
        rlvchild_sdv = itemView.findViewById(R.id.rlvchild_sdv);
        rlvchild_price = itemView.findViewById(R.id.rlvchild_price);
        rlvchild_title = itemView.findViewById(R.id.rlvchild_title);
    }
}
