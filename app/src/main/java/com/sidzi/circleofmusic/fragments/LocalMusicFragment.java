package com.sidzi.circleofmusic.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sidzi.circleofmusic.R;
import com.sidzi.circleofmusic.adapters.TracksAdapter;
import com.sidzi.circleofmusic.helpers.LocalMusicLoader;
import com.sidzi.circleofmusic.helpers.VerticalSpaceDecorationHelper;


public class LocalMusicFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RecyclerView mRecyclerView;
        RecyclerView.LayoutManager mLayoutManager;
        View homeView = inflater.inflate(R.layout.fragment_track_list, container, false);
        mRecyclerView = (RecyclerView) homeView.findViewById(R.id.rVTrackList);
        mLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            mRecyclerView.addItemDecoration(new VerticalSpaceDecorationHelper(getContext()));
        }
        TracksAdapter tracksAdapter1 = new TracksAdapter(getContext());
        LocalMusicLoader lml = new LocalMusicLoader(getContext(), tracksAdapter1);
        lml.execute();
        mRecyclerView.setAdapter(tracksAdapter1);

        return homeView;
    }
}
