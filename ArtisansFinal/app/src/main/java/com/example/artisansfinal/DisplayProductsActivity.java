package com.example.artisansfinal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class DisplayProductsActivity extends AppCompatActivity {

    private ArrayList<ProductInfo> productInfos = new ArrayList<ProductInfo>();
    private DatabaseReference databaseReference;
    private CategoryRecyclerViewAdapter categoryRecyclerViewAdapter;
    private ArrayList<ProductInfo> searchResults = new ArrayList<>();
    private String queryText = null;
    private RelativeLayout recyclerViewLayout;
    private RecyclerView recyclerView;
    private ImageView loading;

    private RelativeLayout contentLayout;
    private RelativeLayout loadingLayout;
    private RelativeLayout notFoundLayout;
    private RelativeLayout noMatchLayout;

    private int quantity = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_products);
        loading = findViewById(R.id.display_products_loading_iv);
        Glide.with(getApplicationContext())
                .asGif()
                .load(R.mipmap.loading2)
                .into(loading);
        contentLayout = findViewById(R.id.display_products_content_rl);
        contentLayout.setVisibility(RelativeLayout.GONE);
        loadingLayout = findViewById(R.id.display_products_loading_rl);
        loadingLayout.setVisibility(RelativeLayout.VISIBLE);
        notFoundLayout = findViewById(R.id.display_products_not_found_outer_rl);
        notFoundLayout.setVisibility(RelativeLayout.GONE);
        noMatchLayout = findViewById(R.id.display_products_not_found_inner_rl);
        recyclerView  = findViewById(R.id.display_products_rv);
        recyclerViewLayout = findViewById(R.id.display_products_rl);

        Intent intent = getIntent();
        final String choice = intent.getStringExtra("choice");

        final SearchView searchView = findViewById(R.id.display_products_sv_search);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(this, productInfos);
        recyclerView.setAdapter(categoryRecyclerViewAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchResults.clear();
                queryText = query;

                noMatchLayout.setVisibility(View.GONE);
                recyclerViewLayout.setVisibility(View.VISIBLE);

                for(ProductInfo product: productInfos){
                    if(product.getProductName().toLowerCase().contains(query.trim().toLowerCase()))
                        searchResults.add(product);
                }
                categoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(getBaseContext(), searchResults);
                recyclerView.setAdapter(categoryRecyclerViewAdapter);

                if(searchResults.isEmpty()){
                    noMatchLayout.setVisibility(View.VISIBLE);
                    recyclerViewLayout.setVisibility(View.GONE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")){
                    categoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(getBaseContext(),productInfos);
                    recyclerView.setAdapter(categoryRecyclerViewAdapter);
                }

                searchResults.clear();

                noMatchLayout.setVisibility(View.GONE);
                recyclerViewLayout.setVisibility(View.VISIBLE);

                for(ProductInfo product: productInfos){
                    if(product.getProductName().toLowerCase().contains(newText.trim().toLowerCase()))
                        searchResults.add(product);
                }
                categoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(getBaseContext(), searchResults);
                recyclerView.setAdapter(categoryRecyclerViewAdapter);

                if(searchResults.isEmpty()){
                    noMatchLayout.setVisibility(View.VISIBLE);
                    recyclerViewLayout.setVisibility(View.GONE);
                }
                return false;
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Products/");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    databaseReference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if (contentLayout.getVisibility() == View.GONE) {
                                loadingLayout.setVisibility(View.GONE);
                                contentLayout.setVisibility(View.VISIBLE);
                                recyclerViewLayout.setVisibility(View.VISIBLE);
                                noMatchLayout.setVisibility(View.GONE);
                            }

                            ProductInfo productInfo;
                            HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.getValue();
                            productInfo = new ProductInfo(map.get("productID"), map.get("productName"), map.get("productDescription"), map.get("productCategory"), map.get("productPrice"), map.get("artisanName"), map.get("artisanContactNumber"));
                            productInfo.setTotalRating(map.get("totalRating"));
                            productInfo.setNumberOfSales(map.get("numberOfSales"));
                            productInfo.setDateOfRegistration(map.get("dateOfRegistration"));
                            productInfo.setNumberOfPeopleWhoHaveRated(map.get("numberOfPeopleWhoHaveRated"));
                            if(quantity++ < 20) {
                                categoryRecyclerViewAdapter.added(productInfo);
                            }
                            if(choice.equalsIgnoreCase("rated")){
                                Collections.sort(productInfos, new DisplayProductsActivity.SortingByRating());
                                categoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(DisplayProductsActivity.this, productInfos);
                                recyclerView.setAdapter(categoryRecyclerViewAdapter);
                            }
                            else if(choice.equalsIgnoreCase("sold")){
                                Collections.sort(productInfos, new DisplayProductsActivity.SortingBySold());
                                categoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(DisplayProductsActivity.this, productInfos);
                                recyclerView.setAdapter(categoryRecyclerViewAdapter);
                            }
                            else if(choice.equalsIgnoreCase("added")){
                                Collections.sort(productInfos, new DisplayProductsActivity.SortingByDate());
                                categoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(DisplayProductsActivity.this, productInfos);
                                recyclerView.setAdapter(categoryRecyclerViewAdapter);
                            }

                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                else{
                    loadingLayout.setVisibility(RelativeLayout.GONE);
                    notFoundLayout.setVisibility(RelativeLayout.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    class SortingByRating implements Comparator<ProductInfo>
    {

        @Override
        public int compare(ProductInfo o1, ProductInfo o2) {

            if(Float.parseFloat(o1.getTotalRating()) > Float.parseFloat(o2.getTotalRating()))
                return -1;
            else if(Float.parseFloat(o1.getTotalRating()) < Float.parseFloat(o2.getTotalRating()))
                return 1;
            else
                return 0;
        }
    }

    class SortingBySold implements Comparator<ProductInfo>
    {

        @Override
        public int compare(ProductInfo o1, ProductInfo o2) {

            if(Float.parseFloat(o1.getNumberOfSales()) > Float.parseFloat(o2.getNumberOfSales()))
                return -1;
            else if(Float.parseFloat(o1.getNumberOfSales()) < Float.parseFloat(o2.getNumberOfSales()))
                return 1;
            else
                return 0;
        }
    }

    class SortingByDate implements Comparator<ProductInfo>
    {

        @Override
        public int compare(ProductInfo o1, ProductInfo o2) {

            if(o1.getDateOfRegistration().compareTo(o2.getDateOfRegistration()) < 0)
                return 1;
            else if(o1.getDateOfRegistration().compareTo(o2.getDateOfRegistration()) > 0)
                return -1;
            else
                return 0;
        }
    }
}
