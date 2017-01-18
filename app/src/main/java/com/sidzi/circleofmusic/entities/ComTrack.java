package com.sidzi.circleofmusic.entities;


import org.json.JSONException;
import org.json.JSONObject;

public class ComTrack {
    private String username;
    private String path;
    private String title;
    private String artist;

    public ComTrack(JSONObject ComTrack) {
        try {
            this.username = ComTrack.getString("username");
            this.path = ComTrack.getString("path");
            this.title = ComTrack.getString("title");
            this.artist = ComTrack.getString("artist");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }
}
