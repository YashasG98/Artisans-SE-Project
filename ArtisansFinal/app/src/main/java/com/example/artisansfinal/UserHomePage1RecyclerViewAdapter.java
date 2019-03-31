package com.example.artisansfinal;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import io.reactivex.annotations.NonNull;

public class UserHomePage1RecyclerViewAdapter extends RecyclerView.Adapter<UserHomePage1RecyclerViewAdapter.ViewHolder> {

    private ArrayList<ProductInfo> info;
    private Context context;
    private StorageReference storageReference;

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView productName;
        ImageView image;
        RelativeLayout layout;
        TextView productPrice;
        AppCompatRatingBar rb;
        TextView numberRated;

        public ViewHolder(@NonNull View itemView){

            super(itemView);
            productName = itemView.findViewById(R.id.home_page_display_products_tv_product_name);
            image = itemView.findViewById(R.id.home_page_display_products_iv_product_image);
            productPrice = itemView.findViewById(R.id.home_page_display_products_tv_product_price);
            rb = itemView.findViewById(R.id.home_page_display_products_rb);
            numberRated = itemView.findViewById(R.id.home_page_display_products_tv_number_rated);
            layout = itemView.findViewById(R.id.home_page_display_products_rl);
        }
    }

    public UserHomePage1RecyclerViewAdapter(Context context, ArrayList<ProductInfo> info) {

        this.info = info;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.home_page_display_products, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @NonNull
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        final ProductInfo productInfo = info.get(i);
        storageReference = FirebaseStorage.getInstance().getReference("ProductImages/LowRes/" + productInfo.getProductID());
        Log.d("STORAGE", productInfo.getProductID());
        viewHolder.productPrice.setText(productInfo.getProductPrice());
        viewHolder.productName.setText(productInfo.getProductName());
        viewHolder.rb.setRating(Float.parseFloat(productInfo.getTotalRating()));
        viewHolder.numberRated.setText("(" + productInfo.getNumberOfPeopleWhoHaveRated() + ")");

        Glide.with(context)
                .asGif()
                .load(R.mipmap.loading1)
                .into(viewHolder.image);

        RequestOptions options = new RequestOptions().error(R.mipmap.not_found);
        GlideApp.with(context)
                .load(storageReference)
                .apply(options)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(viewHolder.image);

        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String prodName = productInfo.getProductName();
                String prodCategory = productInfo.getProductCategory();
                Intent newintent = new Intent(context, UserProductPageTabbedActivity.class);

                newintent.putExtra("productName", prodName);
                newintent.putExtra("productCategory", prodCategory);
                newintent.putExtra("productID", productInfo.getProductID());
                newintent.putExtra("productInfo", (Parcelable) productInfo);
                context.startActivity(newintent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return info.size()<8?info.size():8;
    }

    public void added(ProductInfo productInfo) {
        info.add(productInfo);
        notifyItemInserted(info.indexOf(productInfo));
    }

}
