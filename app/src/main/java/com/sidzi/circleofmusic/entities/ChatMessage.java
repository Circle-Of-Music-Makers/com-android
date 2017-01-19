package com.sidzi.circleofmusic.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "table_messages")
public class ChatMessage {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String body;

    @DatabaseField
    private String sender;

    public ChatMessage() {
    }

    public ChatMessage(String sender, String body) {
        this.sender = sender;
        this.body = body;
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

    public String getSender() {
        return sender;
    }
}
