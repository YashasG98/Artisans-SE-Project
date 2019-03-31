package com.example.artisansfinal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserprofilePageActivity extends AppCompatActivity {

    DatabaseReference databaseUsers;
    Button buttonUpdateUser;
    private String nametp,pctp,phnotp;
    FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();

    FirebaseUser userX = firebaseAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_page);
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("In a minute!");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        databaseUsers= FirebaseDatabase.getInstance().getReference("User");

        buttonUpdateUser=(Button)findViewById(R.id.editUserInfoButton);
        buttonUpdateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UserprofilePageActivity.this,UserProfileUpdateActivity.class);
                i.putExtra("Name",nametp);
                i.putExtra("PC",pctp);
                i.putExtra("Phone",phnotp);
                startActivity(i);
                finish();
            }
        });

        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                    UserInfo user = userSnapshot.getValue(UserInfo.class);
                    if(user.userEmail.equals(userX.getEmail()))
                    {
                        TextView name=(TextView)findViewById(R.id.userName);
                        TextView phno=(TextView)findViewById(R.id.userPhno);
                        TextView pc=(TextView)findViewById(R.id.userPC);

                        name.setText(user.userName);
                        phno.setText(user.userPnumber);
                        phnotp=user.userPnumber;
                        pc.setText(user.userPcode);

                        nametp=user.userName;
                        pctp=user.userPcode;
                        progress.dismiss();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
