package com.example.artisansfinal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CommonLoginActivity extends AppCompatActivity {

    private Button userLogin;
    private Button artisanLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_login);

        userLogin = findViewById(R.id.common_login_button_user_login);
        artisanLogin = findViewById(R.id.common_login_button_artisan_login);

        userLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CommonLoginActivity.this, LoginActivity.class);
                intent.putExtra("userType", "u");
                startActivity(intent);
            }
        });

        artisanLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(CommonLoginActivity.this, ArtisanLoginActivity.class);
                intent.putExtra("userType", "a");
                startActivity(intent);
            }
        });
    }
}
