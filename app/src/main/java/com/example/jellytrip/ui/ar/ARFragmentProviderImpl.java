package com.example.jellytrip.ui.ar;

import androidx.fragment.app.Fragment;

import com.google.ar.sceneform.ux.ArFragment;

public class ARFragmentProviderImpl implements ARFragmentProvider {
    ArMapFragmentController controller;
    @Override
    public Fragment getFragment() {
        ArFragment fragment = new ArFragment();
        controller = new ArMapFragmentController(fragment);
        controller.start();
        return fragment;
    }
}
