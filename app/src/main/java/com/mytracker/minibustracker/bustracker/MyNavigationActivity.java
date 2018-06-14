package com.mytracker.minibustracker.bustracker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

public class MyNavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback
        , GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks
        , LocationListener,GoogleMap.OnMarkerClickListener {


    GoogleMap mMap;
    GoogleApiClient client;
    LocationRequest request;
    LatLng latLngCurrentuserLocation;
    FirebaseAuth auth;
    HashMap<String,Marker> hashMap;

    boolean driver_profile = false;
    AdView adView;
    boolean user_profile = false;
    LatLng updateLatLng;
    DatabaseReference referenceDrivers,referenceUsers;
    boolean hideMarker = false;
    TextView textName,textEmail;
    InterstitialAd interstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();
    interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.interstitial));
        interstitialAd.loadAd(new AdRequest.Builder().build());

//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        textName = (TextView) header.findViewById(R.id.title_text);
        textEmail = (TextView) header.findViewById(R.id.email_text);


        adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);





        referenceDrivers = FirebaseDatabase.getInstance().getReference().child("Drivers");
        referenceUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        hashMap = new HashMap<>();



        referenceDrivers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user = auth.getCurrentUser();
                if(dataSnapshot.child(user.getUid()).child("lat").exists())
                {
                    driver_profile= true;
                    String driver_name = dataSnapshot.child(user.getUid()).child("name").getValue(String.class);
                    String driver_email = dataSnapshot.child(user.getUid()).child("email").getValue(String.class);
                    textName.setText(driver_name);
                    textEmail.setText(driver_email);

                }
                else
                {
                    user_profile = true;


                    referenceUsers.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            FirebaseUser user1 = auth.getCurrentUser();
                            String user_name = dataSnapshot.child(user1.getUid()).child("name").getValue(String.class);
                            String user_email = dataSnapshot.child(user1.getUid()).child("email").getValue(String.class);
                            textName.setText(user_name);
                            textEmail.setText(user_email);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });




                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






        referenceDrivers.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                try
                {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String lat = dataSnapshot.child("lat").getValue(String.class);
                    String lng = dataSnapshot.child("lng").getValue(String.class);
                    String vehicle_number = dataSnapshot.child("vehiclenumber").getValue(String.class);
                    LatLng latlng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));

                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.title(name);
                    markerOptions.snippet("Van number: "+vehicle_number);
                    markerOptions.position(latlng);
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.mynewbusicon));

                    Marker myMarker = mMap.addMarker(markerOptions);

                    hashMap.put(myMarker.getTitle(),myMarker);
                }catch(Exception e)
                {
                    e.printStackTrace();
                    // Toast.makeText(getApplicationContext(),"Error = "+e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                try
                {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String lat = dataSnapshot.child("lat").getValue().toString();
                    String lng = dataSnapshot.child("lng").getValue().toString();

                    updateLatLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));

                    final Marker marker = hashMap.get(name);

                    if(marker!= null)
                    {
                        marker.setPosition(updateLatLng);
                    }

                }catch(Exception e)
                {
                    e.printStackTrace();
                }




            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        if(interstitialAd.isLoaded())
        {
            interstitialAd.show();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMarkerClickListener(this);
        // Add a marker in Sydney and move the camera
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .build();

        client.connect();
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng marker_Pos = marker.getPosition();

        double distance = CalculationByDistance(latLngCurrentuserLocation,marker_Pos);
        DecimalFormat df = new DecimalFormat("#.##");
        String dist = df.format(distance);

        Toast.makeText(getApplicationContext(),dist + " KM far.",Toast.LENGTH_SHORT).show();

        //      marker.setSnippet(dist + " KM far.");

        StringBuilder sb;
        Object[] dataTransfer = new Object[5];

        sb = new StringBuilder();
        sb.append("https://maps.googleapis.com/maps/api/directions/json?");
        sb.append("origin=" + marker_Pos.latitude + "," + marker_Pos.longitude);
        sb.append("&destination=" + latLngCurrentuserLocation.latitude + "," + latLngCurrentuserLocation.longitude);
        sb.append("&key=" + "PUT YOUR KEY HERE (DIRECTIONS)");



        DirectionAsync getDirectionsData = new DirectionAsync(getApplicationContext());
        dataTransfer[0] = mMap;
        dataTransfer[1] = sb.toString();
        dataTransfer[2] = new LatLng(marker_Pos.latitude, marker_Pos.longitude);
        dataTransfer[3] = new LatLng(latLngCurrentuserLocation.latitude, latLngCurrentuserLocation.longitude);
        dataTransfer[4] = marker;

        getDirectionsData.execute(dataTransfer);








        return true;
    }

    private double CalculationByDistance(LatLng start, LatLng end)
    {
        int Radius=6371;//radius of earth in Km
        double lat1 = start.latitude;
        double lat2 = end.latitude;
        double lon1 = start.longitude;
        double lon2 = end.longitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult= Radius*c;
        double km=valueResult/1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec =  Integer.valueOf(newFormat.format(km));
        double meter=valueResult%1000;
        int  meterInDec= Integer.valueOf(newFormat.format(meter));


        return meter;

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_signout) {
            if (auth != null) {
                auth.signOut();
                finish();
                Intent myIntent = new Intent(MyNavigationActivity.this, MainActivity.class);
                startActivity(myIntent);
            }

        }

        else if(id == R.id.nav_share_Location)
        {
            if(isServiceRunning(getApplicationContext(),LocationShareService.class))
            {
                Toast.makeText(getApplicationContext(),"You are already sharing your location.",Toast.LENGTH_SHORT).show();
            }
            else if(driver_profile)
            {
                interstitialAd.loadAd(new AdRequest.Builder().build());
                if(interstitialAd.isLoaded())
                {
                    interstitialAd.show();
                    interstitialAd.setAdListener(new AdListener()
                    {
                        @Override
                        public void onAdClosed() {
                            super.onAdClosed();
                            Intent myIntent = new Intent(MyNavigationActivity.this,LocationShareService.class);
                            startService(myIntent);
                            interstitialAd.loadAd(new AdRequest.Builder().build());
                        }
                    });
                }
                else
                {
                    Intent myIntent = new Intent(MyNavigationActivity.this,LocationShareService.class);
                    startService(myIntent);
                    interstitialAd.loadAd(new AdRequest.Builder().build());
                }

            }
            else
            {
                Toast.makeText(getApplicationContext(),"Only driver can share location",Toast.LENGTH_SHORT).show();
            }

        }
        else if(id == R.id.nav_stop_Location)
        {

            Intent myIntent2 = new Intent(MyNavigationActivity.this,LocationShareService.class);
            stopService(myIntent2);
        }
        else if(id == R.id.nav_schedule)
        {
            if(interstitialAd.isLoaded())
            {
                interstitialAd.show();
                interstitialAd.setAdListener(new AdListener()
                {
                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        Intent m = new Intent(MyNavigationActivity.this,ScheduleActivity.class);
                        startActivity(m);
                        interstitialAd.loadAd(new AdRequest.Builder().build());
                    }
                });

            }
            else
            {
                Intent m = new Intent(MyNavigationActivity.this,ScheduleActivity.class);
                startActivity(m);
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public boolean isServiceRunning(Context c, Class<?> serviceClass)
    {
        ActivityManager activityManager = (ActivityManager)c.getSystemService(Context.ACTIVITY_SERVICE);


        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);



        for(ActivityManager.RunningServiceInfo runningServiceInfo : services)
        {
            if(runningServiceInfo.service.getClassName().equals(serviceClass.getName()))
            {
                return true;
            }
        }

        return false;


    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(8000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
    }

    @Override
    public void onLocationChanged(Location location) {
        LocationServices.FusedLocationApi.removeLocationUpdates(client,this);

        if(location == null)
        {
            Toast.makeText(getApplicationContext(),"Could not find location",Toast.LENGTH_SHORT).show();
        }
        else
        {
            latLngCurrentuserLocation = new LatLng(location.getLatitude(),location.getLongitude());

            mMap.addMarker(new MarkerOptions().position(latLngCurrentuserLocation).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))).setVisible(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngCurrentuserLocation, 15));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().goOnline();

    }

    @Override
    protected void onPause() {
        super.onPause();
            FirebaseDatabase.getInstance().goOffline();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseDatabase.getInstance().goOffline();
    }
}
