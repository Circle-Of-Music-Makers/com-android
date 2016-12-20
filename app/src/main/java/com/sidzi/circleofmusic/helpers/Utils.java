package com.sidzi.circleofmusic.helpers;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.sidzi.circleofmusic.entities.Track;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class Utils {
    static boolean BUCKET_OPS = false;

    static void bucketOps(String path, Boolean bucket, Context mContext) {
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
            temp_track.setBucket(bucket);
            dbTrack.update(temp_track);
            BUCKET_OPS = true;
        } catch (SQLException | IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    static ArrayList<Track> musicLoader(Context mContext) {

        ArrayList<Track> mTrackList = new ArrayList<>();

        String mSelection = (MediaStore.Audio.AudioColumns.IS_MUSIC + "=1") +
                " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''";
        Cursor mCursor = mContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        /* 0 */
                        MediaStore.Audio.AudioColumns.TITLE,
                        /* 1 */
                        MediaStore.Audio.AudioColumns.DATA,
                        /* 2 */
                        MediaStore.Audio.AudioColumns.ARTIST,

                        MediaStore.Audio.AudioColumns.ALBUM

                }, mSelection //$NON-NLS-2$
                , null, null);

        if (mCursor != null && mCursor.moveToFirst()) {
            do {

                // Copy the song name
                final String songName = mCursor.getString(0);

                // Copy the path/data
                final String path = mCursor.getString(1);

                // Copy artist name
                final String artist = mCursor.getString(2);


                final String album = mCursor.getString(3);

                // Create a new song
                final Track track = new Track(songName, path, artist, album, false);


                // Add everything up
                mTrackList.add(track);

            } while (mCursor.moveToNext());
        }
        // Close the cursor
        if (mCursor != null) {
            mCursor.close();
        }
        return mTrackList;
    }
}
