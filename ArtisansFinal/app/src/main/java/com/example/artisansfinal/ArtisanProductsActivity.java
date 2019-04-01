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
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionButton;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionHelper;
import com.wangjie.rapidfloatingactionbutton.RapidFloatingActionLayout;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RFACLabelItem;
import com.wangjie.rapidfloatingactionbutton.contentimpl.labellist.RapidFloatingActionContentLabelList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private RapidFloatingActionContentLabelList sortFAB;
    private RapidFloatingActionButton sortFAButton;
    private RapidFloatingActionHelper rfabHelper;
    private RapidFloatingActionLayout rfaLayout;
    private String queryText;
    private static boolean runInOnePage =  false;


    class sorting implements Comparator<ProductInfo>
    {

        @Override
        public int compare(ProductInfo o1, ProductInfo o2) {
            if(o1.getProductPrice() == o2.getProductPrice())
                return 0;
            else if(Integer.parseInt(o1.getProductPrice()) > Integer.parseInt(o2.getProductPrice()))
                return 1;
            else
                return -1;
        }
    }

    class sortingByRating implements Comparator<ProductInfo>
    {

        @Override
        public int compare(ProductInfo o1, ProductInfo o2) {

            Log.d("rating", o1.getNumberOfPeopleWhoHaveRated() + " !" + o2.getTotalRating() +" =" +o1.toString());
            if(Float.parseFloat(o1.getTotalRating()) > Float.parseFloat(o2.getTotalRating()))
                return 1;
            else if(Float.parseFloat(o1.getTotalRating()) < Float.parseFloat(o2.getTotalRating()))
                return -1;
            else
                return 0;
        }
    }

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

        //FAB for sorting

        sortFAB = new RapidFloatingActionContentLabelList(getApplicationContext());

        sortFAButton = findViewById(R.id.artisan_rfab);
        rfaLayout = findViewById(R.id.artisan_FAB_layout);

        final ArrayList<RFACLabelItem> sortOptions = new ArrayList<>();
        sortOptions.add(new RFACLabelItem<Integer>().
                setLabel("Price: Low to High"));

        sortOptions.add(new RFACLabelItem<Integer>().
                setLabel("Price: High to Low"));

        sortOptions.add(new RFACLabelItem<Integer>().
                setLabel("Rating: Low to High"));

        sortOptions.add(new RFACLabelItem<Integer>().
                setLabel("Rating: High to Low"));


        sortFAB.setItems(sortOptions);

        rfabHelper = new RapidFloatingActionHelper(
                this,
                rfaLayout,
                sortFAButton,
                sortFAB
        ).build();

        Intent i = getIntent();
        artisanPhoneNumber = i.getStringExtra("phoneNumber");

        final RecyclerView recyclerView = findViewById(R.id.artisan_products_content_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        artisanProductsRecyclerViewAdapter = new ArtisanProductsRecyclerViewAdapter(this, productInfos);
        recyclerView.setAdapter(artisanProductsRecyclerViewAdapter);


        ArrayList<View> views = new ArrayList<>();
//        views.add(searchOption);
        views.add(searchView);
        views.add(sortFAButton);

        final HashMap<View, String> title = new HashMap<>();
//        title.put(searchOption,"Search for products\n with these options");
        title.put(searchView,"Search for your product here");
        title.put(sortFAButton,"Filtering choices");

        final Tutorial tutorial = new Tutorial(this,views);
        tutorial.checkIfFirstRun();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchResults.clear();
                queryText = query;
                for(ProductInfo product : productInfos){
                    try {
                        if (product.getProductName().toLowerCase().contains(query)) {
                            searchResults.add(product);
                        }
                    }
                    catch (Exception e)
                    {

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
                    try {
                        if (products.getProductName().toLowerCase().startsWith(newText)) {
                            searchResults.add(products);
                        }
                    }
                    catch (Exception e)
                    {

                    }
                }
                artisanProductsRecyclerViewAdapter = new ArtisanProductsRecyclerViewAdapter(getBaseContext(),searchResults);
                recyclerView.setAdapter(artisanProductsRecyclerViewAdapter);
                return false;
            }
        });

        sortFAB.setOnRapidFloatingActionContentLabelListListener(new RapidFloatingActionContentLabelList.OnRapidFloatingActionContentLabelListListener() {
            @Override
            public void onRFACItemLabelClick(int position, RFACLabelItem item) {

                final String sorting_order = item.getLabel();
                if (queryText == null) {
                    sort(productInfos, sorting_order);
                    artisanProductsRecyclerViewAdapter = new ArtisanProductsRecyclerViewAdapter(ArtisanProductsActivity.this, productInfos);
                    recyclerView.setAdapter(artisanProductsRecyclerViewAdapter);
                } else {
                    sort(searchResults, sorting_order);
                    artisanProductsRecyclerViewAdapter = new ArtisanProductsRecyclerViewAdapter(ArtisanProductsActivity.this, searchResults);
                    recyclerView.setAdapter(artisanProductsRecyclerViewAdapter);
                }

                rfabHelper.toggleContent();

            }

            @Override
            public void onRFACItemIconClick(int position, RFACLabelItem item) {

                rfabHelper.toggleContent();


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
                                if(contentLayout.getVisibility() == View.VISIBLE && !runInOnePage){

                                    tutorial.requestFocusForViews(title);
                                    tutorial.finishedTutorial();
                                    runInOnePage = true;

                                }
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

    public void sort(ArrayList<ProductInfo> product_list, String sorting_order) {


        if(sorting_order.equals("Price: Low to High") || sorting_order.equals("Price: High to Low")) {
            Collections.sort(product_list, new sorting());

            if(sorting_order.equals("Price: High to Low"))
                Collections.reverse(product_list);

        }



        if(sorting_order.equals("Rating: Low to High") || sorting_order.equals("Rating: High to Low")) {
            Collections.sort(product_list, new sortingByRating());

            if(sorting_order.equals("Rating: High to Low"))
                Collections.reverse(product_list);
        }


    }
}
