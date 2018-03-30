package com.example.git_project.weconnect.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.git_project.weconnect.Login.LoginActivity;
import com.example.git_project.weconnect.R;
import com.example.git_project.weconnect.Utils.BottomNavigationViewHelper;
import com.example.git_project.weconnect.Utils.FirebaseMethods;
import com.example.git_project.weconnect.Utils.GridImageAdapter;
import com.example.git_project.weconnect.Utils.UniversalImageLoader;
import com.example.git_project.weconnect.models.Photo;
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
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.PriorityQueue;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Alpa on 25-03-2018.
 */

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    public interface OnGridImageSelectedListener{
        void onGridImageSelected(Photo photo,int activityNumber);
    }
    OnGridImageSelectedListener mOnGridImageSelectedListener;


    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_GRID_COLUMNS = 3;




    private TextView mPosts,mFollowers,mFollowing,mDisplayName,mUserName,mBranch,mSemester;
    private ProgressBar  mProgressBar;
    private CircleImageView mProfilePhoto;
    private GridView mGridView;
    private Toolbar mToolBar;
    private ImageView profileMenu;
    private BottomNavigationViewEx bottomNavigationView;
    private Context mContext;

    //Firebase Instances
    private FirebaseMethods mFirebaseMethods;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_profile,container,false);
        mDisplayName=(TextView)view.findViewById(R.id.display_name);
        mUserName=(TextView)view.findViewById(R.id.profileName);
        mPosts=(TextView)view.findViewById(R.id.tvPosts);
        mFollowers=(TextView)view.findViewById(R.id.tvfollwers);
        mFollowing=(TextView)view.findViewById(R.id.tvfollowing);
        mBranch=(TextView)view.findViewById(R.id.display_branch);
        mSemester=(TextView)view.findViewById(R.id.display_semester);
        mProfilePhoto=(CircleImageView)view.findViewById(R.id.profile_photo);
        mProgressBar=(ProgressBar)view.findViewById(R.id.profileProgrssBar);
        mGridView=(GridView)view.findViewById(R.id.mgridView);
        mToolBar=(Toolbar)view.findViewById(R.id.profileToolBar);
        profileMenu=(ImageView)view.findViewById(R.id.profileMenu);
        bottomNavigationView=(BottomNavigationViewEx)view.findViewById(R.id.bottomNavViewBar);
        mContext=getActivity();
        mFirebaseMethods=new FirebaseMethods(getActivity());
        Log.d(TAG, "onCreateView: starting");

        setupToolbar();
        setupBottomNavigationView();
        setupFirebaseAuth();
        setupGridView();
        TextView editProfile=(TextView)view.findViewById(R.id.textEditProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to editprofile");
                Intent moveToEditSettings = new Intent(getActivity(),AccountSettingsActivity.class);
                moveToEditSettings.putExtra(getString(R.string.calling_activty),getString(R.string.profile_activity));
                startActivity(moveToEditSettings);
                getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });



        return view;

    }

    @Override
    public void onAttach(Context context) {
        try {
            mOnGridImageSelectedListener =(OnGridImageSelectedListener)getActivity();

        }catch (ClassCastException e){
            Log.e(TAG, "onAttach: ClassCastException"+e.getMessage());
        }
        super.onAttach(context);
    }
    private void setupGridView(){
        Log.d(TAG, "setupGridView: setting up the image GRID");
        final ArrayList<Photo> photos=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
        Query query=reference.child(getString(R.string.dbname_user_photos)).child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot:dataSnapshot.getChildren()){
                    photos.add(singleSnapshot.getValue(Photo.class));

                }
                //setup our image GRID
                int gridWidth=getResources().getDisplayMetrics().widthPixels;
                int imageWidth=gridWidth/NUM_GRID_COLUMNS;
                mGridView.setColumnWidth(imageWidth);

                ArrayList<String> imgUrls=new ArrayList<String>();
                for (int i=0;i<photos.size(); i++){
                    imgUrls.add(photos.get(i).getImg_path());

                }
                GridImageAdapterProfile adapter=new GridImageAdapterProfile(getActivity(),R.layout.layout_center_profile,"",imgUrls);
                mGridView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled");

            }
        });
    }

    private void setProfileWidgets(UserSettings userSettings){
        Log.d(TAG, "setProfileWidgets: setting widgets with data from firebase"+userSettings.toString());
        User user =userSettings.getmUser();
        UserAccountSettings settings=userSettings.getmUserAccountSettings();
        UniversalImageLoader.setImage(settings.getProfile_photo(),mProfilePhoto,null,"");
        mDisplayName.setText(settings.getDisplay_name());
        mUserName.setText(settings.getUsername());
        mBranch.setText(settings.getBranch());
        mSemester.setText(settings.getSemester());
        mPosts.setText(String.valueOf(settings.getPosts()));
        mFollowers.setText(String.valueOf(settings.getFollowers()));
        mFollowing.setText(String.valueOf(settings.getFollowing()));
        mProgressBar.setVisibility(View.GONE);
    }

    private void setupToolbar(){

        ((ProfileActivity)getActivity()).setSupportActionBar(mToolBar);


        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to account settings");
                Intent moveToSetting = new Intent(mContext,AccountSettingsActivity.class);
                startActivity(moveToSetting);
                //getActivity().overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
            }
        });
    }


    //BottoomNavigation thing
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");

        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(mContext,getActivity(),bottomNavigationView);
        Menu menu =bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);


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
