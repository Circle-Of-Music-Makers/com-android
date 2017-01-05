package com.sidzi.circleofmusic.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.sidzi.circleofmusic.config;
import com.sidzi.circleofmusic.entities.Track;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSynchronization extends AsyncTask<Void, Void, Void> {
    private Context mContext;

    public DatabaseSynchronization(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ArrayList<Track> mTrackList = Utils.musicLoader(mContext);
        OrmHandler ormHandler = OpenHelperManager.getHelper(mContext, OrmHandler.class);
        try {
            Dao<Track, String> dbTrack = ormHandler.getDao(Track.class);
            List<Track> _temp = dbTrack.queryForAll();
            _temp.removeAll(mTrackList);
            for (Track t :
                    mTrackList) {
                dbTrack.createIfNotExists(t);
            }
            dbTrack.delete(_temp);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        OpenHelperManager.releaseHelper();
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        SharedPreferences settings = mContext.getSharedPreferences("com_prefs", 0);
        if (settings.getBoolean("init", true)) {
            BucketSaver bucketSaver = new BucketSaver(mContext);
            if (bucketSaver.importFile()) {
                File com_dir = new File(config.com_local_url);
                if (!com_dir.exists()) {
                    if (!com_dir.mkdirs()) {
                        throw new UnsupportedOperationException("Could not create com folder");
                    }
                }
            }
            settings.edit().putBoolean("init", false).apply();
        }
        super.onPostExecute(aVoid);
    }
}
