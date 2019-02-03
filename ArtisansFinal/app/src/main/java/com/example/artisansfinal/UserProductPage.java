package com.example.artisansfinal;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.artisansfinal.R;

public class UserProductPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_product_page_activity);
    }

    public void Go_back(View view) {
        Toast.makeText(this, "Going back...", Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, UserHomePageActivity.class);
        startActivity(i);
    }
}
