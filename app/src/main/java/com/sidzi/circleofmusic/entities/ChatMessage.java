package com.sidzi.circleofmusic.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "chat_trebie")
public class ChatMessage {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String body;

    @DatabaseField
    private boolean self_flag;

    public ChatMessage() {
    }

    public ChatMessage(String body, boolean self_flag) {

        this.body = body;

        this.self_flag = self_flag;
    }

    public boolean isSelf_flag() {
        return self_flag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


}
