package com.blogspot.rajbtc.smartfamilycare;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.blogspot.rajbtc.smartfamilycare.cripple.CrippleActivity;
import com.blogspot.rajbtc.smartfamilycare.cripple.CrippleLabActivity;
import com.blogspot.rajbtc.smartfamilycare.cripple.CripplePersonList;
import com.blogspot.rajbtc.smartfamilycare.outdoor.MapActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private final int REQ_CODE = 100;
    private TextToSpeech t1;
    private String myEmail;
    private FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();

    private String temperature="null", signal="null";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView speak = findViewById(R.id.iv_mic);





        loadCrippleData();
      //  startActivity(new Intent(this, MapActivity.class));

        t1=new TextToSpeech(getApplicationContext(), status -> {
            if(status != TextToSpeech.ERROR) {
                t1.setLanguage(Locale.UK);
            }
        });

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                        "Need to speak");
                try {
                    startActivityForResult(intent, REQ_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry your device not supported",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        startService(new Intent(this, MyService.class));


    }

    void loadCrippleData(){
        firebaseDatabase.getReference("CrippleCare")
                .child("tempareture").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String str=snapshot.getValue(String.class);
                if(str!=null)
                    temperature=str;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        firebaseDatabase.getReference("CrippleCare")
                .child("signal").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String str=snapshot.getValue(String.class);
                if(str!=null)
                    switch (str){
                        case "1":
                            signal="Patient need food!";
                            break;
                        case "2":
                            signal="Patient need medicine!";
                            break;
                        case "3":
                            signal="Patient need to go washroom!";
                            break;
                        case "4":
                            signal="Patient need someone emergency!";
                            break;
                        default:
                            signal="Patient is in normal condition!";

                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.menu_logOut){
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getApplicationContext(),"Signed out!",Toast.LENGTH_SHORT).show();
            stopService(new Intent(this,MyService.class));
            finish();
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(this, ""+result.get(0), Toast.LENGTH_SHORT).show();


                    String voiceInput=result.get(0);
                    if(voiceInput.contains("temperature"))
                        t1.speak("The temperature of the patient is "+temperature+" degree celsius", TextToSpeech.QUEUE_FLUSH, null);
                    else if(voiceInput.contains("patient") || voiceInput.contains("about"))
                        t1.speak(signal, TextToSpeech.QUEUE_FLUSH, null);
                }
                break;
            }
        }
    }

    public void familyProtectorClick(View view) {
        startActivity(new Intent(this,MapActivity.class));
    }

    public void crippleCareClick(View view) {
        startActivity(new Intent(this, CrippleLabActivity.class));
    }


}