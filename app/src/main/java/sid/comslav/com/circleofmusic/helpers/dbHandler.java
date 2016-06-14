package sid.comslav.com.circleofmusic.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class dbHandler extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "circle_of_music.db";
    //    TABLE 1
    public static final String TABLE_TRACKS = "track_list";
    public static final String COLUMN_TRACK_ID = "track_id";
    public static final String COLUMN_TRACK_ID_TYPE = "INTEGER PRIMARY KEY AUTOINCREMENT";
    public static final String COLUMN_TRACK_NAME = "track_name";
    public static final String COLUMN_TRACK_NAME_TYPE = "TEXT";
    public static final String COLUMN_DOWNLOADED = "downloaded";
    public static final String COLUMN_DOWNLOADED_TYPE = "TINYINT";

    //    TABLE 2
//    public static final String TABLE_VERSION = "version_data";
//    public static final String COLUMN_VERSION_ID = "version_id";
//    public static final String COLUMN_VERSION_ID_TYPE = "REAL PRIMARY KEY";

    public dbHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

//    public static final String CHANGELOG_INFO = "changelog_info";
//    public static final String CHANGELOG_INFO_TYPE = "TEXT";

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_TRACKS + "\n(\n" + COLUMN_TRACK_ID + " " + COLUMN_TRACK_ID_TYPE + " , " + COLUMN_TRACK_NAME + " " + COLUMN_TRACK_NAME_TYPE + " , " + COLUMN_DOWNLOADED + " " + COLUMN_DOWNLOADED_TYPE + "\n);";
//        String query2 = "CREATE TABLE IF NOT EXISTS " + TABLE_VERSION + "\n(\n" + COLUMN_VERSION_ID + " " + COLUMN_VERSION_ID_TYPE + "\n);";
        try {
            db.execSQL(query);
//            db.execSQL(query2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKS);
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VERSION);
        onCreate(db);
    }

    public boolean addTrack(String track_name) {
        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("SELECT * FROM %s WHERE %s == \"%s\";", TABLE_TRACKS, COLUMN_TRACK_NAME, track_name);
        try {
            Cursor c = db.rawQuery(query, null);
            if (c.getCount() == 0) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_TRACK_NAME, track_name);
                values.put(COLUMN_DOWNLOADED, 0);
                try {
                    c.close();
                    db.insert(TABLE_TRACKS, null, values);
                    db.close();
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String[] fetchTracks() {
        ArrayList<String> track_list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_TRACK_NAME + " FROM " + TABLE_TRACKS + ";";
        String[] songs = new String[0];
        try {
            Cursor c = db.rawQuery(query, null);

            c.moveToFirst();
            int index = 0;
            while (!c.isAfterLast()) {
                if (c.getString(c.getColumnIndex(COLUMN_TRACK_NAME)) != null) {
                    track_list.add(index, c.getString(c.getColumnIndex(COLUMN_TRACK_NAME)));
                    index++;
                }
                c.moveToNext();
            }
            c.close();
            db.close();
            songs = new String[track_list.size()];
            for (int i = 0; i < track_list.size(); i++) {
                songs[i] = track_list.get(i);
            }
            return songs;
        } catch (Exception e) {
            e.printStackTrace();
            return songs;
        }
    }

    public boolean[] fetchDownloadStatus() {
        ArrayList<Integer> track_list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_DOWNLOADED + " FROM " + TABLE_TRACKS + ";";
        boolean[] songs = new boolean[0];

        try {
            Cursor c = db.rawQuery(query, null);

            c.moveToFirst();
            int index = 0;
            while (!c.isAfterLast()) {
                if (c.getString(c.getColumnIndex(COLUMN_DOWNLOADED)) != null) {
                    track_list.add(index, c.getInt(c.getColumnIndex(COLUMN_DOWNLOADED)));
                    index++;
                }
                c.moveToNext();
            }
            c.close();
            db.close();
            songs = new boolean[track_list.size()];
            for (int i = 0; i < track_list.size(); i++) {
                songs[i] = track_list.get(i) > 0;
            }
            return songs;
        } catch (Exception e) {
            e.printStackTrace();
            return songs;
        }
    }

    public void setDownloadStatus(String selectedItem) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DOWNLOADED, 1);
        try {
            db.update(TABLE_TRACKS, contentValues, COLUMN_TRACK_NAME + "=", new String[]{selectedItem});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
