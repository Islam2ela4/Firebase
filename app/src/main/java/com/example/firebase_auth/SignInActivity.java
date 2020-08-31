package com.example.firebase_auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.firebase_auth.databinding.ActivitySignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignInActivity extends AppCompatActivity {

    ActivitySignInBinding binding;
    private FirebaseAuth auth;
    com.example.firebase_auth.Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);
        Toast.makeText(SignInActivity.this, "From SignInActivity", Toast.LENGTH_SHORT).show();

        auth = FirebaseAuth.getInstance();
        toast = new com.example.firebase_auth.Toast(this);
    }

    public void on_click_signIn(View view){
        sign_in(binding.edtEmail.getText().toString().trim(), binding.edtPassword.getText().toString().trim());
    }

    private void sign_in(String email, String password){
        if (validation()) {
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                toast.createToast("Login successfully");
                                FirebaseUser user = auth.getCurrentUser();
                                actionUpdate(user);
                            } else {
                                toast.createToast("Login failed");
                                actionUpdate(null);
                            }
                        }
                    });
        }
    }

    private Boolean validation(){
        if (binding.edtEmail.getText().toString().trim().isEmpty()) {
            binding.edtEmail.setError("Please enter your email");
            binding.edtEmail.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(binding.edtEmail.getText().toString().trim()).matches()){
            binding.edtEmail.setError("Please enter right email");
            binding.edtEmail.requestFocus();
            return false;
        }
        if (binding.edtPassword.getText().toString().trim().isEmpty()){
            binding.edtPassword.setError("Please enter password");
            binding.edtPassword.requestFocus();
            return false;
        }
        if (binding.edtPassword.getText().toString().trim().length() < 6){
            binding.edtPassword.setError("Password size should be greater than 6");
            binding.edtPassword.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void actionUpdate(FirebaseUser user){
        if (user != null){
            Intent intent = new Intent(SignInActivity.this, StartActivity.class);
            intent.putExtra("name", auth.getCurrentUser().getEmail());
            intent.putExtra("photo", "default");
            startActivity(intent);
            toast.createToast("User email: " + user.getEmail());
            finish();
        }else {
            binding.edtPassword.setText("");
        }
    }
}