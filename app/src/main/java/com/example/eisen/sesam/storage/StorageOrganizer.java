package com.example.eisen.sesam.storage;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import com.google.gson.Gson;

public class StorageOrganizer {

    public static final String SHARED_PREFS = "SHARED_PREFS" ;

    public static final String SLOT_ESP_SETIINGS ="SLOT_ESP_SETIINGS";
    public static final String SLOT_APP_SETIINGS ="SLOT_APP_SETIINGS";
    public static final String SLOT_ESP_ACTIVITIES ="SLOT_ESP_ACTIVITIES";

    private static SharedPreferences sharedPreferences;

    public static <T> T loadObject(ContextWrapper contextWrapper, String sharedprefs, String slot, Class<T> classOf)throws InstantiationException, IllegalAccessException{

        sharedPreferences = contextWrapper.getSharedPreferences(sharedprefs, Context.MODE_PRIVATE);
        T dataObject;

        if(sharedPreferences.contains(slot)) {
            String jsonLoad= sharedPreferences.getString(slot, "");
            dataObject = new Gson().fromJson(jsonLoad, classOf);
        }else{
            return classOf.newInstance();
        }

        return dataObject;
    }

    public static void saveObject(ContextWrapper contextWrapper, String sharedprefs, String slot, Object data){
        String jsonSave = new Gson().toJson(data);
        sharedPreferences = contextWrapper.getSharedPreferences(sharedprefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(slot, jsonSave);
        editor.commit();
    }

}
