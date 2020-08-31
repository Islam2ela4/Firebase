package com.example.firebase_auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.example.firebase_auth.databinding.ActivityForgetPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    ActivityForgetPasswordBinding binding;
    FirebaseAuth mAuth;
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forget_password);

        mAuth = FirebaseAuth.getInstance();
        toast = new Toast(this);

    }

    public void btnClick(View view){
        mAuth.sendPasswordResetEmail(binding.emailPass.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            toast.createToast("Check your inbox");
                        }else {
                            toast.createToast("Fail to send email");
                        }
                    }
                });
    }
}