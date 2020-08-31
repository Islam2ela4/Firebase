package com.example.firebase_auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import com.example.firebase_auth.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;
    FirebaseAuth auth;
    DatabaseReference reference;
    com.example.firebase_auth.Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        toast = new com.example.firebase_auth.Toast(this);
        toast.createToast("From RegisterActivity");

    }

    public void onClick_sign_up(View view){
        sign_up(binding.edtEmail.getText().toString().trim(), binding.edtPassword.getText().toString().trim());
    }

    private void sign_up(final String email, String password){
        if (validation()) {
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                final FirebaseUser user = auth.getCurrentUser();
                                // database
                                String userID = user.getUid();
                                reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

                                HashMap<String, String> map = new HashMap<>();
                                map.put("userID", userID);
                                map.put("FirstName", binding.edtFname.getText().toString());
                                map.put("LastName", binding.edtLname.getText().toString());
                                map.put("Email", binding.edtEmail.getText().toString());
                                map.put("ImageURL", "default");

                                reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            toast.createToast("User created successfully");
                                            actionUpdate(user);
                                        }
                                    }
                                });
                            } else{
                                toast.createToast("Fail to create user");
                                actionUpdate(null);
                            }
                        }
                    });
        }
    }


    private Boolean validation(){
        if (binding.edtFname.getText().toString().trim().isEmpty()) {
            binding.edtFname.setError("Please enter first name");
            binding.edtFname.requestFocus();
            return false;
        }
        if (binding.edtLname.getText().toString().trim().isEmpty()) {
            binding.edtLname.setError("Please enter last name");
            binding.edtLname.requestFocus();
            return false;
        }
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
        }
        if (binding.edtConfirmPassword.getText().toString().trim().isEmpty()){
            binding.edtConfirmPassword.setError("Please enter confirm password");
            binding.edtConfirmPassword.requestFocus();
            return false;
        }
        if (!binding.edtPassword.getText().toString().trim().equals(binding.edtConfirmPassword.getText().toString().trim())){
            binding.edtConfirmPassword.setError("Please confirm your password");
            binding.edtConfirmPassword.requestFocus();
            return false;
        }else {
            return true;
        }
    }

    private void actionUpdate(FirebaseUser user){
        if (user != null){
            Intent intent = new Intent(RegisterActivity.this, StartActivity.class);
//            intent.putExtra("name", auth.getCurrentUser().getEmail());
//            intent.putExtra("photo", "default");
            startActivity(intent);
            toast.createToast("User email: " + user.getEmail());
            finish();
        }else {
            binding.edtPassword.setText("");
            binding.edtConfirmPassword.setText("");
        }
    }

}