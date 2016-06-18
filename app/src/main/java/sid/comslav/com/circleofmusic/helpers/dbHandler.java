package sid.comslav.com.circleofmusic.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class dbHandler extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "circle_of_music.db";
    //    TABLE 1
    public static final String TABLE_TRACKS = "track_list";
    public static final String COLUMN_TRACK_ID = "track_id";
    public static final String COLUMN_TRACK_ID_TYPE = "INTEGER PRIMARY KEY";
    public static final String COLUMN_TRACK_NAME = "track_name";
    public static final String COLUMN_TRACK_NAME_TYPE = "TEXT";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_STATUS_TYPE = "TINYINT";
    public static final String COLUMN_TRACK_PATH = "track_path";
    public static final String COLUMN_TRACK_PATH_TYPE = "TEXT";

    public dbHandler(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_TRACKS + "\n(\n" + COLUMN_TRACK_ID + " " + COLUMN_TRACK_ID_TYPE + " , " + COLUMN_TRACK_NAME + " " + COLUMN_TRACK_NAME_TYPE + " , " + COLUMN_STATUS + " " + COLUMN_STATUS_TYPE + " , " + COLUMN_TRACK_PATH + " " + COLUMN_TRACK_PATH_TYPE + "\n);";
        try {
            db.execSQL(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String[] temp1 = fetchTrackPaths(db);
        String[] temp2 = fetchTracks(db);
        int[] temp3 = fetchStatus(db);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACKS);
        onCreate(db, temp1, temp2, temp3);
    }

    public void onCreate(SQLiteDatabase db, String[] tracks, String[] paths, int[] status) {
        String query = "CREATE TABLE IF NOT EXISTS " + TABLE_TRACKS + "\n(\n" + COLUMN_TRACK_ID + " " + COLUMN_TRACK_ID_TYPE + " , " + COLUMN_TRACK_NAME + " " + COLUMN_TRACK_NAME_TYPE + " , " + COLUMN_STATUS + " " + COLUMN_STATUS_TYPE + " , " + COLUMN_TRACK_PATH + " " + COLUMN_TRACK_PATH_TYPE + "\n);";
        try {
            db.execSQL(query);
            for (int i = 0; i < tracks.length; i++) {
                ContentValues cv = new ContentValues();
                cv.put(COLUMN_TRACK_NAME, tracks[i]);
                cv.put(COLUMN_STATUS, status[i]);
                if (Objects.equals(paths[i], "") && new File(Environment.DIRECTORY_DOWNLOADS + tracks[i]).exists()) {
                    cv.put(COLUMN_TRACK_PATH, Environment.DIRECTORY_DOWNLOADS + tracks[i]);
                } else {
                    cv.put(COLUMN_TRACK_PATH, paths[i]);
                }
                db.insert(TABLE_TRACKS, null, cv);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public boolean addTrack(String track_name, String track_path, int status) {
        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("SELECT * FROM %s WHERE %s == \"%s\";", TABLE_TRACKS, COLUMN_TRACK_NAME, track_name);
        try {
            Cursor c = db.rawQuery(query, null);
            if (c.getCount() == 0) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_TRACK_NAME, track_name);
                values.put(COLUMN_TRACK_PATH, track_path);
                values.put(COLUMN_STATUS, status);
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
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setStatus(String selectedItem, String track_path) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS, 2);
        contentValues.put(COLUMN_TRACK_PATH, track_path);
        try {
            db.update(TABLE_TRACKS, contentValues, COLUMN_TRACK_NAME + "='" + selectedItem + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
    }

    public int[] fetchStatus() {
        ArrayList<Integer> track_list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_STATUS + " FROM " + TABLE_TRACKS + ";";
        int[] status = new int[0];
        try {
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();
            int index = 0;
            while (!c.isAfterLast()) {
                if (c.getString(c.getColumnIndex(COLUMN_STATUS)) != null) {
                    track_list.add(index, c.getInt(c.getColumnIndex(COLUMN_STATUS)));
                    index++;
                }
                c.moveToNext();
            }
            c.close();
            status = new int[track_list.size()];
            for (int i = 0; i < track_list.size(); i++) {
                status[i] = track_list.get(i);
                if (status[i] == 1) {
                    ContentValues cv = new ContentValues();
                    cv.put(COLUMN_STATUS, 0);
                    int j = i + 1;
                    db.update(TABLE_TRACKS, cv, COLUMN_TRACK_ID + "=" + j, null);
                }
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;

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
            songs = new String[track_list.size()];
            for (int i = 0; i < track_list.size(); i++) {
                songs[i] = track_list.get(i);
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return songs;
    }

    public String[] fetchTrackPaths() {
        ArrayList<String> track_list = new ArrayList<>();
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_TRACK_PATH + " FROM " + TABLE_TRACKS + ";";
        String[] paths = new String[0];
        try {
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();
            int index = 0;
            while (!c.isAfterLast()) {
                if (c.getString(c.getColumnIndex(COLUMN_TRACK_PATH)) != null) {
                    track_list.add(index, c.getString(c.getColumnIndex(COLUMN_TRACK_PATH)));
                    index++;
                }
                c.moveToNext();
            }
            c.close();
            paths = new String[track_list.size()];
            for (int i = 0; i < track_list.size(); i++) {
                paths[i] = track_list.get(i);
            }
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return paths;
    }

    public int fetchStatus(String track_name) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_STATUS + " FROM " + TABLE_TRACKS + " WHERE " + COLUMN_TRACK_NAME + " like '" + track_name + "';";
        int tempStatus = 0;
        try {
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                if (c.getString(c.getColumnIndex(COLUMN_STATUS)) != null) {
                    tempStatus = c.getInt(c.getColumnIndex(COLUMN_STATUS));
                    break;
                }
                c.moveToNext();
            }
            c.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempStatus;

    }

    public String fetchTrackPaths(String track_name) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT " + COLUMN_TRACK_PATH + " FROM " + TABLE_TRACKS + " WHERE " + COLUMN_TRACK_NAME + " like '" + track_name + "';";
        String tempPath = "";
        try {
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                if (c.getString(c.getColumnIndex(COLUMN_TRACK_PATH)) != null) {
                    tempPath = c.getString(c.getColumnIndex(COLUMN_TRACK_PATH));
                    break;
                }
                c.moveToNext();
            }
            c.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempPath;
    }

    public int[] fetchStatus(SQLiteDatabase db) {
        ArrayList<Integer> track_list = new ArrayList<>();
        String query = "SELECT " + COLUMN_STATUS + " FROM " + TABLE_TRACKS + ";";
        int[] status = new int[0];
        try {
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();
            int index = 0;
            while (!c.isAfterLast()) {
                if (c.getString(c.getColumnIndex(COLUMN_STATUS)) != null) {
                    track_list.add(index, c.getInt(c.getColumnIndex(COLUMN_STATUS)));
                    index++;
                }
                c.moveToNext();
            }
            c.close();
            status = new int[track_list.size()];
            for (int i = 0; i < track_list.size(); i++) {
                status[i] = track_list.get(i);
                if (status[i] == 1) {
                    ContentValues cv = new ContentValues();
                    cv.put(COLUMN_STATUS, 0);
                    int j = i + 1;
                    db.update(TABLE_TRACKS, cv, COLUMN_TRACK_ID + "=" + j, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;

    }

    public String[] fetchTracks(SQLiteDatabase db) {
        ArrayList<String> track_list = new ArrayList<>();
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

    public String[] fetchTrackPaths(SQLiteDatabase db) {
        ArrayList<String> track_list = new ArrayList<>();
        String query = "SELECT " + COLUMN_TRACK_PATH + " FROM " + TABLE_TRACKS + ";";
        String[] paths = new String[0];
        try {
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();
            int index = 0;
            while (!c.isAfterLast()) {
                if (c.getString(c.getColumnIndex(COLUMN_TRACK_PATH)) != null) {
                    track_list.add(index, c.getString(c.getColumnIndex(COLUMN_TRACK_PATH)));
                    index++;
                }
                c.moveToNext();
            }
            c.close();
            paths = new String[track_list.size()];
            for (int i = 0; i < track_list.size(); i++) {
                paths[i] = track_list.get(i);
            }
            return paths;
        } catch (Exception e) {
            e.printStackTrace();
            return paths;
        }
    }
}
