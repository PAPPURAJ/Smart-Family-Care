package com.blogspot.rajbtc.smartfamilycare.outdoor;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MyService extends Service implements  LocationListener{
    private NotificationChannel serviceChannel;


    private final int MIN_TIME=10000;
    private final int MIN_DISTANCE=3;
    private LocationManager manager;
    private Context context;
    private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    private String myEmail;




    String TAG="===Services===";
    public static final String CHANNEL_ID = "ForegroundServiceChannel";




    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "========OnBind========");
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "====On Create====");
        myEmail= FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".","!");
        manager=(LocationManager)getSystemService(LOCATION_SERVICE);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "====On Start Command====");
        createNotificationChannel();

        Intent notificationIntent = new Intent(this, MapActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        final Notification notification= new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .build();


        startForeground(1, notification);


    getLocationUpdates();

        return START_STICKY;
    }



    @Override
    public void onDestroy() {

        super.onDestroy();
    }







    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }



    private void getLocationUpdates() {
        if(manager!=null){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME,MIN_DISTANCE, (LocationListener) this);
                else if(manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,MIN_TIME,MIN_DISTANCE, (LocationListener) this);
                }else{
                    Toast.makeText(this, "No provider enable!", Toast.LENGTH_SHORT).show();
                }
            }else{
                //ActivityCompat.requestPermissions(context,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},101);
            }

        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        if(location!=null){
                saveLocation(location);
        }else{
            Toast.makeText(this, "No location", Toast.LENGTH_SHORT).show();
        }

    }
    private void saveLocation(Location location) {
        MapData mapData=new MapData(location.getLatitude(),location.getLongitude());
        firebaseDatabase.getReference("Users/"+myEmail+"/Location").setValue(mapData);
    }


}