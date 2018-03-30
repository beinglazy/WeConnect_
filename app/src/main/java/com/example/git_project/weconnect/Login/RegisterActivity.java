package com.example.git_project.weconnect.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.git_project.weconnect.Home.HomeActivity;
import com.example.git_project.weconnect.R;
import com.example.git_project.weconnect.Utils.FirebaseMethods;
import com.example.git_project.weconnect.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Alpa on 23-03-2018.
 * 
 */

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private Context mContext;
    private ProgressBar mProgressBar;
    private Button register;
    private String enrollment,username,password;
    private EditText mEnrollment,mUsername,mPassword;
    private TextView pleaseWait;
    //Firebase Instances
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods firebaseMethods;


    private String append="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext=RegisterActivity.this;
        firebaseMethods=new FirebaseMethods(mContext);
        Log.d(TAG, "onCreate: started");
        initWidgets();
        setupFirebaseAuth();
        init();



    }
    private void init(){
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enrollment=mEnrollment.getText().toString()+"@git.org.in";
                username=mUsername.getText().toString();
                password=mPassword.getText().toString();
                if (checkInputs(enrollment,username,password)){
                    mPassword.setVisibility(View.VISIBLE);
                    pleaseWait.setVisibility(View.VISIBLE);
                    firebaseMethods.registerNewEmail(enrollment,password,username);
                }

            }
        });

    }
    private boolean checkInputs(String enrollment,String username,String password){
        Log.d(TAG, "checkInputs: checking for the null values");
        if (enrollment.equals("") || password.equals("")|| username.equals("")){
            Toast.makeText(mContext,"Enter all details",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void initWidgets(){
        Log.d(TAG, "initWidgets: Initializing Widgets .");
        mEnrollment=(EditText)findViewById(R.id.input_register_email);
        mProgressBar=(ProgressBar)findViewById(R.id.registerRequestLoadingProgressBar);
        pleaseWait=(TextView)findViewById(R.id.registertv);
        mUsername=(EditText)findViewById(R.id.input_username);
        mPassword=(EditText)findViewById(R.id.input_password);
        mContext=RegisterActivity.this;
        mProgressBar.setVisibility(View.GONE);
        pleaseWait.setVisibility(View.GONE);
        register=(Button)findViewById(R.id.btn_register);
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



    //checks if the username is already exists
    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: checking if"+username+"already Exists");
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        Query query=reference.child(getString(R.string.dbname_users)).orderByChild(getString(R.string.field_username)).equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot singleSnapshot:dataSnapshot.getChildren()){
                    if (singleSnapshot.exists()){
                        Log.d(TAG, "checkIfUsernameExists: Found a match "+singleSnapshot.getValue(User.class).getUsername());
                       // Toast.makeText(getActivity(),"Username Already Exists",Toast.LENGTH_LONG).show();
                        append=myRef.push().getKey().substring(3,6);
                        Log.d(TAG, "onDataChange: username already exist.Appending unique string to name"+append);
                    }
                }

                String mUserName="";

                mUserName=username+append;
                //ADD NEW USER TO THE DATABASE
                firebaseMethods.addNewUser(enrollment,mUserName,"","","");
                Toast.makeText(mContext,"SignUp successful. Sending Verification email.",Toast.LENGTH_LONG).show();
                mAuth.signOut();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth=FirebaseAuth.getInstance();
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        myRef=mFirebaseDatabase.getReference();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user= firebaseAuth.getCurrentUser();

                if (user!=null){
                    Log.d(TAG, "onAuthStateChanged: signedin:"+user.getUid());

                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //checks if the username is not already in use
                            /*if (firebaseMethods.checkIfUsernameExist(username,dataSnapshot)){
                                append=myRef.push().getKey().substring(3,6);
                                Log.d(TAG, "onDataChange: username already exist.Appending unique string to name"+append);
                            }

                            username=username+append;
                            //ADD NEW USER TO THE DATABASE
                            firebaseMethods.addNewUser(enrollment,username,"","","");
                            Toast.makeText(mContext,"SignUp successful. Sending Verification email.",Toast.LENGTH_LONG).show();
                            mAuth.signOut();*/
                            checkIfUsernameExists(username);


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    finish();

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

