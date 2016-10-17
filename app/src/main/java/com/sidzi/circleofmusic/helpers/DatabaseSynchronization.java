package com.sidzi.circleofmusic.helpers;

import android.content.Context;
import android.os.AsyncTask;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.sidzi.circleofmusic.entities.Track;

import java.sql.SQLException;
import java.util.List;

class DatabaseSynchronization extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private List<Track> tracks;

    DatabaseSynchronization(Context mContext, List<Track> tracks) {
        this.mContext = mContext;
        this.tracks = tracks;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        OrmHandler ormHandler = OpenHelperManager.getHelper(mContext, OrmHandler.class);
        for (Track t :
                tracks) {
            try {
                Dao<Track, String> dbTrack = ormHandler.getDao(Track.class);
                dbTrack.createIfNotExists(t);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
