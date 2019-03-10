package com.example.artisansfinal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.PicassoProvider;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import static com.bumptech.glide.Glide.with;
import static com.squareup.picasso.Picasso.*;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {

    private ArrayList<ProductInfo> info;
    private Context context;
//    private boolean imageLoaded = false;

    private StorageReference storageReference;

    final long ONE_MB = 1024 * 1024;

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView productName;
        ImageView image;
        TextView productPrice;
        TextView artisanName;
        RelativeLayout layout;
//        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.category_layout_tv_product_name);
            image = itemView.findViewById(R.id.category_layout_iv_product_image);
            productPrice = itemView.findViewById(R.id.category_layout_tv_product_price);
            artisanName = itemView.findViewById(R.id.category_layout_tv_artisan_name);
            layout = itemView.findViewById(R.id.category_layout_rl);
//            progressBar = itemView.findViewById(R.id.category_layout_pb_progress);
        }
    }

    public CategoryRecyclerViewAdapter(Context context, ArrayList<ProductInfo> info) {
        this.info = info;
        this.context = context;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.category_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final ProductInfo productInfo = info.get(i);
        storageReference = FirebaseStorage.getInstance().getReference("ProductImages/LowRes/" + productInfo.getProductID());
        Log.d("STORAGE", productInfo.getProductID());
        viewHolder.artisanName.setText(productInfo.getArtisanName());
        viewHolder.productPrice.setText(productInfo.getProductPrice());
        viewHolder.productName.setText(productInfo.getProductName());

        Glide.with(context)
                .asGif()
                .load(R.mipmap.loading1)
                .into(viewHolder.image);

//        storageReference.getBytes(ONE_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//            @Override
//            public void onSuccess(byte[] bytes) {
//                Glide.with(context)
//                        .load(bytes)
//                        .listener(new RequestListener<Drawable>() {
//                            @Override
//                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
////                                viewHolder.progressBar.setVisibility(View.GONE);
//                                Toast.makeText(context,"Failed: " + productInfo.getProductName(), Toast.LENGTH_LONG).show();
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
////                                viewHolder.progressBar.setVisibility(View.GONE);
////                                imageLoaded = true;
//                                return false;
//                            }
//                        })
//                        .into(viewHolder.image);
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
////                Toast.makeText(context, "IMAGE Load Failed: " + productInfo.getProductName(), Toast.LENGTH_SHORT).show();
//                Glide.with(context)
//                        .load(R.mipmap.not_found)
//                        .into(viewHolder.image);
//                Log.d("FAIL: ", e.toString());
//            }
//        });
        RequestOptions options = new RequestOptions().error(R.mipmap.not_found);
        GlideApp.with(context)
                .load(storageReference)
                .apply(options)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(viewHolder.image);


        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, productInfo.getProductName(), Toast.LENGTH_LONG).show();
//                if(imageLoaded) {
                    Log.d("SELECTION", v.toString());
                    Log.d("SELECTION", productInfo.toString());
                    String prodName = productInfo.getProductName();
                    String prodCategory = productInfo.getProductCategory();
                    Intent newintent = new Intent(context, UserProductPageTabbedActivity.class);

//                    Drawable drawable = viewHolder.image.getDrawable();
//                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                    byte[] b = baos.toByteArray();
//                    newintent.putExtra("productImage", b);

                    newintent.putExtra("productName", prodName);
                    newintent.putExtra("productCategory", prodCategory);
                    newintent.putExtra("productID", productInfo.getProductID());
                    newintent.putExtra("productInfo", (Parcelable) productInfo);
                    Log.d("pINFO", productInfo.toString());
                    context.startActivity(newintent);
//                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return info.size();
    }

    public void added(ProductInfo productInfo) {
        info.add(productInfo);
        notifyItemInserted(info.indexOf(productInfo));
    }
}
