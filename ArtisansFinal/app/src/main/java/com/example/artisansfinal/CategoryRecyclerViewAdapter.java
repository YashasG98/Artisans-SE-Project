package com.example.artisansfinal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder>{

    private ArrayList<ProductInfo> info;
    private Context context;

    private StorageReference storageReference;

    final long ONE_MB = 1024*1024;

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView productName;
        ImageView image;
        TextView productPrice;
        TextView artisanName;
        RelativeLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.category_layout_tv_product_name);
            image = itemView.findViewById(R.id.category_layout_iv_product_image);
            productPrice = itemView.findViewById(R.id.category_layout_tv_product_price);
            artisanName = itemView.findViewById(R.id.category_layout_tv_artisan_name);
            layout = itemView.findViewById(R.id.category_layout_rl);
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
        view = inflater.inflate(R.layout.category_layout,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final ProductInfo productInfo = info.get(i);
        storageReference = FirebaseStorage.getInstance().getReference().child("ProductImages/"+productInfo.getProductID());
        viewHolder.artisanName.setText(productInfo.getArtisanName());
        viewHolder.productPrice.setText(productInfo.getProductPrice());
        viewHolder.productName.setText(productInfo.getProductName());

            storageReference.getBytes(ONE_MB).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Glide.with(context).load(bytes).into(viewHolder.image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "IMAGE Load Failed: " + productInfo.getProductName(), Toast.LENGTH_SHORT).show();
                    Log.d("FAIL: ", e.toString());
                }
            });




        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, productInfo.getProductName(), Toast.LENGTH_LONG).show();
                Log.d("SELECTION", v.toString());
                Log.d("SELECTION", productInfo.toString());
                String prodName = productInfo.getProductName();
                String prodCategory = productInfo.getProductCategory();
                Intent newintent = new Intent(context, ProductPageActivity.class);

                Drawable drawable = viewHolder.image.getDrawable();
                Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] b = baos.toByteArray();
                newintent.putExtra("productImage", b);

                newintent.putExtra("productName", prodName);
                newintent.putExtra("productCategory",prodCategory);
                newintent.putExtra("productInfo", (Parcelable)productInfo);
                Log.d("pINFO", productInfo.toString());
                context.startActivity(newintent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return info.size();
    }

    public void added(ProductInfo productInfo){
        info.add(productInfo);
        notifyItemInserted(info.indexOf(productInfo));
    }
}
