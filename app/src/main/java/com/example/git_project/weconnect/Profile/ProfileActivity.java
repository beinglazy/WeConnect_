package com.example.git_project.weconnect.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toolbar;

import com.example.git_project.weconnect.R;
import com.example.git_project.weconnect.Utils.BottomNavigationViewHelper;
import com.example.git_project.weconnect.Utils.GridImageAdapter;
import com.example.git_project.weconnect.Utils.UniversalImageLoader;
import com.example.git_project.weconnect.ViewPostFragment;
import com.example.git_project.weconnect.models.Photo;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

/**
 * Created by Alpa on 21-03-2018.
 */

public class ProfileActivity extends AppCompatActivity implements ProfileFragment.OnGridImageSelectedListener {
    private static final String TAG = "ProfileActivity";



    @Override
    public void onGridImageSelected(Photo photo, int activityNumber) {
        Log.d(TAG, "onGridImageSelected: selected an image from gridview:"+photo.toString());
        ViewPostFragment fragment=new ViewPostFragment();
        Bundle args=new Bundle();
        args.putParcelable(getString(R.string.photo),photo);
        args.putInt(getString(R.string.activity_num),activityNumber);
        fragment.setArguments(args);
        FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container,fragment);
        transaction.addToBackStack(getString(R.string.view_post_fragment));
        transaction.commit();
    }
    private static final int ACTIVITY_NUM = 4;
    private static final int NUM_GRID_COLUMNS = 3;
    private Context mContext = ProfileActivity.this;
    private ProgressBar mProgressbar;
    private ImageView profilePhoto;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: started");
        init();

        /*setupBottomNavigationView();
        setupToolbar();
        setupActivityWidget();
        setProfileImage();*/
        //tempGridSetup();

    }
    private void init(){
        Log.d(TAG, "init: inflating"+getString(R.string.profile_fragment));
        ProfileFragment fragment=new ProfileFragment();
        FragmentTransaction transaction=ProfileActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container,fragment);
        transaction.addToBackStack(getString(R.string.profile_fragment));
        transaction.commit();
    }


   /* private void tempGridSetup(){
        ArrayList<String> imgURLs = new ArrayList<>();
        imgURLs.add("https://i.redd.it/dllrn39flfn01.jpg");
        imgURLs.add("https://i.redd.it/mdsfl2asedn01.jpg");
        imgURLs.add("https://i.imgur.com/Ykcuusc.jpg");
        imgURLs.add("https://i.imgur.com/TFPOeZ5.png");
        imgURLs.add("http://animals.sandiegozoo.org/sites/default/files/2017-03/animals_hero_bee-eaters.jpg");
        imgURLs.add("https://static1.squarespace.com/static/55259409e4b02b9e39c3a299/58333b4a6a496317255c5a3d/58d41213be65943deef7e70e/1490293363806/Darwin.JPG?format=750w");
        imgURLs.add("http://www.dw.com/image/18586191_303.jpg");
        imgURLs.add("https://nation.com.pk/digital_images/large/2016-07-15/feature-saving-rare-birds-of-pakistan-1468584242-6803.jpg");
        imgURLs.add("https://is4-ssl.mzstatic.com/image/thumb/Purple71/v4/a5/4b/d4/a54bd420-2ab4-c5e0-a1c6-3f40b604d727/mzl.ahotldnn.png/1200x630bb.jpg");
        imgURLs.add("https://lanka.com/wp-content/uploads/2014/12/slagala-bird-sanctuaries.jpg");
        imgURLs.add("https://download.ams.birds.cornell.edu/api/v1/asset/28488511/large");
        setupImageGrid(imgURLs);




    }
    private void setupImageGrid(ArrayList<String> imgURLs ){
        GridView gridView = (GridView) findViewById(R.id.gridView);
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);
        GridImageAdapter adapter=new GridImageAdapter(mContext,R.layout.layout_grid_imageview,"",imgURLs);
        gridView.setAdapter(adapter);
    }
    private void setProfileImage(){
        Log.d(TAG, "setProfileImage: setting prole Photo");
        String imgURL ="www.androidcentral.com/sites/androidcentral.com/files/styles/xlarge/public/article_images/2016/08/ac-lloyd.jpg?itok=bb72Ielf";
        UniversalImageLoader.setImage(imgURL,profilePhoto,mProgressbar,"http://");

    }
    private void setupActivityWidget(){
        mProgressbar=(ProgressBar)findViewById(R.id.profileProgrssBar);
        mProgressbar.setVisibility(View.GONE);
        profilePhoto=  (ImageView)findViewById(R.id.profile_photo);

    }

    private void setupToolbar(){
        android.support.v7.widget.Toolbar toolbar =(android.support.v7.widget.Toolbar)findViewById(R.id.profileToolBar);
        setSupportActionBar(toolbar);
        ImageView profileMenu = (ImageView)findViewById(R.id.profileMenu);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to account settings");
                Intent moveToSetting = new Intent(mContext,AccountSettingsActivity.class);
                startActivity(moveToSetting);
            }
        });
    }

    //BottoomNavigation thing
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx)findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext,bottomNavigationViewEx);
        Menu menu =bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);


    }

*/

}
