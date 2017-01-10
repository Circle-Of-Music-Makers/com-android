package com.sidzi.circleofmusic.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.sidzi.circleofmusic.R;
import com.sidzi.circleofmusic.adapters.TracksAdapter;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TracksAdapter tracksAdapter = new TracksAdapter(this);
        String query = getIntent().getStringExtra("query");
        tracksAdapter.queriedTracks(query);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvSearch);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(tracksAdapter);
    }
}
