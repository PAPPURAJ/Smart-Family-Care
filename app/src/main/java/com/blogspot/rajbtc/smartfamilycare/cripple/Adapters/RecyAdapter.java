package com.blogspot.rajbtc.smartfamilycare.cripple.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.rajbtc.smartfamilycare.R;
import com.blogspot.rajbtc.smartfamilycare.cripple.Models.Model;

import java.util.ArrayList;

public class RecyAdapter extends RecyclerView.Adapter<RecyAdapter.MyHolder> {

    private Context context;
    private ArrayList<Model> arrayList;

    public RecyAdapter(Context context, ArrayList<Model> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyHolder(LayoutInflater.from(context).inflate(R.layout.singleview,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.nameTv.setText(arrayList.get(position).getName());
        holder.bloodTv.setText(arrayList.get(position).getBg());
        holder.bpTv.setText(arrayList.get(position).getBp());
        holder.bsTv.setText(arrayList.get(position).getBs());
        holder.numTv.setText(arrayList.get(position).getNum());
        holder.nameTv.setText(arrayList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{

        TextView nameTv,bloodTv,bpTv,bsTv,numTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            nameTv=itemView.findViewById(R.id.name);
            bloodTv=itemView.findViewById(R.id.blood);
            bpTv=itemView.findViewById(R.id.bp);
            bsTv=itemView.findViewById(R.id.bs);
            numTv=itemView.findViewById(R.id.num);
        }
    }
}
