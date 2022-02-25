package com.blogspot.rajbtc.smartfamilycare.cripple;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.blogspot.rajbtc.smartfamilycare.R;
import com.blogspot.rajbtc.smartfamilycare.cripple.Models.Model;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddCripplePersonDetails extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private  FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference reference = db.getReference().child("Users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cripple_person_details);

        Spinner spinner = findViewById(R.id.bloodGroup);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.Blood_Group, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
           spinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
      String s = adapterView.getItemAtPosition(i).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public  void add(View view){

        final String name,bg,code,mobile,disease,description;
        name=((EditText)findViewById(R.id.UserName)).getText().toString();
        bg = ((Spinner)findViewById(R.id.bloodGroup)).getSelectedItem().toString();
        code =((EditText)findViewById(R.id.code)).getText().toString();
        mobile = ((EditText)findViewById(R.id.mobile)).getText().toString();
        disease = ((EditText)findViewById(R.id.disease1)).getText().toString();
        description = ((EditText)findViewById(R.id.disease2)).getText().toString();
        if(name.equals("") || bg.equals("") || code.equals("") || mobile.equals("") || disease.equals("") || description.equals(""))
            {
                Toast.makeText(getApplicationContext(), "Please input first", Toast.LENGTH_SHORT).show();


                return;

            }
            else {
            Model model=new Model(null,name,null,null,mobile,bg);
            FirebaseFirestore.getInstance().collection("Cripple").add(model).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    Toast.makeText(getApplicationContext(),"Data added!",Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
            }
        }





    }
