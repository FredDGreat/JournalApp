package com.ftech.journalapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity
        implements View.OnClickListener{
    private SignInButton mSignInBtn;
    private LinearLayout mSignInBtnBg;
    //private GooglePlusSignInHelper gSignInHelper;
    private ProgressBar mProgressBar;
    //Database helper for registration and login
    //
    //firebase authentication and its listener
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    //
    GoogleApiClient mGoogleApiClient;
    //RC_SIGN_IN mValueCounter
    public static final int RC_SIGN_IN = 2;


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //track whenever the user signs in or out with google account
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser() != null){
                    Intent mIntent = new Intent(LoginActivity.this,HomeActivity.class);
                    startActivity(mIntent);
                    finish();
                }
            }
        };

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        //
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                })
        .addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        //
        mSignInBtnBg = (LinearLayout) findViewById(R.id.signInBtnBg);
        mSignInBtn = (SignInButton) findViewById(R.id.signInBtn);
        mSignInBtn.setOnClickListener(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            /*Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(sData);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }*/
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
                mProgressBar.setVisibility(View.VISIBLE);
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }else{
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Authentication went wrong!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    //user.getEmail();
                    // save user's email to for reference purposes
                    SharedPreferences mPref = getSharedPreferences("LOGIN_SESSION",MODE_PRIVATE);
                    SharedPreferences.Editor editor = mPref.edit();
                    editor.putString("email",user.getEmail());
                    editor.apply();
                    mProgressBar.setVisibility(View.GONE);
                    //updateUI(user);
                    Intent mIntent = new Intent(LoginActivity.this,HomeActivity.class);
                    startActivity(mIntent);
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();
                    //updateUI(user);
                }
            }
        });
    }
    //run task on a separate thread
    private class LoadingThread extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            doGoogleSignIn();
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            mProgressBar.setVisibility(View.GONE);

        }

        @Override
        protected void onPreExecute() {
            mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
    /**
     * Calls the GoogleSignIn function
     */
    public void doGoogleSignIn(){
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        /*if(v == mSignUpBtn){
            Intent mIntent = new Intent(LoginActivity.this,RegisterActivity.class);
            startActivity(mIntent);
        }
        if(v == mLoginBtn){
            //validateTextBox();
        }
        if(v == linkForgotPassword){
            retrievePassword();
        }*/
        if(v == mSignInBtn){
            new LoadingThread().execute("");
        }
    }
}
