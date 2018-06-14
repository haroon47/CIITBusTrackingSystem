package com.mytracker.minibustracker.bustracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ScheduleActivity extends AppCompatActivity {

    Toolbar toolbar;
AdView adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        toolbar = (Toolbar)findViewById(R.id.driverToolbar);
        toolbar.setTitle("Bus Schedule");
        setSupportActionBar(toolbar);
        adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


    }


}
