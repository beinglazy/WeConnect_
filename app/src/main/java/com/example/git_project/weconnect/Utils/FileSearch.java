package com.example.git_project.weconnect.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Alpa on 27-03-2018.
 */

public class FileSearch {

    //SEARCH A DIRECTORY AND RETURN A LIST OF ALL DIRECTORIES
    public static ArrayList<String> getDirectoryPaths(String directory){
        ArrayList<String> pathArray=new ArrayList<>();
        File file=new File(directory);
        File[] listfiles=file.listFiles();
        for (int i=0; i<listfiles.length;i++){
            if (listfiles[i].isDirectory()){
                pathArray.add(listfiles[i].getAbsolutePath());

            }
        }
        return pathArray;
    }
    //SEARCH A DIRECTORY AND RETURN A LIST OF ALL FILES
    public static ArrayList<String> getFilePaths(String directory){
        ArrayList<String> pathArray=new ArrayList<>();
        File file=new File(directory);
        File[] listfiles=file.listFiles();
        for (int i=0; i<listfiles.length;i++){
            if (listfiles[i].isFile()){
                pathArray.add(listfiles[i].getAbsolutePath());

            }
        }
        return pathArray;

    }
}
