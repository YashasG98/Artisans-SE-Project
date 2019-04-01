package com.example.artisansfinal;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class UserHomePageSlideshowRecyclerViewAdapter extends RecyclerView.Adapter<UserHomePageSlideshowRecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Integer> images;

    public UserHomePageSlideshowRecyclerViewAdapter(Context context, ArrayList<Integer> images) {
        this.context = context;
        this.images = images;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.user_home_page_slideshow_iv_slideshowimage);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_home_page_slideshow_layout,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Glide.with(context).asBitmap().load(images.get(i)).into(viewHolder.image);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }
}
