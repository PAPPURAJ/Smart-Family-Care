package com.blogspot.rajbtc.smartfamilycare.cripple;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.rajbtc.smartfamilycare.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CrippleActivity extends AppCompatActivity {


    private TextView notificationTv;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cripple_lab_main);
        notificationTv=findViewById(R.id.notiTv);

      //  checkCripple();

    }


    void checkCripple(){

        DatabaseReference myRef = database.getReference("User").child("signal");
        myRef.setValue("33");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot val) {

                String value=val.getValue(String.class);

                if(mediaPlayer!=null)
                    mediaPlayer.stop();

                switch (value){
                    case "1":
                        notificationTv.setText("আমার খিদে পেয়েছে");
                        mediaPlayer=MediaPlayer.create(getApplicationContext(),R.raw.food);
                        break;
                    case "2":
                        notificationTv.setText("আমার ওষুধ প্রয়োজন");
                        mediaPlayer=MediaPlayer.create(getApplicationContext(),R.raw.medicine);
                        break;
                    case "3":
                        notificationTv.setText("আমার ওয়াশরুমে যাওয়া প্রয়োজন");
                        mediaPlayer=MediaPlayer.create(getApplicationContext(),R.raw.washroom);
                        break;
                    case "4":
                        notificationTv.setText("আমার দ্রুত সাহায্য প্রয়োজন");
                        mediaPlayer=MediaPlayer.create(getApplicationContext(),R.raw.emergency);
                        break;
                    default:
                        notificationTv.setText("আমার খেয়াল রাখবেন");
                        return;

                }mediaPlayer.start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}