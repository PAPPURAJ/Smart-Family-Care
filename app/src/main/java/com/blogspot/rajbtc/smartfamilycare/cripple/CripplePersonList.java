package com.blogspot.rajbtc.smartfamilycare.cripple;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.blogspot.rajbtc.smartfamilycare.R;
import com.blogspot.rajbtc.smartfamilycare.cripple.Adapters.RecyAdapter;
import com.blogspot.rajbtc.smartfamilycare.cripple.Models.Model;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class CripplePersonList extends AppCompatActivity {
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cripple_person_list);
        init();
        loadFirestore();
    }

    void init() {
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    void loadFirestore() {
        ArrayList<Model> arrayList = new ArrayList<>();
        FirebaseFirestore.getInstance().collection("Cripple").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for(DocumentSnapshot sn:queryDocumentSnapshots){
                            arrayList.add(new Model(sn.getReference(),sn.getString("name"),sn.getString("bp"),sn.getString("bs"),sn.getString("num"),sn.getString("bg")));
                        }
                        recyclerView.setAdapter(new RecyAdapter(CripplePersonList.this,arrayList));
                    }
                });
    }
}