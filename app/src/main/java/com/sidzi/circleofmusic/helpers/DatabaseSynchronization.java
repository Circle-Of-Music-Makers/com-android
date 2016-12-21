package com.sidzi.circleofmusic.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.sidzi.circleofmusic.entities.Track;

import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseSynchronization extends AsyncTask<Void, Void, Void> {
    private Context mContext;

    public DatabaseSynchronization(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ArrayList<Track> mTrackList = Utils.musicLoader(mContext);
        OrmHandler ormHandler = OpenHelperManager.getHelper(mContext, OrmHandler.class);
        for (Track t :
                mTrackList) {
            try {
                Dao<Track, String> dbTrack = ormHandler.getDao(Track.class);
                dbTrack.createIfNotExists(t);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        OpenHelperManager.releaseHelper();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        SharedPreferences settings = mContext.getSharedPreferences("com_prefs", 0);
        if (settings.getBoolean("init", true)) {
            BucketSaver bucketSaver = new BucketSaver(mContext);
            if (bucketSaver.importFile())
                settings.edit().putBoolean("init", false).apply();
        }
        super.onPostExecute(aVoid);
    }
}
