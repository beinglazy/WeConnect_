package com.example.git_project.weconnect.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.util.IndianCalendar;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.git_project.weconnect.Home.HomeActivity;
import com.example.git_project.weconnect.Login.RegisterActivity;
import com.example.git_project.weconnect.Profile.AccountSettingsActivity;
import com.example.git_project.weconnect.R;
import com.example.git_project.weconnect.models.Photo;
import com.example.git_project.weconnect.models.User;
import com.example.git_project.weconnect.models.UserAccountSettings;
import com.example.git_project.weconnect.models.UserSettings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Alpa on 24-03-2018.
 */

public class FirebaseMethods {
    private static final String TAG = "FirebaseMethods";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private StorageReference mStorageReference;
    private DatabaseReference myRef;
    private String userID;

    //vars
    private Context mContext;
    private double mPhotoUploadProgress=0;
    int count=0;

    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase=FirebaseDatabase.getInstance();
        myRef=mFirebaseDatabase.getReference();
        mStorageReference= FirebaseStorage.getInstance().getReference();

        mContext = context;
        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }
    }


    public  void upLoadNewPhoto(String photoType, final String caption, int count, final String imgURL,Bitmap bm){
        Log.d(TAG, "upLoadNewPhoto: attempting to upload new Photo");

        FilePaths filePaths=new FilePaths();

        //case 1 : Uploading a new Image
        if (photoType.equals(mContext.getString(R.string.new_photo))){
            Log.d(TAG, "upLoadNewPhoto: Uploading new photo");
            String user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference= mStorageReference.child(filePaths.FIREBASE_IMAGE_STORAGE+"/"+user_id+"/photo"+(count+=1));

            //convert image url to bitmap

            if (bm==null){
                bm=ImageManager.getBitmap(imgURL);
            }

            byte[] bytes=ImageManager.getBytesFromBitmap(bm,100);


            UploadTask uploadTask=null;
            uploadTask=storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri firebaseUrl= taskSnapshot.getDownloadUrl();
                    Toast.makeText(mContext,"photo uploded successfully",Toast.LENGTH_SHORT).show();

                    //add new photo to 'photos' node and 'user_photos' node
                    addPhotoToDatabase(caption,firebaseUrl.toString());

                    //navigate to main feed
                    Intent intent=new Intent(mContext, HomeActivity.class);
                    mContext.startActivity(intent);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext,"Photo upload failed.",Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    if (progress-15 > mPhotoUploadProgress){
                        Toast.makeText(mContext,"photo upload progress:"+String.format("%.0f",progress)+"%",Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress=progress;

                    }
                    Log.d(TAG, "onProgress: upload progress"+progress+"% done");
                }
            });
            

        }
        //case 2: Uploading Profile Photo
        else if (photoType.equals(mContext.getString(R.string.profile_photo))){
            Log.d(TAG, "upLoadNewPhoto: Uploading Profile Photo");


            String user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference= mStorageReference.child(filePaths.FIREBASE_IMAGE_STORAGE+"/"+user_id+"/profile_photo");

            //convert image url to bitmap

            if (bm==null){
                bm=ImageManager.getBitmap(imgURL);
            }
            byte[] bytes=ImageManager.getBytesFromBitmap(bm,100);


            UploadTask uploadTask=null;
            uploadTask=storageReference.putBytes(bytes);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri firebaseUrl= taskSnapshot.getDownloadUrl();
                    Toast.makeText(mContext,"photo uploded successfully",Toast.LENGTH_SHORT).show();

                    //insert into user_account_setting node
                    setProfilePhoto(firebaseUrl.toString());
                    ((AccountSettingsActivity)mContext).setViewPager(
                            ((AccountSettingsActivity)mContext).pagerAdapter.getFragmentNumber(mContext.getString(R.string.edit_profile_fragment))
                    );



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext,"Photo upload failed.",Toast.LENGTH_SHORT).show();

                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress=(100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    if (progress-15 > mPhotoUploadProgress){
                        Toast.makeText(mContext,"photo upload progress:"+String.format("%.0f",progress)+"%",Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress=progress;

                    }
                    Log.d(TAG, "onProgress: upload progress"+progress+"% done");
                }
            });

        }
    }

    private void setProfilePhoto(String url){
        Log.d(TAG, "setProfilePhoto: setting new Profile photo"+url);
        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.profile_photo)).setValue(url);


    }

    private String getTimestamp(){
        SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        return sdf.format(new Date());

    }





    private void addPhotoToDatabase(String caption,String url){
        Log.d(TAG, "addPhotoToDatabase: Adding photos to the database");
        String tags=StringManipulation.getTags(caption);
        String newPhotoKey=myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();
        Photo photo=new Photo();
        photo.setCaption(caption);
        photo.setDate_created(getTimestamp());
        photo.setImg_path(url);
        photo.setTags(tags);
        photo.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
        photo.setPhoto_id(newPhotoKey);

        //insert into database
        myRef.child(mContext.getString(R.string.dbname_user_account_settings)).child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid())
                .child(newPhotoKey).setValue(photo);
        myRef.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid())
                .child(newPhotoKey).setValue(photo);
        myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).setValue(photo);


    }

    public int getImageCount(DataSnapshot dataSnapshot)
    {
        count+=1 ;

        for (DataSnapshot ds:dataSnapshot.child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getChildren()){
            count++;
        }
        return count;
    }

    public void updateUserAccountSettings(String displayName,String branch,String semester){
        Log.d(TAG, "updateUserAccountSettings: Updating UserAccount Settings");
        if (displayName!=null){
            myRef.child(mContext.getString(R.string.dbname_user_account_settings)).child(userID).child(mContext.getString(R.string.field_name)).setValue(displayName);
        }
        if (branch!=null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings)).child(userID).child(mContext.getString(R.string.field_branch)).setValue(branch);
        }
        if (semester!=null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings)).child(userID).child(mContext.getString(R.string.field_semester)).setValue(semester);
        }

    }

    public void updateUsername(String username){
        Log.d(TAG, "updateUsername: updating username");
        myRef.child(mContext.getString(R.string.dbname_users)).child(userID).child(mContext.getString(R.string.field_username)).setValue(username);
        myRef.child(mContext.getString(R.string.dbname_user_account_settings)).child(userID).child(mContext.getString(R.string.field_username)).setValue(username);

    }

    /*public boolean checkIfUsernameExist(String username, DataSnapshot dataSnapshot){
        Log.d(TAG, "checkIfUsernameExist: checking if"+username+"already exist");
        User user = new User();
        for (DataSnapshot ds: dataSnapshot.child(userID).getChildren()){
            Log.d(TAG, "checkIfUsernameExist: datasnapshot"+ds);
            user.setUsername(ds.getValue(User.class).getUsername());
            if (StringManipulation.expandUsername(user.getUsername()).equals(username)){
                Log.d(TAG, "checkIfUsernameExist: FOUND A MATCH"+user.getUsername());
                return true;

            }
        }
        return false;
    }*/


    public void registerNewEmail(final String enrollment, String password, final String username) {
        mAuth.createUserWithEmailAndPassword(enrollment, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "onComplete: " + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.auth_failed, Toast.LENGTH_LONG).show();

                        } else if (task.isSuccessful()) {
                            //send verification email
                            sendVerificationEmail();
                            userID = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: Authstate changed " + userID);
                            Toast.makeText(mContext, R.string.auth_success, Toast.LENGTH_LONG).show();


                        }

                    }
                });
    }
    //For sending Verification code
    public void sendVerificationEmail(){
        FirebaseUser user =FirebaseAuth.getInstance().getCurrentUser();
        if (user!=null){
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                    } else {
                        Toast.makeText(mContext, "couldn't send verificationemail.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void addNewUser(String enrollment,String username,String branch,String semester,String profile_photo){
        User user=new User(userID,StringManipulation.condenseUsername(username));
        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID).setValue(user);

        UserAccountSettings settings=new UserAccountSettings(branch,username,0,0,0,profile_photo,semester,StringManipulation.condenseUsername(username));
        myRef.child(mContext.getString((R.string.dbname_user_account_settings))).child(userID).setValue(settings);

    }
    //IT SIMPLY RETRIEVE THE ACCOUNT SETTINGS FOR THE USER CURRENTLY LOGGED IN
    public UserSettings getUserSettings(DataSnapshot dataSnapshot) {
        Log.d(TAG, "getUserAccountSettings: retrieving user account setting from FIREBASE");
        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();
        for (DataSnapshot ds : dataSnapshot.getChildren()) {
            if (ds.getKey().equals(mContext.getString(R.string.dbname_user_account_settings))) {
                Log.d(TAG, "getUserAccountSettings: datasnapshot" + ds);

                try {


                    settings.setDisplay_name(ds.child(userID).getValue(UserAccountSettings.class).getDisplay_name());
                    settings.setUsername(ds.child(userID).getValue(UserAccountSettings.class).getUsername());
                    settings.setBranch(ds.child(userID).getValue(UserAccountSettings.class).getBranch());
                    settings.setSemester(ds.child(userID).getValue(UserAccountSettings.class).getSemester());
                    settings.setProfile_photo(ds.child(userID).getValue(UserAccountSettings.class).getProfile_photo());
                    settings.setPosts(ds.child(userID).getValue(UserAccountSettings.class).getPosts());
                    settings.setFollowers(ds.child(userID).getValue(UserAccountSettings.class).getFollowers());
                    settings.setFollowing(ds.child(userID).getValue(UserAccountSettings.class).getFollowing());
                    Log.d(TAG, "getUserAccountSettings:retrieved all user account setting data " + settings.toString());
                } catch (NullPointerException e) {
                    Log.e(TAG, "getUserAccountSettings:NullPointerException " + e.getMessage());

                }


            }
            if (ds.getKey().equals(mContext.getString(R.string.dbname_users))) {
                Log.d(TAG, "getUserAccountSettings: datasnapshot" + ds);

                user.setUsername(ds.child(userID).getValue(User.class).getUsername());
                user.setUser_id(ds.child(userID).getValue(User.class).getUser_id());
                Log.d(TAG, "getUserAccountSettings:retrieved all user data " + user.toString());
            }


        }
        return new UserSettings(user,settings);
    }


}
