package com.mytracker.minibustracker.bustracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LocationShareService extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener{
    public LocationShareService() {
    }

    GoogleApiClient client;
    LocationRequest request;
    LatLng latLngCurrent;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser user;

    NotificationCompat.Builder notification;
    public final int uniqueId = 654321;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.


        throw new UnsupportedOperationException("Not yet implemented");




    }

    @Override
    public void onCreate() {
        super.onCreate();
        reference = FirebaseDatabase.getInstance().getReference().child("Drivers");
        auth = FirebaseAuth.getInstance();
        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(false);
        notification.setOngoing(true);

        user = auth.getCurrentUser();
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();


    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest().create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(1000);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);

        notification.setSmallIcon(R.drawable.share_location);
        notification.setTicker("Notification.");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("Trash Collector");
        notification.setContentText("You are sharing your location.!");
        notification.setDefaults(Notification.DEFAULT_SOUND);


        Intent intent = new Intent(getApplicationContext(),MyNavigationActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        notification.setContentIntent(pendingIntent);

        // Build the nofification

        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        nm.notify(uniqueId,notification.build());




        // display notification
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        latLngCurrent = new LatLng(location.getLatitude(), location.getLongitude());

        shareLocation();


    }




    public void shareLocation()
    {
        try
        {
            reference.child(user.getUid()).child("lat").setValue(String.valueOf(latLngCurrent.latitude));
            reference.child(user.getUid()).child("lng").setValue(String.valueOf(latLngCurrent.longitude))
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful())
                            {
                                Toast.makeText(getApplicationContext(),"Could not share Location.",Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }catch(Exception e)
        {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Only drivers can share their location",Toast.LENGTH_SHORT).show();

        }

    }


    @Override
    public void onDestroy() {
        LocationServices.FusedLocationApi.removeLocationUpdates(client,this);
        client.disconnect();
      //  reference.child(user.getUid()).child("issharing").setValue("false");

        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(uniqueId);



    }
}
