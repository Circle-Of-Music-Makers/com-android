package com.sidzi.circleofmusic.fragments;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sidzi.circleofmusic.R;
import com.sidzi.circleofmusic.activities.ListFileActivity;
import com.sidzi.circleofmusic.adapters.ComTracksAdapter;
import com.sidzi.circleofmusic.helpers.FileUploader;
import com.sidzi.circleofmusic.helpers.VerticalSpaceDecorationHelper;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class TheFifthFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View TheFifthFragmentView = inflater.inflate(R.layout.fragment_fifth, container, false);

        RecyclerView mRecyclerView;
        RecyclerView.LayoutManager mLayoutManager;

        mRecyclerView = (RecyclerView) TheFifthFragmentView.findViewById(R.id.rvComTrackList);
        mLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            mRecyclerView.addItemDecoration(new VerticalSpaceDecorationHelper(getContext()));
        }
        ComTracksAdapter mComTracksAdapter = new ComTracksAdapter(getContext());
        mRecyclerView.setAdapter(mComTracksAdapter);

        FloatingActionButton fabUpload = (FloatingActionButton) TheFifthFragmentView.findViewById(R.id.fabUpload);
        fabUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ListFileActivity.class);
                startActivityForResult(intent, 220);
            }
        });

        return TheFifthFragmentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 220 && resultCode == RESULT_OK) {
            String path;
            path = data.getStringExtra("filepath");
            if (!"".equals(path) && !(new File(path).isDirectory()))
                new FileUploader(getContext(), path).execute();
            else
                startActivityForResult(new Intent(getContext(), ListFileActivity.class), 220);
        }
    }
}
