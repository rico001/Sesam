package com.example.eisen.sesam.com.example.eisen.interfaces;

import android.view.View;

public interface IUpdatableFragment {

    String TAG = "IUpdatableFragment";

    void onMainActivityUpdate();

    void refreshFragment();
}
