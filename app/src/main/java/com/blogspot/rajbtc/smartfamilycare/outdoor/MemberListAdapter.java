package com.blogspot.rajbtc.smartfamilycare.outdoor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.rajbtc.smartfamilycare.R;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class MemberListAdapter extends RecyclerView.Adapter<MemberListAdapter.MemberListViewHolder> {

    private Context context;
    private ArrayList<MemberlistData> arrayList;

    public MemberListAdapter(Context context, ArrayList<MemberlistData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MemberListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MemberListViewHolder(LayoutInflater.from(context).inflate(R.layout.single_memberlist,parent,false));
    }


    @Override
    public void onBindViewHolder(@NonNull MemberListViewHolder holder, int position) {
        holder.memberEmailTv.setText(arrayList.get(position).getEmail());
        holder.itemView.setOnLongClickListener(view -> {
            deleteData(position);
            return true;
        });
    }

    void deleteData(int position){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Want to delete?")
                .setNegativeButton("No",null)
                .setPositiveButton("Yes", (dialogInterface, i) -> arrayList.get(position).getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(@NonNull Void unused) {
                        Toast.makeText(context, "Member deleted!", Toast.LENGTH_SHORT).show();
                        arrayList.remove(position);
                        notifyDataSetChanged();
                    }
                })).create().show();
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MemberListViewHolder extends RecyclerView.ViewHolder{

        TextView memberEmailTv;
        public MemberListViewHolder(@NonNull View itemView) {
            super(itemView);
            memberEmailTv=itemView.findViewById(R.id.singleMemberListTv);
        }
    }
}
