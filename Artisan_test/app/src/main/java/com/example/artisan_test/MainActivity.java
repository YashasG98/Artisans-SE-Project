package com.example.artisan_test;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button buttonAdd;
    Button buttonReg;
    String[] listItems;
    boolean[] checked_items;
    String[] skillset;
    ArrayList<Integer> mitems = new ArrayList<>();

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Artisans");


        buttonAdd = (Button) findViewById(R.id.Registration_id);
        buttonReg = (Button) findViewById(R.id.skilset_id);
        final EditText name_edit = (EditText) findViewById(R.id.edit_name_id);
        final EditText contact_edit = (EditText) findViewById(R.id.edit_contact_no_id);
        final EditText postal_edit = (EditText) findViewById(R.id.edit_address_id);
        final EditText username_edit = (EditText) findViewById(R.id.edit_username_id);
        final EditText password_edit = (EditText) findViewById(R.id.edit_password_id);

        final TextView final_list = (TextView) findViewById(R.id.Registered_skills_id);

        listItems = getResources().getStringArray(R.array.skills);
        checked_items = new boolean[listItems.length];

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                mBuilder.setTitle("Skillset");
                mBuilder.setMultiChoiceItems(listItems, checked_items, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position, boolean isChecked) {

                        if (isChecked) {
                            mitems.add(position);
                        } else {
                            mitems.remove((Integer.valueOf(position)));

                        }

                    }
                });

                mBuilder.setCancelable(false);
                mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        String item = "";
                        skillset = new String[mitems.size()];

                        for (int i = 0; i < mitems.size(); i++) {
                            item = item + listItems[mitems.get(i)];
                            skillset[i] = listItems[mitems.get(i)];

                            if (i != mitems.size() - 1) {
                                item = item + ", ";
                            }

                        }
                        final_list.setText("You have selected " + item);
                    }
                });

                mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                mBuilder.setNeutralButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i < checked_items.length; i++) {
                            checked_items[i] = false;
                            mitems.clear();
                            final_list.setText("");
                        }
                        skillset = new String[1];
                        skillset[0] = "";
                    }
                });

                AlertDialog mDialog = mBuilder.create();
                mDialog.show();
            }
        });


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = name_edit.getText().toString();
                String address = postal_edit.getText().toString();
                String username = username_edit.getText().toString();
                String password = password_edit.getText().toString();
                String contact_no = (contact_edit.getText().toString());
                String id = databaseReference.push().getKey();
                //String id = "asdf";

                Artisan_info artisan = new Artisan_info(id, name, contact_no, address, username, password);

                databaseReference.child(id).setValue(artisan);

                Toast.makeText(getApplicationContext(), "registered", Toast.LENGTH_LONG).show();


            }
        });
    }
}
