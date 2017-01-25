package com.sidzi.circleofmusic.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.sidzi.circleofmusic.entities.Track;

import java.sql.SQLException;

public class OrmHandler extends OrmLiteSqliteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "circle_of_music.db";

    public OrmHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Track.class);
//            TableUtils.createTable(connectionSource, ChatMessage.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Track.class, true);
//            TableUtils.dropTable(connectionSource, ChatMessage.class, true);

            TableUtils.createTable(connectionSource, Track.class);
//            TableUtils.createTable(connectionSource, ChatMessage.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
