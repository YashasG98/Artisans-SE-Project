package com.example.artisansfinal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static android.view.View.VISIBLE;

public class OHAdapter extends RecyclerView.Adapter<OHAdapter.OHViewHolder> {

    private ArrayList<orderInfo> order;
    private Context context;
    private String className;

    public static class OHViewHolder extends RecyclerView.ViewHolder{
        TextView productName;
        ImageView image;
        TextView productPrice;
        TextView date;
        RelativeLayout layout;
        TextView reviewTextView;
        CardView cardView;
        public OHViewHolder(@NonNull View itemView, String className){
            super(itemView);
            productName=itemView.findViewById(R.id.orderHistory_tv_product_name);
            image=itemView.findViewById(R.id.orderHistory_iv_product_image);
            productPrice=itemView.findViewById(R.id.orderHistory_tv_product_price);
            date=itemView.findViewById(R.id.orderHistory_tv_date);
            layout=itemView.findViewById(R.id.orderHistoryRL);
            reviewTextView = itemView.findViewById(R.id.review);
            cardView = itemView.findViewById(R.id.orderHistoryCv);



        }
    }

    public OHAdapter(Context context,ArrayList<orderInfo> order, String className){
        this.order=order;
        this.context=context;
        this.className = className;
    }

    @NonNull
    @Override
    public OHViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,int i){
        View view;
        LayoutInflater inflater=LayoutInflater.from(context);
        view=inflater.inflate(R.layout.order_history_layout,viewGroup,false);
        OHViewHolder viewHolder=new OHViewHolder(view, this.className);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OHViewHolder viewHolder, final int i){
        final orderInfo orderX=order.get(i);
        viewHolder.productName.setText(orderX.getName());
        viewHolder.date.setText(orderX.getDate());
        viewHolder.productPrice.setText(orderX.getPrice());

        if(className.equals("UserCompletedOrderHistoryFragment"))
        {
            viewHolder.reviewTextView.setClickable(true);
            viewHolder.reviewTextView.setVisibility(VISIBLE);
            viewHolder.reviewTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Reviews and Ratings");
                    builder.setView(R.layout.review_ratings);
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });


                    builder.show();

                }
            });
        }

    }

    @Override
    public int getItemCount(){ return order.size();}

    public void added(orderInfo orderX){
        order.add(orderX);
        notifyItemInserted(order.indexOf(orderX));
    }
}

