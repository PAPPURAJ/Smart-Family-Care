package com.blogspot.rajbtc.smartfamilycare.outdoor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.blogspot.rajbtc.smartfamilycare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class MemberListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private String myEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);
        init();
        loadFirestore();
    }

    void init(){
        recyclerView=findViewById(R.id.memberListRecy);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myEmail= FirebaseAuth.getInstance().getCurrentUser().getEmail().replace(".","!");


    }


    void loadFirestore(){
        ArrayList<MemberlistData> arrayList=new ArrayList<>();
        FirebaseFirestore.getInstance().collection("Family").document(myEmail).collection("Member").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for(int i=0;i<queryDocumentSnapshots.size();i++){
                        arrayList.add(new MemberlistData(queryDocumentSnapshots.getDocuments().get(i).getReference(),queryDocumentSnapshots.getDocuments().get(i).getString("ID")));
                    }
                    recyclerView.setAdapter(new MemberListAdapter(this,arrayList));
                });
    }
}