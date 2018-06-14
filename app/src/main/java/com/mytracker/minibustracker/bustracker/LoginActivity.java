package com.mytracker.minibustracker.bustracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText e1,e2;

    FirebaseAuth auth;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        toolbar = (Toolbar)findViewById(R.id.userToolbar);
        toolbar.setTitle("Login");
        FirebaseDatabase.getInstance().goOnline();
        setSupportActionBar(toolbar);
        e1 = (EditText)findViewById(R.id.editText2);
        e2 = (EditText)findViewById(R.id.editText3);
        auth = FirebaseAuth.getInstance();
        radioGroup = (RadioGroup) findViewById(R.id.radioOption);
        dialog = new ProgressDialog(this);

    }


    public void login(View v)
    {

        int radioButtonId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton)findViewById(radioButtonId);

        dialog.setMessage("Logging in. Please wait.");
        dialog.show();

            if(e1.getText().toString().equals("") || e2.getText().toString().equals(""))
            {
                Toast.makeText(getApplicationContext(),"Blank fields not allowed.",Toast.LENGTH_SHORT).show();
                dialog.dismiss();

            }
            else
            {
                auth.signInWithEmailAndPassword(e1.getText().toString(),e2.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful())
                                        {
                                            Toast.makeText(getApplicationContext(),"User is logged in successfully",Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                            Intent loginIntent = new Intent(getApplicationContext(),MyNavigationActivity.class);
                                            startActivity(loginIntent);
                                            finish();
                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(),"Wrong email/password combination. Try again.",Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                            }
                        });
            }
    }

}
