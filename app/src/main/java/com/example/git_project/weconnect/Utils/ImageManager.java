package com.example.git_project.weconnect.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Alpa on 27-03-2018.
 */

public class ImageManager {
    private static final String TAG = "ImageManager";

    public static Bitmap getBitmap(String imgURl){
        File imageFile=new File(imgURl);
        FileInputStream fis=null;
        Bitmap bitmap=null;
        try {
            fis=new FileInputStream(imageFile);
            bitmap= BitmapFactory.decodeStream(fis);


        }catch (FileNotFoundException e){
            Log.e(TAG, "getBitmap: FileNotFoundException"+ e.getMessage());
        }finally {
            try {
                fis.close();

            }catch (IOException e){
                Log.e(TAG, "getBitmap: FileNotFoundException"+ e.getMessage());
            }
        }
        return bitmap;
    }

    //return byte array from a bitmap
    //Quality is greater than 0 but less than 100

    public static byte[] getBytesFromBitmap(Bitmap bm,int quality){

        ByteArrayOutputStream stream=new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,quality,stream);
        return stream.toByteArray();

    }
}
