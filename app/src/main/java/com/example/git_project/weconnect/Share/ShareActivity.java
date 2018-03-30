package com.example.git_project.weconnect.Share;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.git_project.weconnect.R;
import com.example.git_project.weconnect.Utils.BottomNavigationViewHelper;
import com.example.git_project.weconnect.Utils.Permissions;
import com.example.git_project.weconnect.Utils.SectionStatePagerAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

/**
 * Created by Alpa on 21-03-2018.
 */

public class ShareActivity extends AppCompatActivity {
    private static final String TAG = "ShareActivity";
    private static final int ACTIVITY_NUM = 2;
    private static final int VERIFY_PERMISSIONS_REQUEST=1;
    private ViewPager mViewPager;

    private Context mContext = ShareActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Log.d(TAG, "onCreate: started");
        if (checkPermissionArray(Permissions.PERMISSIONS)){
            setupViewPager();

        }else {
            verifyPermissions(Permissions.PERMISSIONS);

        }



        //setupBottomNavigationView();

    }
// Return Current Tab number
    //0=GalleryFragment
    //1=CameraFragment

    public int getCurrentTabNumber(){
        return mViewPager.getCurrentItem();





    }



    private void setupViewPager(){
        SectionStatePagerAdapter adapter =new SectionStatePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new GalleryFragment(),"GalleryFragment");
        adapter.addFragment(new PhotoFragment(),"PhotoFragment");
        mViewPager=(ViewPager)findViewById(R.id.container);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout=(TabLayout)findViewById(R.id.tabsBottom);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setText(getString(R.string.gallery));
        tabLayout.getTabAt(1).setText(getString(R.string.photo));
    }
    public int getTask(){
        ///Log.d(TAG, "getTask:Task" +getIntent().getFlags());
        return getIntent().getFlags();
    }

    public void verifyPermissions(String[] permissions){
        Log.d(TAG, "verifyPermissions:  verifying permission");
        ActivityCompat.requestPermissions(ShareActivity.this,permissions,VERIFY_PERMISSIONS_REQUEST);
    }



    public boolean checkPermissionArray(String[] permissions){
        Log.d(TAG, "checkPermissionArray: Checking Permission Array");
        for(int i=0;i<permissions.length;i++){
            String check=permissions[i];
            if (!checkPermissions(check)){
                return false;
            }
        }
        return true;
    }
    public boolean checkPermissions(String permission){
        Log.d(TAG, "checkPermissions: checking permission"+permission);
        int permissionRequest= ActivityCompat.checkSelfPermission(ShareActivity.this,permission);
        if (permissionRequest!= PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "checkPermissions: permission was not granted for"+ permission);
            return false;
        }else {
            Log.d(TAG, "checkPermissions: Permission Granted");
            return true;
        }
    }
    //BottoomNavigation thing
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx)findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext,this,bottomNavigationViewEx);
        Menu menu =bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);


    }
}
