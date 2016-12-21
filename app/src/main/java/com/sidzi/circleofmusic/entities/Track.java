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
    private String album;
    @DatabaseField
    private Boolean bucket;
    @DatabaseField(defaultValue = "0")
    private Integer play_count;

    public Track() {
    }

    public Track(String name, String path, String artist, String album, Boolean bucket) {
        this.path = path;
        this.name = name;
        this.artist = artist;
        this.bucket = bucket;
        this.album = album;
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

    public Boolean getBucket() {
        return bucket;
    }

    public void setBucket(Boolean bucket) {
        this.bucket = bucket;
    }

    public String getArtist() {
        return artist;
    }

    public Integer getPlay_count() {
        return play_count;
    }

    public void setPlay_count(Integer play_count) {
        this.play_count = play_count;
    }

    public String getAlbum() {
        return album;
    }
}
