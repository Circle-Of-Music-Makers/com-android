package com.sidzi.circleofmusic.helpers;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.sidzi.circleofmusic.entities.Track;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSynchronization extends AsyncTask<Void, Void, Void> {
    private Context mContext;
    private List<Track> mTrackList;

    public DatabaseSynchronization(Context mContext) {
        this.mContext = mContext;
        this.mTrackList = new ArrayList<>();
    }

    private static Cursor makeSongCursor(final Context context) {
        String mSelection = (MediaStore.Audio.AudioColumns.IS_MUSIC + "=1") +
                " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''";
        return context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{
                        /* 0 */
                        MediaStore.Audio.AudioColumns.TITLE,
                        /* 1 */
                        MediaStore.Audio.AudioColumns.DATA,
                        /* 2 */
                        MediaStore.Audio.AudioColumns.ARTIST
                }, mSelection //$NON-NLS-2$
                , null, null);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Cursor mCursor = makeSongCursor(mContext);
        // Gather the data
        if (mCursor != null && mCursor.moveToFirst()) {
            do {

                // Copy the song name
                final String songName = mCursor.getString(0);

                // Copy the path/data
                final String data = mCursor.getString(1);

                // Copy artist name
                final String artist = mCursor.getString(2);

                // Create a new song
                final Track track = new Track(true, songName, data, artist);

                // Add everything up
                mTrackList.add(track);

            } while (mCursor.moveToNext());
        }
        // Close the cursor
        if (mCursor != null) {
            mCursor.close();
        }
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
}
