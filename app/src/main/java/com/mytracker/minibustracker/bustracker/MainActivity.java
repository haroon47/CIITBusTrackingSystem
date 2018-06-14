package com.mytracker.minibustracker.bustracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.karan.churi.PermissionManager.PermissionManager;

public class MainActivity extends AppCompatActivity {


    Toolbar toolbar;
    PermissionManager permissionManager;
    FirebaseAuth auth;
    FirebaseUser user;
    AdView adView;
    InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        permissionManager = new PermissionManager() {};
        permissionManager.checkAndRequestPermissions(this);


        if(user == null)
        {
            setContentView(R.layout.activity_main);
            setContentView(R.layout.activity_main);
            toolbar = (Toolbar)findViewById(R.id.myToolbar);
            toolbar.setTitle("CIIT Bus Tracker");
            setSupportActionBar(toolbar);
            interstitialAd = new InterstitialAd(this);
            interstitialAd.setAdUnitId(getResources().getString(R.string.interstitial));
            interstitialAd.loadAd(new AdRequest.Builder().build());
            FirebaseDatabase.getInstance().goOnline();


            adView = (AdView)findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
        }
        else
        {
            Intent myIntent = new Intent(MainActivity.this,MyNavigationActivity.class);
            startActivity(myIntent);
            finish();

        }


    }


    public void registerAsUser(View v)
    {
        if(interstitialAd.isLoaded())
        {
            interstitialAd.show();
            interstitialAd.setAdListener(new AdListener()
            {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    Intent myIntent = new Intent(getApplicationContext(),UserRegistrationActivity.class);
                    startActivity(myIntent);
                }
            });

        }
        else
        {
            Intent myIntent = new Intent(getApplicationContext(),UserRegistrationActivity.class);
            startActivity(myIntent);
            interstitialAd.loadAd(new AdRequest.Builder().build());
        }

    }

    public void registerAsDriver(View v)
    {
        if(interstitialAd.isLoaded())
        {
            interstitialAd.show();
            interstitialAd.setAdListener(new AdListener()
            {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    Intent myIntent = new Intent(getApplicationContext(),DriverRegistrationActivity.class);
                    startActivity(myIntent);
                }
            });

        }
        else
        {
            Intent myIntent = new Intent(getApplicationContext(),DriverRegistrationActivity.class);
            startActivity(myIntent);
            interstitialAd.loadAd(new AdRequest.Builder().build());
        }
    }

    public void login(View v)
    {
        Intent myIntent = new Intent(this,LoginActivity.class);
        startActivity(myIntent);

    }



}
