package com.example.git_project.weconnect.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.git_project.weconnect.Login.LoginActivity;
import com.example.git_project.weconnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Alpa on 22-03-2018.
 */

public class SignOutFragment extends Fragment {
    private static final String TAG = "SignOutFragment";
    //FireBase STuff
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private ProgressBar mProgressBar;
    private TextView tvSigOut,signingout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_signout,container,false);
        tvSigOut=(TextView)view.findViewById(R.id.tvConfirmSignout);
        mProgressBar=(ProgressBar)view.findViewById(R.id.signoutProgressBar);
        signingout=(TextView)view.findViewById(R.id.tvsigningout);
        Button btn=(Button)view.findViewById(R.id.btnConfirmSignout);
        mProgressBar.setVisibility(View.GONE);
        signingout.setVisibility(View.GONE);

        setupFirebaseAuth();



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Attempting to signout");
                mProgressBar.setVisibility(View.VISIBLE);
                signingout.setVisibility(View.VISIBLE);

                mAuth.signOut();
                getActivity().finish();
            }
        });
        return view;

    }
    //*******************************************************SET THE FIREBASE AUTH OBJECTS*********************************************************************************
    //CHECKE if the user is logged in or not

    /*private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in");
        if (user==null){
            Intent moveToLogin=new Intent(mContext, LoginActivity.class);
            startActivity(moveToLogin);
        }

    }*/



    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth=FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user= firebaseAuth.getCurrentUser();
                //CHECKING FOR THE USER...
               // checkCurrentUser(user);
                if (user!=null){
                    Log.d(TAG, "onAuthStateChanged: signedin:"+user.getUid());
                }else{
                    Log.d(TAG, "onAuthStateChanged: signed out");

                    Log.d(TAG, "onAuthStateChanged: Back to login activity");
                    Intent backToLogin=new Intent(getActivity(),LoginActivity.class);
                    backToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(backToLogin);
                }
            }
        };

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

       // checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
