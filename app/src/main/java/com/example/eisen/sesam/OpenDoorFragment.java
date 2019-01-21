package com.example.eisen.sesam;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class OpenDoorFragment extends Fragment {

    private ImageView imageView;
    private Button buttonOpenDoor;

    public static final String OPENDOORTOPIC="Sesam/openDoorNow";

    public OpenDoorFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_open_door, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageView= (ImageView) getView().findViewById(R.id.imageView8);
        imageView.setVisibility(View.INVISIBLE);
        buttonOpenDoor= (Button) getView().findViewById(R.id.buttonOpenDoor);
        buttonOpenDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsModel settingsModel=((MainActivity)getActivity()).getSettingsModel();
                String data=settingsModel.getTimes()+"";
                data+=settingsModel.getDuration();
                ((MainActivity)getActivity()).sendDataToServer(OPENDOORTOPIC,data,true); //ESp sendet nach öffnen leeren string!!
                startAnim(settingsModel.getDuration());
            }
        });

    }

    private void startAnim(int sek){

        Log.d("aimation", "animation läuft");
        imageView.setVisibility(View.VISIBLE);
        buttonOpenDoor.setAlpha(0.5f);
        buttonOpenDoor.setClickable(false);
        final int repeat=sek;
        final Animation fadingPlus = new AlphaAnimation(0,1);
        fadingPlus.setDuration(1000);
        fadingPlus.setRepeatCount(sek);
        fadingPlus.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                vibratNow(300);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(animation.getRepeatCount()==repeat){
                    imageView.setVisibility(View.INVISIBLE);
                    buttonOpenDoor.setAlpha(1f);
                    buttonOpenDoor.setClickable(true);
                    vibratNow(300);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                vibratNow(300);
            }
        });

        imageView.startAnimation(fadingPlus);
        vibratNow(sek);

    }

    private void vibratNow(int millis){

        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for n milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //v.vibrate(VibrationEffect.createOneShot(millis+1000, VibrationEffect.DEFAULT_AMPLITUDE));
            v.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(millis);
        }
    }





}
