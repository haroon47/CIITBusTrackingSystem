package com.mytracker.minibustracker.bustracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DriverRegistrationActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText e1,e2,e3,e4;
    FirebaseAuth auth;
    ProgressDialog dialog;
    FirebaseUser user;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_registration);
        toolbar = (Toolbar)findViewById(R.id.driverToolbar);
        toolbar.setTitle("Driver Register");
        FirebaseDatabase.getInstance().goOnline();
        setSupportActionBar(toolbar);
        e1 = (EditText)findViewById(R.id.editText);
        e2 = (EditText)findViewById(R.id.editText2);
        e3 = (EditText)findViewById(R.id.editText3);
        e4 = (EditText)findViewById(R.id.editText4);
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
    }

    public void registerDriver(View v) {
        dialog.setTitle("Creating account");
        dialog.setMessage("Please wait");
        dialog.show();

        final String name = e1.getText().toString();
        final String email = e2.getText().toString();
        final String password = e3.getText().toString();


        if (name.equals("") && email.equals("") && password.equals("") ) {
            Toast.makeText(getApplicationContext(), "Please enter correct details", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        } else {
            auth.fetchProvidersForEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<ProviderQueryResult> task) {

                            if (task.isSuccessful()) {
                                boolean check = !task.getResult().getProviders().isEmpty();
                                if (!check) {
                                    doAllStuff();
                                }

                                else {
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Account already exists.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                    });
        }


    }


    public void doAllStuff()
    {
        auth.createUserWithEmailAndPassword(e2.getText().toString(),e3.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Driver driver = new Driver(e1.getText().toString(),e2.getText().toString(),e3.getText().toString(),e4.getText().toString(),"33.652037","73.156598");
                            user = auth.getCurrentUser();
                            databaseReference = FirebaseDatabase.getInstance().getReference().child("Drivers").child(user.getUid());

                            databaseReference.setValue(driver)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {
                                                dialog.dismiss();
                                                Toast.makeText(getApplicationContext(),"Account created successfully",Toast.LENGTH_SHORT).show();
                                                finish();
                                                Intent myIntent = new Intent(DriverRegistrationActivity.this,MyNavigationActivity.class);
                                                startActivity(myIntent);
                                            }
                                            else
                                            {
                                                Toast.makeText(getApplicationContext(),"Could not create account",Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        }
                                    });


                        }
                    }
                });
    }
}
