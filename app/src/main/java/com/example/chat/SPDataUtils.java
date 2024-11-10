package com.example.chat;

import android.content.Context;
import android.content.SharedPreferences;

public class SPDataUtils {
    private static final String fileName = "myData";

    public static void storageInformation(Context context , String storageRemark , String storageInfo){
        SharedPreferences sp = context.getSharedPreferences(fileName , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(storageRemark , storageInfo);
        editor.apply();
    }

    public static String getStorageInformation(Context context , String storageRemark){
        String Result = null;

        SharedPreferences sp = context.getSharedPreferences(fileName , Context.MODE_PRIVATE);
        Result = sp.getString(storageRemark , null);

        return Result;
    }
}
