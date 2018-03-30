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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.git_project.weconnect.R;
import com.example.git_project.weconnect.Share.ShareActivity;
import com.example.git_project.weconnect.Utils.FirebaseMethods;
import com.example.git_project.weconnect.Utils.UniversalImageLoader;
import com.example.git_project.weconnect.models.User;
import com.example.git_project.weconnect.models.UserAccountSettings;
import com.example.git_project.weconnect.models.UserSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Alpa on 22-03-2018.
 */

public class EditProfileFragment extends Fragment {
    private static final String TAG = "EditProfileFragment";
    //EDIT PROFILE FRAGMENT
    private CircleImageView mProfilePhoto ;
    private EditText mDisplayName,mBranch,mSemester,mUsername;
    private TextView mChangePhoto;
    private ImageView saveChanges;
    private String userID;
    private UserSettings mUserSettings;

    //Firebase Instances
    private FirebaseMethods mFirebaseMethods;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_editprofile,container,false);
        mProfilePhoto=(CircleImageView) view.findViewById(R.id.profile_photo);
        mDisplayName=(EditText)view.findViewById(R.id.username);
        mUsername=(EditText)view.findViewById(R.id.ETNAME);
        mBranch=(EditText)view.findViewById(R.id.branch);
        mSemester=(EditText)view.findViewById(R.id.semester);
        mChangePhoto=(TextView)view.findViewById(R.id.changeProfilePhoto);
        mFirebaseMethods=new FirebaseMethods(getActivity());
        saveChanges=(ImageView)view.findViewById(R.id.saveChanges);
        setupFirebaseAuth();

        //setProfileImage();

        //BACK button setup
        ImageView backArrow=(ImageView)view.findViewById(R.id.backArrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                getActivity().finish();
            }
        });
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: saving changes");
                saveProfileSettings();

            }
        });


        return view;

    }



   /* private void setProfileImage(){
        Log.d(TAG, "setProfileImage: setting profile image");
        String imgURL ="www.androidcentral.com/sites/androidcentral.com/files/styles/xlarge/public/article_images/2016/08/ac-lloyd.jpg?itok=bb72Ielf";
        UniversalImageLoader.setImage(imgURL,mProfilePhoto,null,"http://");

    }*/

   private void saveProfileSettings(){
       final String displayName=mDisplayName.getText().toString();
       final String username=mUsername.getText().toString();
       final String branch=mBranch.getText().toString();
       final String semester=mSemester.getText().toString();
       myRef.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {

               /*User user=new User();
               for (DataSnapshot ds: dataSnapshot.child(getString(R.string.dbname_users)).getChildren()){
                   if (ds.getKey().equals(userID)){
                       user.setUsername(ds.getValue(User.class).getUsername());

                   }
               }
               Log.d(TAG, "onDataChange: Current Username"+user.getUsername());*/
               //case1:If user doesnot change username
               if (!mUserSettings.getmUser().getUsername().equals(username)){
                   checkIfUsernameExists(username);


               }

               if (!mUserSettings.getmUserAccountSettings().getDisplay_name().equals(displayName));{
                   //update display name
                   mFirebaseMethods.updateUserAccountSettings(displayName,null,null);
               }
               if (!mUserSettings.getmUserAccountSettings().getBranch().equals(branch));{
                   //update display branch
                   mFirebaseMethods.updateUserAccountSettings(null,branch,null);

               }
               if (!mUserSettings.getmUserAccountSettings().getSemester().equals(semester));{
                   //update display semester
                   mFirebaseMethods.updateUserAccountSettings(null,null,semester);

               }




           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });

   }

   //checks if the username is already exists
    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: checking if"+username+"already Exists");
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        Query query=reference.child(getString(R.string.dbname_users)).orderByChild(getString(R.string.field_username)).equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    //add the username
                    mFirebaseMethods.updateUsername(username);
                    Toast.makeText(getActivity(),"Username Saved",Toast.LENGTH_LONG).show();
                }
                for (DataSnapshot singleSnapshot:dataSnapshot.getChildren()){
                    if (singleSnapshot.exists()){
                        Log.d(TAG, "checkIfUsernameExists: Found a match "+singleSnapshot.getValue(User.class).getUsername());
                        Toast.makeText(getActivity(),"Username Already Exists",Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void setProfileWidgets(UserSettings userSettings){
        Log.d(TAG, "setProfileWidgets: setting widgets with data from firebase"+userSettings.toString());


        mUserSettings=userSettings;



        User user =userSettings.getmUser();
        UserAccountSettings settings=userSettings.getmUserAccountSettings();
        UniversalImageLoader.setImage(settings.getProfile_photo(),mProfilePhoto,null,"");
        mUsername.setText(settings.getUsername());
        mDisplayName.setText(settings.getDisplay_name());
        mBranch.setText(settings.getBranch());
        mSemester.setText(settings.getSemester());

        mChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Changing profile photo-");
                Intent intent=new Intent(getActivity(), ShareActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//268435456
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });

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
        mFirebaseDatabase= FirebaseDatabase.getInstance();
        myRef=mFirebaseDatabase.getReference();
        userID=mAuth.getCurrentUser().getUid();
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
                //retrieve user informathion from the database
                 setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));



                //retrieve images for the users
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
