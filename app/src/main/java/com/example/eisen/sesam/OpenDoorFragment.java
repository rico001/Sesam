package com.example.eisen.sesam;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;



/**
 * A simple {@link Fragment} subclass.
 */
public class OpenDoorFragment extends Fragment {

    private ImageView imageView;
    private Button buttonOpenDoor;

    public static final String SHARED_PREFS = "sharedPrefs" ;
    public static final String SAVESETIINGS="savesettings";
    public static final String OPENDOORTOPIC="Sesam/openDoor";

    public OpenDoorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_open_door, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        imageView= (ImageView) getView().findViewById(R.id.imageViewBsp);
        buttonOpenDoor= (Button) getView().findViewById(R.id.buttonOpenDoor);
        buttonOpenDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnim(5);
                sendDataToServer("1");
            }
        });

    }

    private void startAnim(int sek){
        Log.d("aimation", "animation läuft");
        imageView.setVisibility(imageView.getVisibility()+10);
    }

    private void sendDataToServer(String data){
        ((MainActivity)getActivity()).pubTo(data,OPENDOORTOPIC);
    }



}
