package com.example.git_project.weconnect.Utils;

import android.os.Environment;

/**
 * Created by Alpa on 27-03-2018.
 */

public class FilePaths {
    public String ROOT_DIR= Environment.getExternalStorageDirectory().getPath();
    public String PICTURES=ROOT_DIR+"/Pictures";
    public String CAMERA=ROOT_DIR +"/DCIM/camera";

    public String FIREBASE_IMAGE_STORAGE="photos/users/";
}
