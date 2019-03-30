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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class RegistrationActivity extends AppCompatActivity {
    private EditText userName,userPassword,userEmail,userPnumber,userPcode,userCpassword,userWallet;
    private Button regButton;

    private FirebaseAuth firebaseAuth;
    String name,pcode,pnumber,email,wallet;
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
                                int i=0;
                                while(i!=10000){i++;}
                                //Toast.makeText(RegistrationActivity.this,"Registration Successful",Toast.LENGTH_SHORT).show();
                                FirebaseInstanceId.getInstance().getInstanceId()
                                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                if(task.isSuccessful()){
                                                    final String token=task.getResult().getToken();
                                                    final FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                                                    final DatabaseReference database= FirebaseDatabase.getInstance().getReference("User/");
                                                    database.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                            for(DataSnapshot data: dataSnapshot.getChildren()){
                                                                if(data.child("userEmail").getValue().toString().equals(user.getEmail())){
                                                                    DatabaseReference db=FirebaseDatabase.getInstance().getReference("User/");
                                                                    db.child(data.child("userPnumber").getValue().toString()).child("FCMToken").setValue(token);
                                                                }
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                                        }
                                                    });
                                                }
                                                else {
                                                    Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
//                               Intent intent = new Intent(RegistrationActivity.this, UserHomePageActivity.class);
//                               startActivity(intent);
                                sendEmailVerification();
                            }
                            else
                            {
                                Toast.makeText(RegistrationActivity.this,"Registration Failed",Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                }
            }
        });


    }
    private void setupUIViews()
    {
        userName=(EditText)findViewById(R.id.registration_page_et_user_name);
        userPassword=(EditText)findViewById(R.id.registration_page_et_user_password);
        userEmail=(EditText)findViewById(R.id.registration_page_et_user_email);
        regButton=(Button)findViewById(R.id.registration_page_button_user_registration);
       // userWallet=(EditText)findViewById(R.id.registration_page_et_user_wallet);
        userPcode=(EditText) findViewById(R.id.registration_page_et_user_pcode);
        userPnumber=(EditText) findViewById(R.id.registration_page_et_user_number);
        userCpassword=(EditText) findViewById(R.id.registration_page_et_user_cpassword);
    }
    private Boolean validate()
    {
        Boolean result=true;
        name=userName.getText().toString();
        email=userEmail.getText().toString();
        wallet="2500";
        String password=userPassword.getText().toString();
        String cpassword=userCpassword.getText().toString();
        pcode=userPcode.getText().toString();
        pnumber=userPnumber.getText().toString();


        if(name.isEmpty())
        {
            //Toast.makeText(this,"Name is empty",Toast.LENGTH_SHORT).show();
            userName.setError("Name is empty");
            userName.requestFocus();
            result=false;
        }
        if(email.isEmpty())
        {
            //Toast.makeText(this,"Name is empty",Toast.LENGTH_SHORT).show();
            userEmail.setError("Email is empty");
            userEmail.requestFocus();
            result=false;
        }
//        if(wallet.isEmpty())
//        {
//            //Toast.makeText(this,"Name is empty",Toast.LENGTH_SHORT).show();
//            userWallet.setError("Wallet is empty");
//            userWallet.requestFocus();
//            result=false;
//        }

         if(pnumber.isEmpty())
        {
            //Toast.makeText(this,"Name is empty",Toast.LENGTH_SHORT).show();
            userPnumber.setError("Phone Number is empty");
            userPnumber.requestFocus();
            result=false;
        }
        if(pcode.isEmpty())
        {
            //Toast.makeText(this,"Name is empty",Toast.LENGTH_SHORT).show();
            userPcode.setError("Pincode is empty");
            userPcode.requestFocus();
            result=false;
        }

        if(password.isEmpty())
        {
            //Toast.makeText(this,"Name is empty",Toast.LENGTH_SHORT).show();
            userPassword.setError("Password is empty");
            userPassword.requestFocus();
            result=false;
        }
        if(password.length()<8)
        {Toast.makeText(this, "Password should be of min 8 characters", Toast.LENGTH_SHORT).show();
            result=false;}

        if(cpassword.isEmpty())
        {
            //Toast.makeText(this,"Name is empty",Toast.LENGTH_SHORT).show();
            userCpassword.setError("Confirm Password empty");
            userCpassword.requestFocus();
            result=false;
        }
        if(!cpassword.equals(password))
        {
            Toast.makeText(this,"Passwords do not match",Toast.LENGTH_SHORT).show();
            result=false;
        }

        return result;
    }

    private void sendEmailVerification(){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        sendUserData();
                        Toast.makeText(RegistrationActivity.this, "Successfully Registered, Verification mail sent!", Toast.LENGTH_SHORT).show();
                        firebaseAuth.signOut();
                        finish();
                        startActivity(new Intent(RegistrationActivity.this, CommonLoginActivityTabbed.class));
                    }else{
                        Toast.makeText(RegistrationActivity.this, "Verification mail has'nt been sent!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private  void sendUserData()
    {
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference myRef=firebaseDatabase.getReference();
        UserInfo userinfo=new UserInfo(name,pcode,pnumber,wallet,email,user.getUid());
        myRef.child("User").child(userinfo.userPnumber).setValue(userinfo);


    }
}
