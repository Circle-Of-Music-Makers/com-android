package com.sidzi.circleofmusic.helpers;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.sidzi.circleofmusic.entities.Track;

import java.sql.SQLException;
import java.util.List;

class Utils {
    static void saveToBucket(String path, Context mContext) {
        final OrmHandler ormHandler = OpenHelperManager.getHelper(mContext, OrmHandler.class);
        try {
            Dao<Track, String> dbTrack = ormHandler.getDao(Track.class);
            QueryBuilder<Track, String> queryBuilder = dbTrack.queryBuilder();
            SelectArg selectArg = new SelectArg();
            queryBuilder.where().eq("path", selectArg);
            PreparedQuery<Track> preparedQuery = queryBuilder.prepare();
            selectArg.setValue(path);
            List<Track> lister = dbTrack.query(preparedQuery);
            Track temp_track = lister.get(0);
            temp_track.setBucket(true);
            dbTrack.update(temp_track);
        } catch (SQLException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }
}
