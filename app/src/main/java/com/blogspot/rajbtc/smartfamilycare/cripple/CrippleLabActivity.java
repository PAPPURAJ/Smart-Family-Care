package com.blogspot.rajbtc.smartfamilycare.cripple;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.blogspot.rajbtc.smartfamilycare.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class CrippleLabActivity extends AppCompatActivity {
   Button listButton,addButton;
   TextView tv;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cripple_lab_main);

        listButton = (Button) findViewById(R.id.list_button);
        addButton = (Button) findViewById(R.id.add_button);
        tv = (TextView)findViewById(R.id.tx);
     checkCripple();

    }

    void checkCripple(){

        DatabaseReference myRef = database.getReference("CrippleCare").child("signal");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot val) {

                String value=val.getValue(String.class);

                if(mediaPlayer!=null)
                    mediaPlayer.stop();

                switch (value){


                    case "1": {
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.food);
                        tv.setText("Patient Need Food");
                        break;
                    }
                    case "2": {
                        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.medicine);
                        tv.setText("Patient Need Medicine");
                        break;
                    }
                    case "3":{
                        mediaPlayer=MediaPlayer.create(getApplicationContext(),R.raw.washroom);
                        tv.setText("Need Help for Washroom");
                        break;}
                    case "4":{
                        mediaPlayer=MediaPlayer.create(getApplicationContext(),R.raw.emergency);
                        tv.setText("It's Emergency");
                        break;}
                    default: {
                        tv.setText("!!!BE SAFE!!!");
                        return;
                    }
                }//mediaPlayer.start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void list(View view) {
        Intent intent = new Intent(this,CripplePersonList.class);
        startActivity(intent);
    }
   public void add(View view){
        Intent intent = new Intent(this,AddCripplePersonDetails.class);
        startActivity(intent);
}

}