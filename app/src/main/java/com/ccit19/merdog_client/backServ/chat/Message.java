package com.ccit19.merdog_client.backServ.chat;

public class Message {
    private String text;
    private String memberData;
    private String date;
    private boolean belongsToCurrentUser;

    public Message(String text, String name,String date, boolean belongsToCurrentUser) {
        this.text = text;
        this.memberData = name;
        this.date = date;
        this.belongsToCurrentUser = belongsToCurrentUser;
    }

    public String getText() {
        return text;
    }

    public String getMemberData() {
        return memberData;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }

    public String getDate() {
        return date;
    }
}
