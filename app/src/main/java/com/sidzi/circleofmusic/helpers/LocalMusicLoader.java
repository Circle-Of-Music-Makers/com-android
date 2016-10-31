package com.sidzi.circleofmusic.helpers;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.MediaStore;

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
    protected ArrayList<Track> doInBackground(Void... voids) {
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
        return mTrackList;
    }

    @Override
    protected void onPostExecute(final ArrayList<Track> tracks) {
        super.onPostExecute(tracks);
        tracksAdapter.updateTracks(tracks);
    }
}
