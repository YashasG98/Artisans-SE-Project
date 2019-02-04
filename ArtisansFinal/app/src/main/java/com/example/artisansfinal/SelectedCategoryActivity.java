package com.example.artisansfinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class SelectedCategoryActivity extends AppCompatActivity {

    private ArrayList<ProductInfo> productInfos = new ArrayList<ProductInfo>();
    private DatabaseReference databaseReference;
    private CategoryRecyclerViewAdapter categoryRecyclerViewAdapter;
    private static final String TAG = "SelectedCategoryAct";
    private ArrayList<ProductInfo> searchResults = new ArrayList<>();
    private ProgressDialog progressDialog;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading the products");
        progressDialog.show();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_category);
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

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
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
        });
    }


    public void sort(ArrayList<ProductInfo> product_list, String sorting_order) {

        Collections.sort(product_list, new sorting());

        if(sorting_order.equals("Price: High to Low"))
            Collections.reverse(product_list);
    }
}
