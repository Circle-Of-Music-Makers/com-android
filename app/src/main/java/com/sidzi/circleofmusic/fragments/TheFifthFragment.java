package com.sidzi.circleofmusic.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sidzi.circleofmusic.R;

public class TheFifthFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View TheFifthFragmentView = inflater.inflate(R.layout.fragment_fifth, container, false);
        return TheFifthFragmentView;
    }
}
