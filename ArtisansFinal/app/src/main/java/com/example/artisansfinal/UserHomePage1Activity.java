package com.example.artisansfinal;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import static java.util.Collections.sort;

public class UserHomePage1Activity extends AppCompatActivity {

    private RecyclerView bestRatedRecyclerView;
    private RecyclerView mostSoldRecyclerView;
    private RecyclerView recentlyAddedRecyclerView;
    private ArrayList<ProductInfo> bestRatedProducts = new ArrayList<>();
    private ArrayList<ProductInfo> mostSoldProducts = new ArrayList<>();
    private ArrayList<ProductInfo> recentlyAddedProducts = new ArrayList<>();
    private ArrayList<ProductInfo> productInfos = new ArrayList<>();
    private DatabaseReference databaseReference;
    private ArrayList<ProductInfo> searchResults = new ArrayList<>();
    private LinearLayout contentLayout;
    private LinearLayout searchLayout;
    private UserHomePage1RecyclerViewAdapter bestRatedRecyclerViewAdapter;
    private UserHomePage1RecyclerViewAdapter mostSoldRecyclerViewAdapter;
    private UserHomePage1RecyclerViewAdapter recentlyAddedRecyclerViewAdapter;

    private String queryText = null;
    private RecyclerView searchRecyclerView;
    private CategoryRecyclerViewAdapter searchRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page1);

        final LinearLayout contentLayout = findViewById(R.id.user_home_page1_ll_content);
        final LinearLayout searchLayout = findViewById(R.id.user_home_page1_ll_search);
        searchLayout.setVisibility(View.GONE);

        bestRatedRecyclerView = findViewById(R.id.user_home_page1_srv_best_rated);
        bestRatedRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        bestRatedRecyclerViewAdapter = new UserHomePage1RecyclerViewAdapter(this, bestRatedProducts);
//        SnapHelper snapHelper1 = new PagerSnapHelper();
//        snapHelper1.attachToRecyclerView(bestRatedRecyclerView);
        bestRatedRecyclerView.setAdapter(bestRatedRecyclerViewAdapter);

        mostSoldRecyclerView = findViewById(R.id.user_home_page1_srv_most_sold);
        mostSoldRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mostSoldRecyclerViewAdapter = new UserHomePage1RecyclerViewAdapter(this, mostSoldProducts);
//        SnapHelper snapHelper2 = new PagerSnapHelper();
//        snapHelper2.attachToRecyclerView(mostSoldRecyclerView);
        mostSoldRecyclerView.setAdapter(mostSoldRecyclerViewAdapter);

        recentlyAddedRecyclerView = findViewById(R.id.user_home_page1_srv_recently_added);
        recentlyAddedRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recentlyAddedRecyclerViewAdapter = new UserHomePage1RecyclerViewAdapter(this, recentlyAddedProducts);
        recentlyAddedRecyclerView.setAdapter(recentlyAddedRecyclerViewAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Categories/Saree/"); //needs to be changed
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                ProductInfo productInfo;
                HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.getValue();
                productInfo = new ProductInfo(map.get("productID"), map.get("productName"), map.get("productDescription"), map.get("productCategory"), map.get("productPrice"), map.get("artisanName"), map.get("artisanContactNumber"));
                productInfo.setTotalRating(map.get("totalRating"));
                productInfo.setNumberOfPeopleWhoHaveRated(map.get("numberOfPeopleWhoHaveRated"));
                productInfo.setNumberOfSales(map.get("numberOfSales"));
                productInfo.setDateOfRegistration(map.get("dateOfRegistration"));
                bestRatedRecyclerViewAdapter.added(productInfo);
                mostSoldRecyclerViewAdapter.added(productInfo);
                searchRecyclerViewAdapter.added(productInfo);
                recentlyAddedRecyclerViewAdapter.added(productInfo);

                Collections.sort(bestRatedProducts, new SortingByRating());
//                ArrayList<ProductInfo> temp1 = new ArrayList<>();
//                for(int i=0; i<bestRatedProducts.size() && i<8; i++){
//                    temp1.add(bestRatedProducts.get(i));
//                }
                bestRatedRecyclerViewAdapter = new UserHomePage1RecyclerViewAdapter(UserHomePage1Activity.this, bestRatedProducts);
                bestRatedRecyclerView.setAdapter(bestRatedRecyclerViewAdapter);

                Collections.sort(mostSoldProducts, new SortingBySold());
//                ArrayList<ProductInfo> temp2 = new ArrayList<>();
//                for(int i=0; i<bestRatedProducts.size() && i<8; i++){
//                    temp2.add(bestRatedProducts.get(i));
//                }
                mostSoldRecyclerViewAdapter = new UserHomePage1RecyclerViewAdapter(UserHomePage1Activity.this, mostSoldProducts);
                mostSoldRecyclerView.setAdapter(mostSoldRecyclerViewAdapter);

                Collections.sort(recentlyAddedProducts, new SortingByDate());
//                ArrayList<ProductInfo> temp3 = new ArrayList<>();
//                for(int i=0; i<bestRatedProducts.size() && i<8; i++){
//                    temp3.add(bestRatedProducts.get(i));
//                }
                recentlyAddedRecyclerViewAdapter = new UserHomePage1RecyclerViewAdapter(UserHomePage1Activity.this, recentlyAddedProducts);
                recentlyAddedRecyclerView.setAdapter(recentlyAddedRecyclerViewAdapter);
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







        final Spinner searchOption = findViewById(R.id.user_home_page1_spinner_search_choice);
        final SearchView searchView = findViewById(R.id.user_home_page1_sv_search);
        searchRecyclerView = findViewById(R.id.user_home_page1_srv_search);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerViewAdapter = new CategoryRecyclerViewAdapter(this, productInfos);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                contentLayout.setVisibility(View.GONE);
                searchLayout.setVisibility(View.VISIBLE);

                searchResults.clear();
                queryText = query;
                String searchFilter = searchOption.getSelectedItem().toString();

                if(searchFilter.equals("Artisan")){
                    for(ProductInfo product : productInfos){
                        try{
                            if(product.getArtisanName().toLowerCase().contains(query.trim().toLowerCase()))
                                searchResults.add(product);
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }
                    searchRecyclerViewAdapter = new CategoryRecyclerViewAdapter(getBaseContext(), searchResults);
                    searchRecyclerView.setAdapter(searchRecyclerViewAdapter);
                }
                else{
                    for(ProductInfo product: productInfos){
                        if(product.getProductName().toLowerCase().contains(query.trim().toLowerCase()))
                            searchResults.add(product);
                    }
                    searchRecyclerViewAdapter = new CategoryRecyclerViewAdapter(getBaseContext(), searchResults);
                    searchRecyclerView.setAdapter(searchRecyclerViewAdapter);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")){
                    searchRecyclerViewAdapter = new CategoryRecyclerViewAdapter(getBaseContext(),productInfos);
                    searchRecyclerView.setAdapter(searchRecyclerViewAdapter);

                    contentLayout.setVisibility(View.VISIBLE);
                    searchLayout.setVisibility(View.GONE);
                }
                else{
                    contentLayout.setVisibility(View.GONE);
                    searchLayout.setVisibility(View.VISIBLE);
                }
                searchResults.clear();
                String searchFilter = searchOption.getSelectedItem().toString();

                if(searchFilter.equals("Artisan")){
                    for(ProductInfo product : productInfos){
                        try{
                            if(product.getArtisanName().toLowerCase().contains(newText))
                                searchResults.add(product);
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }
                    searchRecyclerViewAdapter = new CategoryRecyclerViewAdapter(getBaseContext(), searchResults);
                    searchRecyclerView.setAdapter(searchRecyclerViewAdapter);
                }
                else{
                    for(ProductInfo product: productInfos){
                        if(product.getProductName().toLowerCase().contains(newText))
                            searchResults.add(product);
                    }
                    searchRecyclerViewAdapter = new CategoryRecyclerViewAdapter(getBaseContext(), searchResults);
                    searchRecyclerView.setAdapter(searchRecyclerViewAdapter);
                }
                return false;
            }
        });




        final TextView more1 = findViewById(R.id.user_home_page1_tv_most_sold_more);
        final TextView more2 = findViewById(R.id.user_home_page1_tv_best_rated_more);
        final TextView more3 = findViewById(R.id.user_home_page1_tv_recently_added_more);

        more1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(UserHomePage1Activity.this, DisplayProductsActivity.class);
                newIntent.putExtra("choice","sold");
                startActivity(newIntent);
            }
        });

        more2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(UserHomePage1Activity.this, DisplayProductsActivity.class);
                newIntent.putExtra("choice","rated");
                startActivity(newIntent);
            }
        });

        more3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(UserHomePage1Activity.this, DisplayProductsActivity.class);
                newIntent.putExtra("choice","added");
                startActivity(newIntent);
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