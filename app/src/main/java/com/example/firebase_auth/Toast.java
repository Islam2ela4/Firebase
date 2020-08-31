package com.example.firebase_auth;

import android.content.Context;

public class Toast {

    Context context;

    public Toast(Context context){
        this.context = context;
    }

    public void createToast(String text){
        android.widget.Toast.makeText(context, text, android.widget.Toast.LENGTH_SHORT).show();
    }
}
