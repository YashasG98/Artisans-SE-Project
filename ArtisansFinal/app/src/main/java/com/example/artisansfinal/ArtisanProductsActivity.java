package com.example.artisansfinal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import java.util.HashMap;

public class ArtisanProductsActivity extends AppCompatActivity {

    private ArrayList<ProductInfo> productInfos = new ArrayList<ProductInfo>();
    private ArrayList<ProductInfo> searchResults = new ArrayList<>();
    private ArtisanProductsRecyclerViewAdapter artisanProductsRecyclerViewAdapter;
    private DatabaseReference databaseReference;
    private RelativeLayout contentLayout;
    private RelativeLayout loadingLayout;
    private RelativeLayout notFoundLayout;
    private ImageView loading;
    private String artisanPhoneNumber;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artisan_products);
        loading = findViewById(R.id.artisan_products_iv);
        Glide.with(getApplicationContext())
                .asGif()
                .load(R.mipmap.loading2)
                .into(loading);
        contentLayout = findViewById(R.id.artisan_products_content_rl);
        contentLayout.setVisibility(View.GONE);
        loadingLayout = findViewById(R.id.artisan_products_loading_rl);
        loadingLayout.setVisibility(View.VISIBLE);
        notFoundLayout = findViewById(R.id.artisan_products_not_found_rl);
        notFoundLayout.setVisibility(View.GONE);
        searchView = findViewById(R.id.artisan_product_sv_search);

        Intent i = getIntent();
        artisanPhoneNumber = i.getStringExtra("phoneNumber");

        final RecyclerView recyclerView = findViewById(R.id.artisan_products_content_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        artisanProductsRecyclerViewAdapter = new ArtisanProductsRecyclerViewAdapter(this, productInfos);
        recyclerView.setAdapter(artisanProductsRecyclerViewAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchResults.clear();
                for(ProductInfo product : productInfos){
                    if(product.getProductName().toLowerCase().contains(query)){
                        searchResults.add(product);
                    }
                }
                artisanProductsRecyclerViewAdapter = new ArtisanProductsRecyclerViewAdapter(getBaseContext(),searchResults);
                recyclerView.setAdapter(artisanProductsRecyclerViewAdapter);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchResults.clear();
                for(ProductInfo products: productInfos){
                    if(products.getProductName().toLowerCase().startsWith(newText)){
                        searchResults.add(products);
                    }
                }
                artisanProductsRecyclerViewAdapter = new ArtisanProductsRecyclerViewAdapter(getBaseContext(),searchResults);
                recyclerView.setAdapter(artisanProductsRecyclerViewAdapter);
                return false;
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("ArtisanProducts/"+artisanPhoneNumber);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    databaseReference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if(contentLayout.getVisibility()==View.GONE){
                                contentLayout.setVisibility(View.VISIBLE);
                                loadingLayout.setVisibility(View.GONE);
                            }
                            ProductInfo productInfo;
                            HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.getValue();
                            Log.d("MAP", dataSnapshot.toString());
                            productInfo = new ProductInfo(map.get("productID"), map.get("productName"), map.get("productDescription"),
                                    map.get("productCategory"), map.get("productPrice"), map.get("artisanName"),
                                    map.get("artisanContactNumber"));
                            productInfo.setNumberOfPeopleWhoHaveRated(map.get("numberOfPeopleWhoHaveRated"));
                            productInfo.setTotalRating(map.get("totalRating"));
                            artisanProductsRecyclerViewAdapter.added(productInfo);
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
                    loadingLayout.setVisibility(View.GONE);
                    notFoundLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
