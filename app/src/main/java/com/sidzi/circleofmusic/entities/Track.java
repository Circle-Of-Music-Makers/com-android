package com.sidzi.circleofmusic.entities;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "table_tracks")
public class Track {
    @DatabaseField(id = true)
    private String path;
    @DatabaseField
    private String name;
    @DatabaseField
    private String artist;
    @DatabaseField
    private String emotion;
    @DatabaseField
    private Boolean bucket;
    @DatabaseField
    private Boolean local;

    public Track() {
    }

    public Track(Boolean local, String name, String path, String artist) {
        this.local = local;
        this.artist = artist;
        this.name = name;
        this.path = path;
    }

    public Track(String name, String path, String artist, Boolean bucket) {
        this.path = path;
        this.name = name;
        this.artist = artist;
        this.bucket = bucket;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getEmotion() {
        return emotion;
    }

    public void setEmotion(String emotion) {
        this.emotion = emotion;
    }

    public Boolean getBucket() {
        return bucket;
    }

    public void setBucket(Boolean bucket) {
        this.bucket = bucket;
    }

    public String getArtist() {
        return artist;
    }
}
