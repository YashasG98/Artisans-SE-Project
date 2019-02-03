package com.example.artisansfinal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {
    private EditText userName,userPassword,userEmail,userPnumber,userPcode,userCpassword;
    private Button regButton;
    private TextView userLogin;
    private FirebaseAuth firebaseAuth;
    String name,pcode,pnumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        setupUIViews();
        FirebaseApp.initializeApp(this);
        firebaseAuth=FirebaseAuth.getInstance();
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validate())
                {
                    String user_email=userEmail.getText().toString().trim();
                    String user_password=userPassword.getText().toString().trim();
                    firebaseAuth.createUserWithEmailAndPassword(user_email,user_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                           if(task.isSuccessful())
                           {
                               sendUserData();
                               Toast.makeText(RegistrationActivity.this,"Registration Successful",Toast.LENGTH_SHORT).show();
                               startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                           }
                           else
                           {
                               Toast.makeText(RegistrationActivity.this,"User Already Exists",Toast.LENGTH_SHORT).show();

                           }

                        }
                    });
                }
            }
        });

       userLogin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
           }
       });
    }
    private void setupUIViews()
    {
        userName=(EditText)findViewById(R.id.registration_page_et_user_name);
        userPassword=(EditText)findViewById(R.id.registration_page_et_user_password);
        userEmail=(EditText)findViewById(R.id.registration_page_et_user_email);
        regButton=(Button)findViewById(R.id.registration_page_button_user_registration);
        userLogin=(TextView)findViewById(R.id.registration_page_tv_user_login);

        userPcode=(EditText) findViewById(R.id.registration_page_et_user_pcode);
        userPnumber=(EditText) findViewById(R.id.registration_page_et_user_number);
        userCpassword=(EditText) findViewById(R.id.registration_page_et_user_cpassword);
    }
    private Boolean validate()
    {
        Boolean result=false;
         name=userName.getText().toString();
        String email=userEmail.getText().toString();
        String password=userPassword.getText().toString();
        String cpassword=userCpassword.getText().toString();
         pcode=userPcode.getText().toString();
         pnumber=userPnumber.getText().toString();


        if(name.isEmpty())
        {
            Toast.makeText(this,"Name is empty",Toast.LENGTH_SHORT).show();
        }
        else if(email.isEmpty())
            Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show();
        else if(pnumber.isEmpty())
            Toast.makeText(this, "Phone Number is empty", Toast.LENGTH_SHORT).show();
        else if(pcode.isEmpty())
            Toast.makeText(this, "Pincode is empty", Toast.LENGTH_SHORT).show();

        else if(password.isEmpty())
            Toast.makeText(this, "Password is empty", Toast.LENGTH_SHORT).show();
        else if(password.length()<8)
            Toast.makeText(this, "Password should be of min 8 characters", Toast.LENGTH_SHORT).show();

        else if(cpassword.isEmpty())
            Toast.makeText(this, "Confirm Password is empty", Toast.LENGTH_SHORT).show();

        else if(!cpassword.equals(password))
        {
            Toast.makeText(this,"Passwords do not match",Toast.LENGTH_SHORT).show();
        }
        else
        {
            result=true;
        }
        return result;
    }

    private  void sendUserData()
    {
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference myRef=firebaseDatabase.getReference();
        UserInfo userinfo=new UserInfo(name,pcode,pnumber);
        myRef.child("User").child(userinfo.userPnumber).setValue(userinfo);
    }
}
