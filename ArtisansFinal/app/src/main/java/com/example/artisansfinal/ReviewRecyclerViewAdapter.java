package com.example.artisansfinal;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import io.reactivex.annotations.NonNull;

public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<ReviewRecyclerViewAdapter.ViewHolder> {

    private ArrayList<ProductReview> reviews;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView userName;
        RatingBar ratingBar;
        TextView review;

        public ViewHolder(@NonNull View itemView){
            super(itemView);
            userName = itemView.findViewById(R.id.review_layout_tv_name);
            ratingBar = itemView.findViewById(R.id.review_layout_rb);
            review = itemView.findViewById(R.id.review_layout_tv_review);
        }
    }

    public ReviewRecyclerViewAdapter(Context context, ArrayList<ProductReview> reviews){
        this.context = context;
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i){
        View view;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        view = layoutInflater.inflate(R.layout.review_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i){

        final ProductReview productReview = reviews.get(i);
        viewHolder.userName.setText(productReview.getUserName());
        viewHolder.ratingBar.setNumStars(Integer.parseInt(productReview.getRating()));
        viewHolder.review.setText(productReview.getReview());
    }

    @Override
    public int getItemCount(){ return reviews.size(); }

    public void added(ProductReview productReview){
        reviews.add(productReview);
        notifyItemInserted(reviews.indexOf(productReview));
    }

}
