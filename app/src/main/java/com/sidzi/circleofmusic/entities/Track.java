package com.sidzi.circleofmusic.entities;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;

@DatabaseTable(tableName = "table_tracks")
public class Track implements Serializable, Externalizable {
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

    public Track(String path) {
        this.path = path;
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Track)
            return ((Track) obj).getPath().contentEquals(this.path);
        throw new UnsupportedOperationException();
    }

    @Override
    public void writeExternal(ObjectOutput objectOutput) throws IOException {
        objectOutput.writeObject(this);
    }

    @Override
    public void readExternal(ObjectInput objectInput) throws IOException, ClassNotFoundException {
        objectInput.readObject();
    }
}
