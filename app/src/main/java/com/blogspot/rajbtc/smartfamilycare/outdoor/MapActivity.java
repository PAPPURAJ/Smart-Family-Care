package com.blogspot.rajbtc.smartfamilycare.outdoor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blogspot.rajbtc.smartfamilycare.MyService;
import com.blogspot.rajbtc.smartfamilycare.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback  {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navView;

    private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
    private Marker[] marker;
    private GoogleMap mMap;
    private MarkerOptions firstMarker;
    private LatLng location;
    private String myEmail;
    private ArrayList<MapData> mapDataArrayList=new ArrayList<>();
    private boolean isMapLoaded=false;
    private String TAG="===MapActivity===";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        checkPermission();
        myEmail=FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".","!");

        drawerLayout = findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navView = findViewById(R.id.navigationView);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        readLocationChanges();
        initDrawable();

        loadFirestore();

    }



    private void checkPermission() {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
              //  startService(new Intent(this,MyService.class));
            }else{
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},101);
            }

        }


    private void readLocationChanges() {

        for(int i=0;i<mapDataArrayList.size();i++){
            marker[i].setPosition(new LatLng(mapDataArrayList.get(i).getLat(),mapDataArrayList.get(i).getLon()));

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==101)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission Granted!",Toast.LENGTH_SHORT).show();

                startService(new Intent(this, MyService.class));
            }else{
                Toast.makeText(this,"Permission Denied!",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_logOut){
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getApplicationContext(),"Signed out!",Toast.LENGTH_SHORT).show();
            stopService(new Intent(this,MyService.class));
            finish();
        }

        if (toggle.onOptionsItemSelected(item)) {

            return true;
        }
        return super.onOptionsItemSelected(item);

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw = (BitmapDrawable)getResources().getDrawable(R.drawable.icon);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        marker=new Marker[mapDataArrayList.size()];

        for(int i=0;i<mapDataArrayList.size();i++){
            location = new LatLng(mapDataArrayList.get(i).getLat(), mapDataArrayList.get(i).getLon());
            firstMarker=new MarkerOptions().position(location).title("Child").icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            marker[i] = mMap.addMarker(firstMarker);
            //if(i==0){}
            mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        }
        mMap.getUiSettings().setAllGesturesEnabled(true);
        Log.e(TAG,"Total array: "+mapDataArrayList.size());
    }
    void initDrawable() {
        drawerLayout = findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navView = findViewById(R.id.navigationView);
        navView.setNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.homeMenuId:
                    break;
                case R.id.addMemberMenuId:
                    EditText idEt=new EditText(this);
                    idEt.setHint("Enter email");
                    EditText passEt=new EditText(this);
                    passEt.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    passEt.setHint("Enter access password");


                    LinearLayout linearLayout=new LinearLayout(this);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.addView(idEt);
                    linearLayout.addView(passEt);

                    AlertDialog.Builder builder=new AlertDialog.Builder(this);
                    builder.setTitle("Add member");
                    builder.setView(linearLayout)
                            .setPositiveButton("Add",(dialog, which) -> {
                                String id=idEt.getText().toString();
                                String pass=passEt.getText().toString();

                                if(id.equals("") || pass.equals("")){
                                    Toast.makeText(getApplicationContext(), "Please input first!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                addFamilyMember(id,pass);

                            }).setNegativeButton("Cancel",null)
                            .create().show();
                    break;

                case R.id.memberListMenuId:
                    startActivity(new Intent(this,MemberListActivity.class));
                    break;

                case R.id.howToWorkMenuId:
                    AlertDialog.Builder howtoW=new AlertDialog.Builder(this);
                    howtoW.setTitle("How to work")
                            .setPositiveButton("Youtube", null).create().show();
                    break;

                case R.id.privacyAndPolicyMenuId:
                    AlertDialog.Builder privacyAlert=new AlertDialog.Builder(this);
                    privacyAlert.setTitle("Privacy & Policy")
                            .setPositiveButton("Ok", null)
                            .setMessage("Privacy will be updated!").create().show();
                    break;


                case R.id.contactUsMenuId:
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("https://www.linkedin.com/in/pappuraj"));
                    startActivity(i);
                    break;

                  case R.id.rateUsMenuId:
                    Intent ii = new Intent(Intent.ACTION_VIEW);
                    ii.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.blogspot.rajbtc.familyprotector"));
                    startActivity(ii);

            }drawerLayout.closeDrawers();

            return true;
        });


    }
    void addFamilyMember(String ID, String accessPass){


        firebaseDatabase.getReference("Users").child(ID.replace(".","!"))
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        MyUserData myUserData = dataSnapshot.getValue(MyUserData.class);
                        if (myUserData == null) {
                            Toast.makeText(getApplicationContext(), "User not found!", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (!myUserData.getAccessPass().equals(accessPass)) {
                            Toast.makeText(getApplicationContext(), "Access password not matched!!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Map<String,String> map=new HashMap<>();
                        map.put("ID",ID.replace(".","!"));

                        FirebaseFirestore.getInstance().collection("Family").document(myEmail).collection("Member").add(map).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(@NonNull DocumentReference documentReference) {
                                Toast.makeText(getApplicationContext(), "Member added!", Toast.LENGTH_SHORT).show();
                                loadFirestore();
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }


                });
    }
    void loadLocation(String email,int position,int totalData){
        firebaseDatabase.getReference("Users").child(email.replace(".","!")).child("Location").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                MapData mapData=dataSnapshot.getValue(MapData.class);
                if(mapData==null)
                    return;

                if(mapDataArrayList.size()+1>=totalData){
                    if(!isMapLoaded){
                        mapDataArrayList.add(mapData);
                        onMapReady(mMap);
                        isMapLoaded=true;
                    }
                    else{
                        mapDataArrayList.get(position).setLat(mapData.getLat());
                        mapDataArrayList.get(position).setLon(mapData.getLon());
                        readLocationChanges();
                    }
                }
                else{
                    mapDataArrayList.add(mapData);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        onMapReady(mMap);
    }
    void loadFirestore(){
        mapDataArrayList.clear();
        FirebaseFirestore.getInstance().collection("Family").document(myEmail).collection("Member").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(int i=0;i<queryDocumentSnapshots.size();i++){
                        loadLocation(queryDocumentSnapshots.getDocuments().get(i).get("ID").toString(),i,queryDocumentSnapshots.size());
                    }
                });
    }

}