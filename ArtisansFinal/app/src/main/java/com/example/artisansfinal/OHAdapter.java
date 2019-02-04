package com.example.artisansfinal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class OHAdapter extends RecyclerView.Adapter<OHAdapter.OHViewHolder> {

    private ArrayList<orderInfo> order;
    private Context context;

    public static class OHViewHolder extends RecyclerView.ViewHolder{
        TextView productName;
        ImageView image;
        TextView productPrice;
        TextView date;
        RelativeLayout layout;
        public OHViewHolder(@NonNull View itemView){
            super(itemView);
            productName=itemView.findViewById(R.id.orderHistory_tv_product_name);
            image=itemView.findViewById(R.id.orderHistory_iv_product_image);
            productPrice=itemView.findViewById(R.id.orderHistory_tv_product_price);
            date=itemView.findViewById(R.id.orderHistory_tv_date);
            layout=itemView.findViewById(R.id.orderHistoryRL);
        }
    }

    public OHAdapter(Context context,ArrayList<orderInfo> order){
        this.order=order;
        this.context=context;
    }

    @NonNull
    @Override
    public OHViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,int i){
        View view;
        LayoutInflater inflater=LayoutInflater.from(context);
        view=inflater.inflate(R.layout.order_history_layout,viewGroup,false);
        OHViewHolder viewHolder=new OHViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OHViewHolder viewHolder, final int i){
        final orderInfo orderX=order.get(i);
        viewHolder.productName.setText(orderX.getName());
        viewHolder.date.setText(orderX.getDate());
        viewHolder.productPrice.setText(orderX.getPrice());
    }

    @Override
    public int getItemCount(){ return order.size();}

    public void added(orderInfo orderX){
        order.add(orderX);
        notifyItemInserted(order.indexOf(orderX));
    }
}

