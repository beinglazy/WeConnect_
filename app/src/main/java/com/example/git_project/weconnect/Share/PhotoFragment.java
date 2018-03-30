package com.example.git_project.weconnect.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.git_project.weconnect.Profile.AccountSettingsActivity;
import com.example.git_project.weconnect.R;
import com.example.git_project.weconnect.Utils.Permissions;

/**
 * Created by Alpa on 21-03-2018.
 */

public class PhotoFragment extends Fragment {
    private static final String TAG = "PhotoFragment";
    Button btn;

    private static final int PHOTO_FRAGMENT_NUMBER=1;
    private static final int GALLERY_FRAGMENT_NUMBER=1;
    private static final int CAMERA_REQUEST_CODE=5;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_photo,container,false);
        Log.d(TAG, "onCreateView: Started");
        btn=(Button)view.findViewById(R.id.btnLaunchCamera);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Launch Camera");
                if (((ShareActivity)getActivity()).getCurrentTabNumber()==PHOTO_FRAGMENT_NUMBER){
                    if (((ShareActivity)getActivity()).checkPermissions(Permissions.CAMERA_PERMISSIONS[0])){
                        Log.d(TAG, "onClick: starting camera");
                        Intent cameraIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent,CAMERA_REQUEST_CODE);
                    }else {
                        Intent intent=new Intent(getActivity(),ShareActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }

            }
        });




        return view;

    }

    private boolean isRootTask(){
        if (((ShareActivity)getActivity()).getTask()==0){
            return true;
        }
        else{
            return false;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==CAMERA_REQUEST_CODE){
            Log.d(TAG, "onActivityResult: Photo taken");
            Log.d(TAG, "onActivityResult: navigating to share screen");
            //navigating to publish Photo
            Bitmap bitmap;
            bitmap = (Bitmap) data.getExtras().get("data");
            if (isRootTask()){
                try {
                    Log.d(TAG, "onActivityResult: recievd new bitmap from camera"+bitmap);
                    Intent intent=new Intent(getActivity(),NextActivity.class);
                    intent.putExtra(getString(R.string.selected_bitmap),bitmap);
                   // intent.putExtra(getString(R.string.return_to_fragment),getString(R.string.edit_profile_fragment));
                    startActivity(intent);
                    //getActivity().finish();

                }catch (NullPointerException e){
                    Log.d(TAG, "onActivityResult: NullPonterException" +e.getMessage());

                }



            }else{
                try {
                    Log.d(TAG, "onActivityResult: recievd new bitmap from camera"+bitmap);
                    Intent intent=new Intent(getActivity(),AccountSettingsActivity.class);
                    intent.putExtra(getString(R.string.selected_bitmap),bitmap);
                    intent.putExtra(getString(R.string.return_to_fragment),getString(R.string.edit_profile_fragment));
                    startActivity(intent);
                    getActivity().finish();

                }catch (NullPointerException e){
                    Log.d(TAG, "onActivityResult: NullPonterException" +e.getMessage());

                }


            }


        }
    }
}
