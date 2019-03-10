package com.example.artisansfinal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.ArrayList;


public class ArtisanOrderHistoryAdapter extends RecyclerView.Adapter<ArtisanOrderHistoryAdapter.ArtisanOrderHistoryViewHolder> {

    private ArrayList<orderInfo> order;
    private Context context;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseUser userX = firebaseAuth.getCurrentUser();

    public static class ArtisanOrderHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        ImageView image;
        TextView productPrice;
        TextView dueDate;
        TextView userUID;
        RelativeLayout layout;
        CardView card;


        public ArtisanOrderHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.artisan_order_request_tv_product_name);
            image = itemView.findViewById(R.id.artisan_order_request_iv_product_image);
            productPrice = itemView.findViewById(R.id.artisan_order_request_tv_product_price);
            dueDate = itemView.findViewById(R.id.artisan_order_request_tv_date);
            userUID = itemView.findViewById(R.id.artisan_order_request_tv_user_uid);
            layout = itemView.findViewById(R.id.artisan_order_request_rl);
            card = itemView.findViewById(R.id.artisan_order_request_cv);
        }
    }

    public ArtisanOrderHistoryAdapter(Context context, ArrayList<orderInfo> order) {
        this.order = order;
        this.context = context;
    }

    @NonNull
    @Override
    public ArtisanOrderHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.artisan_order_request_layout, viewGroup, false);
        ArtisanOrderHistoryViewHolder viewHolder = new ArtisanOrderHistoryViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ArtisanOrderHistoryViewHolder viewHolder, final int i) {
        final orderInfo orderX = order.get(i);
        viewHolder.productName.setText(orderX.getName());
        viewHolder.dueDate.setText(orderX.getDate());
        viewHolder.productPrice.setText(orderX.getPrice());
        viewHolder.userUID.setText(orderX.getUserUID());
        viewHolder.userUID.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return order.size();
    }

    public void added(orderInfo orderX) {
        order.add(orderX);
        notifyItemInserted(order.indexOf(orderX));
    }

}
