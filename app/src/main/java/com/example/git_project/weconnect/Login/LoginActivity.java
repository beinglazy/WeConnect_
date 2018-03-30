package com.example.git_project.weconnect.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Printer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.git_project.weconnect.Home.HomeActivity;
import com.example.git_project.weconnect.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Alpa on 23-03-2018.
 * 
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    //Firebase Instances
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Context mContext;
    private ProgressBar mProgressBar;
    private EditText enrollment,password;
    private TextView pleaseWait;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    
    

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mProgressBar=(ProgressBar)findViewById(R.id.loginRequestLoadingProgressBar); 
        pleaseWait=(TextView)findViewById(R.id.logintv);
        enrollment=(EditText)findViewById(R.id.input_email);
        password=(EditText)findViewById(R.id.input_password);
        mContext=LoginActivity.this;
        TextView linkSignUp =(TextView)findViewById(R.id.signup_link);
        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to Sign up Activity");
                Intent moveToRegister=new Intent(mContext,RegisterActivity.class);
                startActivity(moveToRegister);
            }
        });

        

        mProgressBar.setVisibility(View.GONE);
        pleaseWait.setVisibility(View.GONE);
        setupFirebaseAuth();
        init();
        //checkInputs();
        
        
        
    }


    private boolean isStringNull(String string){
        Log.d(TAG, "isStringNull: Checking String parameter");
        if (string.equals("")){
            return true;
        }
        else{
            return false;
        }

    }
    //*******************************************************SET THE FIREBASE AUTH OBJECTS*********************************************************************************


    private void init(){
        //init the button for login
        Button btnLogin=(Button)findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Logging In");
                String pass,email=enrollment.getText().toString()+"@git.org.in";
                pass=password.getText().toString();
                if ( isStringNull(email) &&  isStringNull(pass)){
                    Toast.makeText(mContext,"Fill the following details",Toast.LENGTH_SHORT).show();

                }else{
                    mProgressBar.setVisibility(View.VISIBLE);
                    pleaseWait.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email, pass)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "onComplete: "+task.isSuccessful());
                                    FirebaseUser user=mAuth.getCurrentUser();

                                    if (!task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:failed",task.getException());
                                        Toast.makeText(LoginActivity.this,getString(R.string.auth_failed),Toast.LENGTH_SHORT).show();
                                        mProgressBar.setVisibility(View.GONE);
                                        pleaseWait.setVisibility(View.GONE);
                                    }else
                                    {
                                        try {
                                            if (user.isEmailVerified()){
                                                Log.d(TAG, "onComplete: success email is verified");
                                                Intent moveToHome =new Intent(mContext,HomeActivity.class);
                                                startActivity(moveToHome);


                                            }else{
                                                Toast.makeText(mContext,"Email not verified",Toast.LENGTH_LONG).show();
                                                mProgressBar.setVisibility(View.GONE);
                                                pleaseWait.setVisibility(View.GONE);
                                                mAuth.signOut();
                                            }

                                        }catch (NullPointerException e){
                                            Log.e(TAG, "onComplete:  "+ e.getMessage() );

                                        }
                                    }

                                    // ...
                                }
                            });
                }
            }
        });




        // if the user is logged in then it directly goes to HomeActivity
        if (mAuth.getCurrentUser()!=null){
            Intent moveToHome= new Intent(mContext, HomeActivity.class);
            moveToHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(moveToHome);
            finish();

        }
    }
   



    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth=FirebaseAuth.getInstance();
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        //myRef=mFirebaseDatabase.getReference();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user= firebaseAuth.getCurrentUser();
                
                if (user!=null){
                    Log.d(TAG, "onAuthStateChanged: signedin:"+user.getUid());



                }else{
                    Log.d(TAG, "onAuthStateChanged: signed out");
                }
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
