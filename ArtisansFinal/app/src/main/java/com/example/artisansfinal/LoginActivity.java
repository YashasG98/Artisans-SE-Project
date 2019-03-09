package com.example.artisansfinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText Name;
    private EditText Password;
    private Button Login;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private Spinner spinner;
    private TextView forgotpassword;
    private Intent intent;
    private String userType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        intent = getIntent();

        userType = intent.getStringExtra("userType");

        Name = (EditText) findViewById(R.id.login_page_et_user_name);
        Password = (EditText) findViewById(R.id.login_page_et_user_password);
        Login = (Button) findViewById(R.id.login_page_button_user_login);
        //userRegistration=(Button)findViewById(R.id.login_page_button_user_registration);

        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.intents, R.layout.support_simple_spinner_dropdown_item);

        forgotpassword = (TextView) findViewById(R.id.login_page_tv_user_forgotpassword);


        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            finish();
            if(userType.equals("u")) {
                Log.d("HERE1",user.getEmail());
                Intent intent1 = new Intent(new Intent(LoginActivity.this, UserHomePageActivity.class));
                Log.d("HERE1",Name.getText().toString().trim());
                startActivity(intent1);
            }
            else {
                Intent intent1 = new Intent(new Intent(LoginActivity.this, ArtisanHomePageActivity.class));
                intent1.putExtra("userType", userType);
                startActivity(intent1);
            }
        }
//        userRegistration.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
//            }
//        });
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, PasswordActivity.class));
            }
        });

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Name.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Email is empty", Toast.LENGTH_SHORT).show();
                } else if (Password.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Password is empty", Toast.LENGTH_SHORT).show();
                } else {
                    validate(Name.getText().toString(), Password.getText().toString());
                }
            }
        });
    }

    private void validate(String userName, String userPassword) {
        progressDialog.setMessage("Please wait while we Log U in");
        progressDialog.show();
        firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
//                    if(userType.equals("u")) {

                        Intent intent1 = new Intent(new Intent(LoginActivity.this, UserHomePageActivity.class));
                        intent1.putExtra("userType", userType);
                        startActivity(intent1);
//                    }
//                    else {
//                        Intent intent1 = new Intent(new Intent(LoginActivity.this, ArtisanHomePageActivity.class));
//                        intent1.putExtra("userType", userType);
//                        startActivity(intent1);
//                    }

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {

        } else {

            if(position == 1) {
                Intent intent1 = new Intent(new Intent(LoginActivity.this, RegistrationActivity.class));
                intent1.putExtra("userType", userType);
                startActivity(intent1);
            }
            else
            {
                Intent intent1 = new Intent(new Intent(LoginActivity.this, ArtisanRegistrationActivity.class));
                intent1.putExtra("userType", userType);
                startActivity(intent1);
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
