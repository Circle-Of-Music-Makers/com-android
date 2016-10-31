package com.sidzi.circleofmusic.entities;

import org.json.JSONException;
import org.json.JSONObject;

public class Potm {
    private String path;
    private String title;
    private String description;
    private Integer month;

    public Potm(JSONObject potm) throws JSONException {
        this.path = potm.getString("path");
        this.title = potm.getString("title");
        this.description = potm.getString("description");
        this.month = potm.getInt("month");
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getMonth() {
        return month;
    }
}
