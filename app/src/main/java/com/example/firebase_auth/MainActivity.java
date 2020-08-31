package com.example.firebase_auth;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.firebase_auth.databinding.ActivityMainBinding;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth auth;
    DatabaseReference reference;
    Toast toast;


    // google sign in
    private static final int RC_SIGN_IN_GOOGLE = 9001;

    private GoogleSignInClient mGoogleSignInClient;

    // facebook
    private CallbackManager mCallbackManager;
    String TAG = "facebook";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // to enable facebool login button / should be before setContent
        FacebookSdk.sdkInitialize(getApplicationContext());
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        toast = new Toast(this);


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();

    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser user = auth.getCurrentUser();
        if (user != null){
//            reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
//            reference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    User u = snapshot.getValue(User.class);
//                    Intent intent = new Intent(MainActivity.this, StartActivity.class);
//                    intent.putExtra("name", user.getEmail());
//                    if (u.getImageURL().equals("default")){
//                        intent.putExtra("photo", "default");
//                    }else {
//                        intent.putExtra("photo", u.getImageURL());
//                    }
//                    startActivity(intent);
//                    toast.createToast("User email: " + user.getEmail());
//                    finish();
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });

            Intent intent = new Intent(MainActivity.this, StartActivity.class);
            startActivity(intent);
            toast.createToast("User email: " + user.getEmail());
            finish();
        }
    }


    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_sign_in:
                Intent intent1 = new Intent(MainActivity.this, SignInActivity.class);
                startActivity(intent1);
                break;
            case R.id.btn_sign_up:
                Intent intent2 = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent2);
                break;
            case R.id.sign_in_button_google:
                sign_with_google();
                break;
            case R.id.btn_sign_with_facebook:
                sign_in_with_facebook();
                break;
            case R.id.forget_pass:
                startActivity(new Intent(MainActivity.this, ForgetPassword.class));
                break;
            case R.id.all_users:
                startActivity(new Intent(MainActivity.this, UsersActivity.class));
                break;
        }
    }

    // sign google
    private void sign_with_google(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                toast.createToast("firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                toast.createToast("Google sign in failed");
                // ...
            }
        }

        // facebook
        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
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
                            map.put("FirstName", user.getDisplayName());
                            map.put("LastName", user.getDisplayName());
                            map.put("Email", user.getEmail());
                            map.put("ImageURL", user.getPhotoUrl().toString());

                            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        toast.createToast("User created successfully");

                                        toast.createToast("signInWithCredential:success");
                                        Intent intent = new Intent(MainActivity.this, StartActivity.class);
//                                        intent.putExtra("name", user.getDisplayName());
//                                        intent.putExtra("photo", user.getPhotoUrl().toString());
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            toast.createToast("signInWithCredential:failure");
                        }

                        // ...
                    }
                });
    }

    private void sign_in_with_facebook(){
        binding.btnSignWithFacebook.setReadPermissions("email", "public_profile");
        binding.btnSignWithFacebook.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                toast.createToast("facebook:onSuccess:" + loginResult);
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                toast.createToast("facebook:onCancel");
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                toast.createToast("facebook:onError" + error);
                Log.d(TAG, "facebook:onError" + error);
                // ...
            }
        });
    }


    private void handleFacebookAccessToken(final AccessToken token) {
        toast.createToast("handleFacebookAccessToken:" + token);
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            final FirebaseUser user = auth.getCurrentUser();
                            // database
                            final String userID = user.getUid();
                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userID);

                            HashMap<String, String> map = new HashMap<>();
                            map.put("userID", userID);
                            map.put("FirstName", user.getDisplayName());
                            map.put("LastName", user.getDisplayName());
                            map.put("Email", user.getEmail());
                            map.put("ImageURL", user.getPhotoUrl().toString());

                            reference.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        toast.createToast("User created successfully");

                                        toast.createToast("signInWithCredential:success");
                                        Intent intent = new Intent(MainActivity.this, StartActivity.class);
//                                        intent.putExtra("name", user.getDisplayName());
//                                        intent.putExtra("photo", user.getPhotoUrl().toString());
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            toast.createToast("signInWithCredential:failure"+ task.getException());
                            toast.createToast("Authentication failed.");
                            Log.d(TAG, "signInWithCredential:failure"+ task.getException());
                            Log.d(TAG, "Authentication failed.");
                        }

                        // ...
                    }
                });
    }


}