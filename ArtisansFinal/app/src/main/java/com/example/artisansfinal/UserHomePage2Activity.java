package com.example.artisansfinal;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

public class UserHomePage2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

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
    private UserHomePage2RecyclerViewAdapter bestRatedRecyclerViewAdapter;
    private UserHomePage2RecyclerViewAdapter mostSoldRecyclerViewAdapter;
    private UserHomePage2RecyclerViewAdapter recentlyAddedRecyclerViewAdapter;

    private String queryText = null;
    private RecyclerView searchRecyclerView;
    private CategoryRecyclerViewAdapter searchRecyclerViewAdapter;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private FirebaseAuth firebaseAuth;
    String username;
    String email;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);

        drawer.addDrawerListener(toggle);

        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        navigationView.setNavigationItemSelectedListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null)
        {
            uid = user.getUid();

            FirebaseDatabase.getInstance().getReference().child("User").addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    HashMap<String, String> map = (HashMap<String, String>)dataSnapshot.getValue();
                    if(map.get("UID").equals(uid)){
                        username = map.get("userName");
                        email = map.get("userEmail");

                        TextView displayName = findViewById(R.id.nav_header_user_home_page2_user_name);
                        TextView displayEmail = findViewById(R.id.nav_header_user_home_page2_user_email);

                        displayEmail.setText(email);
                        displayName.setText(username);
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

        final LinearLayout contentLayout = findViewById(R.id.user_home_page2_ll_content);
        final LinearLayout searchLayout = findViewById(R.id.user_home_page2_ll_search);
        searchLayout.setVisibility(View.GONE);

        bestRatedRecyclerView = findViewById(R.id.user_home_page2_srv_best_rated);
        bestRatedRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        bestRatedRecyclerViewAdapter = new UserHomePage2RecyclerViewAdapter(this, bestRatedProducts);
//        SnapHelper snapHelper1 = new PagerSnapHelper();
//        snapHelper1.attachToRecyclerView(bestRatedRecyclerView);
        bestRatedRecyclerView.setAdapter(bestRatedRecyclerViewAdapter);

        mostSoldRecyclerView = findViewById(R.id.user_home_page2_srv_most_sold);
        mostSoldRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mostSoldRecyclerViewAdapter = new UserHomePage2RecyclerViewAdapter(this, mostSoldProducts);
//        SnapHelper snapHelper2 = new PagerSnapHelper();
//        snapHelper2.attachToRecyclerView(mostSoldRecyclerView);
        mostSoldRecyclerView.setAdapter(mostSoldRecyclerViewAdapter);

        recentlyAddedRecyclerView = findViewById(R.id.user_home_page2_srv_recently_added);
        recentlyAddedRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recentlyAddedRecyclerViewAdapter = new UserHomePage2RecyclerViewAdapter(this, recentlyAddedProducts);
        recentlyAddedRecyclerView.setAdapter(recentlyAddedRecyclerViewAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("Products");
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

                Collections.sort(bestRatedProducts, new UserHomePage2Activity.SortingByRating());
//                ArrayList<ProductInfo> temp1 = new ArrayList<>();
//                for(int i=0; i<bestRatedProducts.size() && i<8; i++){
//                    temp1.add(bestRatedProducts.get(i));
//                }
                bestRatedRecyclerViewAdapter = new UserHomePage2RecyclerViewAdapter(UserHomePage2Activity.this, bestRatedProducts);
                bestRatedRecyclerView.setAdapter(bestRatedRecyclerViewAdapter);

                Collections.sort(mostSoldProducts, new UserHomePage2Activity.SortingBySold());
//                ArrayList<ProductInfo> temp2 = new ArrayList<>();
//                for(int i=0; i<bestRatedProducts.size() && i<8; i++){
//                    temp2.add(bestRatedProducts.get(i));
//                }
                mostSoldRecyclerViewAdapter = new UserHomePage2RecyclerViewAdapter(UserHomePage2Activity.this, mostSoldProducts);
                mostSoldRecyclerView.setAdapter(mostSoldRecyclerViewAdapter);

                Collections.sort(recentlyAddedProducts, new UserHomePage2Activity.SortingByDate());
//                ArrayList<ProductInfo> temp3 = new ArrayList<>();
//                for(int i=0; i<bestRatedProducts.size() && i<8; i++){
//                    temp3.add(bestRatedProducts.get(i));
//                }
                recentlyAddedRecyclerViewAdapter = new UserHomePage2RecyclerViewAdapter(UserHomePage2Activity.this, recentlyAddedProducts);
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

        final SearchView searchView = findViewById(R.id.user_home_page2_sv_search);
        searchRecyclerView = findViewById(R.id.user_home_page2_srv_search);
        searchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchRecyclerViewAdapter = new CategoryRecyclerViewAdapter(this, productInfos);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                contentLayout.setVisibility(View.GONE);
                searchLayout.setVisibility(View.VISIBLE);

                searchResults.clear();
                queryText = query;

                for(ProductInfo product: productInfos){
                    if(product.getProductName().toLowerCase().contains(query.trim().toLowerCase())
                            || product.getArtisanName().toLowerCase().contains(query.trim().toLowerCase()))
                        searchResults.add(product);
                }
                searchRecyclerViewAdapter = new CategoryRecyclerViewAdapter(getBaseContext(), searchResults);
                searchRecyclerView.setAdapter(searchRecyclerViewAdapter);

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

                for(ProductInfo product: productInfos){
                    if(product.getProductName().toLowerCase().contains(newText.trim().toLowerCase())
                            || product.getArtisanName().toLowerCase().contains(newText.trim().toLowerCase()))
                        searchResults.add(product);
                }
                searchRecyclerViewAdapter = new CategoryRecyclerViewAdapter(getBaseContext(), searchResults);
                searchRecyclerView.setAdapter(searchRecyclerViewAdapter);

                return false;
            }
        });

        final TextView more1 = findViewById(R.id.user_home_page2_tv_most_sold_more);
        final TextView more2 = findViewById(R.id.user_home_page2_tv_best_rated_more);
        final TextView more3 = findViewById(R.id.user_home_page2_tv_recently_added_more);

        more1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(UserHomePage2Activity.this, DisplayProductsActivity.class);
                newIntent.putExtra("choice","sold");
                startActivity(newIntent);
            }
        });

        more2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(UserHomePage2Activity.this, DisplayProductsActivity.class);
                newIntent.putExtra("choice","rated");
                startActivity(newIntent);
            }
        });

        more3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newIntent = new Intent(UserHomePage2Activity.this, DisplayProductsActivity.class);
                newIntent.putExtra("choice","added");
                startActivity(newIntent);
            }
        });


        final LinearLayout saree = findViewById(R.id.user_home_page2_ll_saree_category);
        final LinearLayout shawl = findViewById(R.id.user_home_page2_ll_shawl_category);
        final LinearLayout shirt = findViewById(R.id.user_home_page2_ll_shirt_category);
        final LinearLayout bracelet = findViewById(R.id.user_home_page2_ll_bracelet_category);
        final LinearLayout garland = findViewById(R.id.user_home_page2_ll_garland_category);
        final LinearLayout pottery = findViewById(R.id.user_home_page2_ll_pottery_category);
        final LinearLayout glassPainting = findViewById(R.id.user_home_page2_ll_glass_painting_category);
        final LinearLayout toys = findViewById(R.id.user_home_page2_ll_toys_category);

        saree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserHomePage2Activity.this, SelectedCategoryActivity.class);
                i.putExtra("category", "Saree");
                startActivity(i);
            }
        });
        shawl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserHomePage2Activity.this, SelectedCategoryActivity.class);
                i.putExtra("category", "Shawl");
                startActivity(i);
            }
        });
        shirt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserHomePage2Activity.this, SelectedCategoryActivity.class);
                i.putExtra("category", "Shirt");
                startActivity(i);
            }
        });
        bracelet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserHomePage2Activity.this, SelectedCategoryActivity.class);
                i.putExtra("category", "Bracelet");
                startActivity(i);
            }
        });
        garland.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserHomePage2Activity.this, SelectedCategoryActivity.class);
                i.putExtra("category", "Garland");
                startActivity(i);
            }
        });
        pottery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserHomePage2Activity.this, SelectedCategoryActivity.class);
                i.putExtra("category", "Pottery");
                startActivity(i);
            }
        });
        glassPainting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserHomePage2Activity.this, SelectedCategoryActivity.class);
                i.putExtra("category", "Glass Painting");
                startActivity(i);
            }
        });
        toys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserHomePage2Activity.this, SelectedCategoryActivity.class);
                i.putExtra("category", "Toys");
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_home_page2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_profile) {
            Intent i = new Intent(this, UserprofilePageActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_order_history) {
            Intent i = new Intent(this, OrderHistoryTabbedActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_wallet) {
            Intent i = new Intent(this, UserWalletActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_tutorial) {
            Intent i = new Intent(this, UserTutorialActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Logout Confirmation");
            builder.setMessage("Are you sure you want to logout?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(UserHomePage2Activity.this, CommonLoginActivityTabbed.class));
                    Toast.makeText(UserHomePage2Activity.this, "Logged out", Toast.LENGTH_SHORT).show();

                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            Dialog dialog = builder.create();
            builder.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
