package com.example.artisansfinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;

public class SelectedCategoryActivity extends AppCompatActivity {

    private ArrayList<ProductInfo> productInfos = new ArrayList<ProductInfo>();
    private DatabaseReference databaseReference;
    private CategoryRecyclerViewAdapter categoryRecyclerViewAdapter;
    private static final String TAG = "SelectedCategoryAct";
    private ArrayList<ProductInfo> searchResults = new ArrayList<>();
    private ProgressDialog progressDialog;

    private RelativeLayout contentLayout;
    private RelativeLayout loadingLayout;
    private RelativeLayout notFoundLayout;
    private RelativeLayout noMatchLayout;
    private RelativeLayout recyclerViewLayout;
    private ImageView loading;

    //Tutorials (done by shashwatha)
    private static boolean runInOnePage = false;

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
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Loading the products");
//        progressDialog.show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_category);
        loading = findViewById(R.id.selected_category_loading_iv);
        Glide.with(getApplicationContext())
                .asGif()
                .load(R.mipmap.loading2)
                .into(loading);
        contentLayout = findViewById(R.id.selected_category_content_rl);
        contentLayout.setVisibility(RelativeLayout.GONE);
        loadingLayout = findViewById(R.id.selected_category_loading_rl);
        loadingLayout.setVisibility(RelativeLayout.VISIBLE);
        notFoundLayout = findViewById(R.id.selected_category_not_found_outer_rl);
        notFoundLayout.setVisibility(RelativeLayout.GONE);
        noMatchLayout = findViewById(R.id.selected_category_not_found_inner_rl);
        recyclerViewLayout = findViewById(R.id.selected_category_rl);
//        setContentView(R.layout.loading_layout);
//        ImageView imageView = findViewById(R.id.loading_layout_iv);
//        Glide.with(getApplicationContext())
//                .asGif()
//                .load(R.mipmap.loading2)
//                .into(imageView);
        Log.d(TAG,"entered this activity");

        Intent intent = getIntent();
        final String category = intent.getStringExtra("category");
        //FirebaseDatabase fb = databaseReference.child(category).getDatabase() ;
        //Log.d(TAG, fb.toString());
        //String str = databaseReference.child(category).getKey();
        //Log.d(TAG, str);

        ImageButton searchButton = findViewById(R.id.selected_category_iButton_search);
        final Spinner sortChoice = findViewById(R.id.selected_category_spinner_sort_choice);
        final EditText searchQuery = findViewById(R.id.selected_category_et_search_query);
        final Spinner searchOption = findViewById(R.id.selected_category_spinner_search_choice);
        final RecyclerView recyclerView  = findViewById(R.id.selected_category_rv);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(this, productInfos);
        recyclerView.setAdapter(categoryRecyclerViewAdapter);

        ArrayList<View> views = new ArrayList<>();
        views.add(searchOption);
        views.add(searchQuery);
        views.add(sortChoice);

        final HashMap<View, String> title = new HashMap<>();
        title.put(searchOption,"Search for products\n with these options");
        title.put(searchQuery,"Search for your product here");
        title.put(sortChoice,"Filtering choices");

        final Tutorial tutorial = new Tutorial(this,views);
        tutorial.checkIfFirstRun();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                noMatchLayout.setVisibility(View.GONE);
                recyclerViewLayout.setVisibility(View.VISIBLE);

                String searchItem = searchQuery.getText().toString().toLowerCase();
                String searchFilter = searchOption.getSelectedItem().toString();

                searchResults.clear();

                if(searchFilter.equals("Artisan")){
                    for(ProductInfo product : productInfos){
                        try{
                            if(product.getArtisanName().toLowerCase().contains(searchItem))
                                searchResults.add(product);
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }
                    categoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(v.getContext(), searchResults);
                    recyclerView.setAdapter(categoryRecyclerViewAdapter);
                }
                else{
                    for(ProductInfo product: productInfos){
                        if(product.getProductName().toLowerCase().contains(searchItem))
                            searchResults.add(product);
                    }
                    categoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(v.getContext(), searchResults);
                    recyclerView.setAdapter(categoryRecyclerViewAdapter);
                }
                if(searchResults.isEmpty()){
                    noMatchLayout.setVisibility(View.VISIBLE);
                    recyclerViewLayout.setVisibility(View.GONE);
                }
            }
        });

        sortChoice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String sorting_order = sortChoice.getSelectedItem().toString();
                if(searchQuery.getText().toString().length()==0) {
                    sort(productInfos, sorting_order);
                    categoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(SelectedCategoryActivity.this, productInfos);
                    recyclerView.setAdapter(categoryRecyclerViewAdapter);
                } else{
                    sort(searchResults, sorting_order);
                    categoryRecyclerViewAdapter = new CategoryRecyclerViewAdapter(SelectedCategoryActivity.this, searchResults);
                    recyclerView.setAdapter(categoryRecyclerViewAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("Categories/"+category);
        Log.d("EXIST1", databaseReference.getDatabase().toString());
        Log.d("EXIST2", databaseReference.getKey());
        Log.d("EXIST3", databaseReference.getParent().toString());
        Log.d("EXIST4", databaseReference.getRoot().toString());
        Log.d("EXIST5", databaseReference.toString());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null){
                    databaseReference.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                if(progressDialog.isShowing())
//                    progressDialog.dismiss();
                            if(contentLayout.getVisibility()==View.GONE) {
                                loadingLayout.setVisibility(View.GONE);
                                contentLayout.setVisibility(View.VISIBLE);
                                recyclerViewLayout.setVisibility(View.VISIBLE);
                                noMatchLayout.setVisibility(View.GONE);

                                if(contentLayout.getVisibility() == View.VISIBLE && !runInOnePage){

                                    tutorial.requestFocusForViews(title);
                                    tutorial.finishedTutorial();
                                    runInOnePage = true;

                                }

                            }
                            ProductInfo productInfo;
                            Log.d("FOUND",dataSnapshot.toString());
                            Log.d("FOUND",dataSnapshot.getKey());
                            Log.d("FOUND", dataSnapshot.getValue().toString());
                            HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.getValue();
                            Log.d("HASHMAP", map.toString());
                            productInfo = new ProductInfo(map.get("productID"), map.get("productName"), map.get("productDescription"), map.get("productCategory"), map.get("productPrice"), map.get("artisanName"), map.get("artisanContactNumber"));
                            productInfo.setTotalRating(map.get("totalRating"));
                            productInfo.setNumberOfPeopleWhoHaveRated(map.get("numberOfPeopleWhoHaveRated"));
                            Log.d("MAP", map.get("productDescription"));
                            categoryRecyclerViewAdapter.added(productInfo);
                            //categoryRecyclerViewAdapter.addedImage(storageReference.child("ProductImage/"+(map.get("productID"))));


                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Log.d("onchildchanged", dataSnapshot.toString());
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                            Log.d("onchildremoved", dataSnapshot.toString());
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Log.d("onchildmoved", dataSnapshot.toString());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            progressDialog.dismiss();
                            Log.d("oncancelled", databaseError.toString());
                        }
                    });
                }
                else {
                    loadingLayout.setVisibility(RelativeLayout.GONE);
                    notFoundLayout.setVisibility(RelativeLayout.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /*databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                if(progressDialog.isShowing())
//                    progressDialog.dismiss();
                if(contentLayout.getVisibility()==View.GONE) {
                    loadingLayout.setVisibility(View.GONE);
                    contentLayout.setVisibility(View.VISIBLE);
                }
                ProductInfo productInfo;
                Log.d("FOUND",dataSnapshot.toString());
                Log.d("FOUND",dataSnapshot.getKey());
                Log.d("FOUND", dataSnapshot.getValue().toString());
                HashMap<String, String> map = (HashMap<String, String>) dataSnapshot.getValue();
                Log.d("HASHMAP", map.toString());
                productInfo = new ProductInfo(map.get("productID"), map.get("productName"), map.get("productDescription"), map.get("productCategory"), map.get("productPrice"), map.get("artisanName"), map.get("artisanContactNumber"));
                Log.d("MAP", map.get("productDescription"));
                categoryRecyclerViewAdapter.added(productInfo);
                //categoryRecyclerViewAdapter.addedImage(storageReference.child("ProductImage/"+(map.get("productID"))));


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("onchildchanged", dataSnapshot.toString());
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onchildremoved", dataSnapshot.toString());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("onchildmoved", dataSnapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                Log.d("oncancelled", databaseError.toString());
            }
        });*/
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
