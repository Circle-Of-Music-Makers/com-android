package com.sidzi.circleofmusic.entities;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "table_tracks")
public class Track {
    @DatabaseField(generatedId = true)
    private Integer id;
    @DatabaseField
    private String name;
    @DatabaseField
    private String path;
    @DatabaseField
    private String emotion;
    @DatabaseField
    private Boolean bucket;

    public Track() {
    }

    public Track(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
}
