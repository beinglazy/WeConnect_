package com.example.git_project.weconnect.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.git_project.weconnect.R;
import com.example.git_project.weconnect.Utils.FirebaseMethods;
import com.example.git_project.weconnect.Utils.UniversalImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Alpa on 27-03-2018.
 */

public class NextActivity extends AppCompatActivity {
    private static final String TAG = "NextActivity";

    private String mAppend="file:/";
    private static int imageCount =0;
    private String imgURL;
    private Bitmap bitmap;
    private Intent intent;

    //Widgets
    private EditText mCaption;


    //Firebase Instances
    private FirebaseMethods mFirebaseMethods;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        mFirebaseMethods=new FirebaseMethods(NextActivity.this);
        mCaption=(EditText)findViewById(R.id.caption);


        setupFirebaseAuth();

        ImageView backArrow=(ImageView)findViewById(R.id.ivBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing share activity.");
                finish();
            }
        });

        TextView share=(TextView)findViewById(R.id.tvShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to final share screen.");
                //uploading the image
                imageCount+=1;
                Toast.makeText(NextActivity.this,"Attempting to upload new Photo",Toast.LENGTH_SHORT).show();
                String caption = mCaption.getText().toString();

                if (intent.hasExtra(getString(R.string.selected_image))){
                    imgURL=intent.getStringExtra(getString(R.string.selected_image));
                    mFirebaseMethods.upLoadNewPhoto(getString(R.string.new_photo),caption, imageCount,imgURL,null);
                }else if (intent.hasExtra(getString(R.string.selected_bitmap))){
                    bitmap=(Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
                    mFirebaseMethods.upLoadNewPhoto(getString(R.string.new_photo),caption, imageCount,null,bitmap);
                }
            }
        });

        setImage();
    }
     private void someMethod(){
         //1.create a data model gor photos
         //2.Add property for Photo objects like captions,imgURL,date,photoid,tags,
         //3.counting the number of photos
         //4.upload the Photo to firebase & insert two nodes to firebase
         //insert into user node & user_photo node
     }
    //get image URL from incoming intent
    private void setImage(){
        intent=getIntent();
        ImageView image=(ImageView)findViewById(R.id.imageShare);
        if (intent.hasExtra(getString(R.string.selected_image))){
            imgURL=intent.getStringExtra(getString(R.string.selected_image));
            Log.d(TAG, "setImage: GOT NEW IMAGE URL"+imgURL);
            UniversalImageLoader.setImage(imgURL,image,null,mAppend);


        }else if (intent.hasExtra(getString(R.string.selected_bitmap))){
            bitmap=(Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
            Log.d(TAG, "setImage: got new BITMAP");
            image.setImageBitmap(bitmap);
        }

    }

    //*******************************************************SET THE FIREBASE AUTH OBJECTS*********************************************************************************
    //CHECKE if the user is logged in or not

 /*   private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in");
        if (user==null){
            Intent moveToLogin=new Intent(mContext, LoginActivity.class);
            startActivity(moveToLogin);
        }

    }*/



    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth= FirebaseAuth.getInstance();
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        myRef=mFirebaseDatabase.getReference();
        Log.d(TAG, "onDataChange: image count"+ imageCount);

        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user= firebaseAuth.getCurrentUser();
                /*//CHECKING FOR THE USER...
                checkCurrentUser(user);*/
                if (user!=null){
                    Log.d(TAG, "onAuthStateChanged: signedin:"+user.getUid());
                }else{
                    Log.d(TAG, "onAuthStateChanged: signed out");
                }
            }
        };

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                imageCount =mFirebaseMethods.getImageCount(dataSnapshot);
                Log.d(TAG, "onDataChange: image count"+ imageCount);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        //checkCurrentUser(mAuth.getCurrentUser());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener!=null){
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
