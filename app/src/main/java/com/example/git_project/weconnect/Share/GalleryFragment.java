package com.example.git_project.weconnect.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.git_project.weconnect.Profile.AccountSettingsActivity;
import com.example.git_project.weconnect.R;
import com.example.git_project.weconnect.Utils.FilePaths;
import com.example.git_project.weconnect.Utils.FileSearch;
import com.example.git_project.weconnect.Utils.GridImageAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by Alpa on 21-03-2018.
 */

public class GalleryFragment extends Fragment {
    private static final String TAG = "GalleryFragment";
    private static final int NUM_GRID_COLUMNS = 3;
    private String mAppend="file:/";
    private String mSelectedImage;


    private GridView gridView;
    private ImageView galleryImage,shareClose;
    private ProgressBar mProgressBar;
    private Spinner directorySpinner;
    // vars
    private ArrayList<String> directories;
    ImageLoader imageLoader;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_gallery,container,false);

        galleryImage=(ImageView)view.findViewById(R.id.galleryImageView);
        gridView=(GridView)view.findViewById(R.id.gridView);
        directorySpinner=(Spinner)view.findViewById(R.id.spinnerDirectory);
        mProgressBar=(ProgressBar)view.findViewById(R.id.progrssBar);
        mProgressBar.setVisibility(view.GONE);
        directories=new ArrayList<>();

       // imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        imageLoader= ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
        Log.d(TAG, "onCreateView: Started");
        shareClose=(ImageView)view.findViewById(R.id.ivCLoseShare);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing gallery fragment.");
                getActivity().finish();
            }
        });

        TextView nextScreen=(TextView)view.findViewById(R.id.tvNext);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to final share screen.");
                if (isRootTask()){
                    Intent intent=new Intent(getActivity(),NextActivity.class);
                    intent.putExtra(getString(R.string.selected_image),mSelectedImage);
                    startActivity(intent);
                }else{
                    Intent intent=new Intent(getActivity(),AccountSettingsActivity.class);
                    intent.putExtra(getString(R.string.selected_image),mSelectedImage);
                    intent.putExtra(getString(R.string.return_to_fragment),getString(R.string.edit_profile_fragment));
                    startActivity(intent);
                    getActivity().finish();
                }



            }
        });

        init();


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

    private void init(){
        FilePaths filePaths=new FilePaths();

        //check for othe =r directory
        if (FileSearch.getDirectoryPaths(filePaths.PICTURES)!=null){
            directories=FileSearch.getDirectoryPaths(filePaths.PICTURES);

        }
       /* ArrayList<String> directoryNames=new ArrayList<>();
        for (int i=0;i<directories.size();i++){

            int index=directories.get(i).lastIndexOf("/");
            String string=directories.get(i).substring(index).replace("/","");
            directoryNames.add(string);
        }*/

        directories.add(filePaths.CAMERA);

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,directories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);
        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               // Log.d(TAG, "onItemSelected: "+directories.get(position));
                //setup for Imamge for GridView
                setupGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setupGridView(String selectedDirectory){
        Log.d(TAG, "setupGridView:showing in the grid "+selectedDirectory);
        final ArrayList<String> imgURLs=FileSearch.getFilePaths(selectedDirectory);

        //set the grid column width
        int gridWidth=getResources().getDisplayMetrics().widthPixels;
        int imgWidth=gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imgWidth);
        //use the grid adapter to adapter the image to gridview
        GridImageAdapter adapter=new GridImageAdapter(getActivity(),R.layout.layout_grid_imageview,mAppend,imgURLs);
        gridView.setAdapter(adapter);

        //setting first image to be displayed
        setImage(imgURLs.get(0),galleryImage,mAppend);
        mSelectedImage=imgURLs.get(0);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected an Image"+imgURLs.get(position));
                setImage(imgURLs.get(position),galleryImage,mAppend);
               mSelectedImage=imgURLs.get(position);

            }
        });


    }
    private void setImage(String imgURL,ImageView image,String append){
        Log.d(TAG, "setImage: setting Image");
        try{
           // imageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getActivity()));

        // ImageLoader imageLoader = new ImageLoader(getActivity());
        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressBar.setVisibility(View.VISIBLE);

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mProgressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                mProgressBar.setVisibility(View.INVISIBLE);

            }

        });
        }catch (Exception e){
            e.getMessage();
        }
    }

}
