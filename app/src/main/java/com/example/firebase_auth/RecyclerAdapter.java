package com.example.firebase_auth;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    Context context;
    ArrayList<User> list;
    Toast toast;

    public RecyclerAdapter(Context context, ArrayList<User> list){
        this.context = context;
        this.list = list;
        toast = new Toast(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        MyViewHolder holder = new MyViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = list.get(position);
        if (user.getFirstName().equals(user.getLastName())){
            holder.txt_name.setText(user.getFirstName());
        }else {
            holder.txt_name.setText(user.getFirstName() + " " + user.getLastName());
        }
        holder.txt_email.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_name, txt_email;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.display_name);
            txt_email = itemView.findViewById(R.id.email);
        }
    }
}
