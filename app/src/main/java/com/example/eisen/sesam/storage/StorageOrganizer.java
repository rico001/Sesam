package com.example.eisen.sesam.storage;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.eisen.sesam.userinterface.MainActivity;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Primitives;

import java.lang.reflect.Type;
import java.util.List;

public class StorageOrganizer {

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

    public static String loadString(ContextWrapper contextWrapper, String sharedprefs, String slot){
        sharedPreferences = contextWrapper.getSharedPreferences(sharedprefs, Context.MODE_PRIVATE);
        if(sharedPreferences.contains(slot)) {
            String ip= sharedPreferences.getString(slot, "");
            return ip;
        }else{
            Log.d("IP", "SERVERIP existiert noch nicht");
            return MainActivity.IP;
        }
    }

    public static void saveString(ContextWrapper contextWrapper, String sharedprefs, String slot, String data){
        sharedPreferences = contextWrapper.getSharedPreferences(sharedprefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(slot, data);
        editor.commit();
    }

    public static void saveObject(ContextWrapper contextWrapper, String sharedprefs, String slot, Object data){
        String jsonSave = new Gson().toJson(data);
        sharedPreferences = contextWrapper.getSharedPreferences(sharedprefs, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(slot, jsonSave);
        editor.commit();
    }

}
