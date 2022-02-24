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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.blogspot.rajbtc.smartfamilycare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MyService extends Service implements  LocationListener{
    private NotificationChannel serviceChannel;


    private final int MIN_TIME=10000;
    private final int MIN_DISTANCE=3;
    private LocationManager manager;
    private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    private String myEmail;


    private ArrayList<MemberlistData> mapDataArrayList=new ArrayList<>();

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float sensorSummation;

    private MediaPlayer mediaPlayer;



    private boolean canAlert=true;

    private Handler handler=new Handler();
    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            if(sensorSummation>50)
                handler.postDelayed(runnable,10000);
            else{
                firebaseDatabase.getReference("Users/"+myEmail+"/danger").setValue("0");
                canAlert=true;
            }
        }
    };


    private SensorEventListener sensorEvenListener = new SensorEventListener() {




        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            sensorSummation=x+y+z;

            if(sensorSummation>50 && canAlert){
                setDanger();
            }


        }



        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };



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

        mediaPlayer=MediaPlayer.create(this,R.raw.danger_alarm);

        myEmail= FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".","!");
        manager=(LocationManager)getSystemService(LOCATION_SERVICE);
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);


        mSensorManager =(SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

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

        mSensorManager.registerListener(sensorEvenListener,mAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        getLocationUpdates();
        loadFirestore();

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

    private void setDanger(){
        firebaseDatabase.getReference("Users/"+myEmail+"/danger").setValue("1");
        handler.postDelayed(runnable,10000);
    }

    void loadFirestore(){
        mapDataArrayList.clear();
        mediaPlayer=MediaPlayer.create(getApplicationContext(),R.raw.danger_alarm);
        FirebaseFirestore.getInstance().collection("Family").document(myEmail).collection("Member").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(int i=0;i<queryDocumentSnapshots.size();i++){
                        setAlert(queryDocumentSnapshots.getDocuments().get(i).get("ID").toString());
                    }
                });
    }

    void setAlert(String email){
        firebaseDatabase.getReference("Users").child(email).child("danger").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String val=snapshot.getValue(String.class);
                if(val==null)
                    return;

                Log.e(TAG,"==========Email: "+email+"   Value: "+val);
                if(val.equals("1")){
                    if(!mediaPlayer.isPlaying())
                            mediaPlayer.start();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}