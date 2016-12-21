package com.sidzi.circleofmusic.helpers;

import android.content.Context;
import android.os.AsyncTask;

import com.sidzi.circleofmusic.adapters.TracksAdapter;
import com.sidzi.circleofmusic.entities.Track;

import java.util.ArrayList;

public class LocalMusicLoader extends AsyncTask<Void, Void, ArrayList<Track>> {
    private final ArrayList<Track> mTrackList = new ArrayList<>();
    private Context mContext;
    private TracksAdapter tracksAdapter;

    public LocalMusicLoader(Context mContext, TracksAdapter tracksAdapter) {
        super();
        this.mContext = mContext;
        this.tracksAdapter = tracksAdapter;
    }

    @Override
    protected ArrayList<Track> doInBackground(Void... voids) {
        return Utils.musicLoader(mContext);
    }

    @Override
    protected void onPostExecute(final ArrayList<Track> tracks) {
        super.onPostExecute(tracks);
        tracksAdapter.updateTracks(tracks);
    }
}
