package com.ccit19.merdog_client.backServ.chat;

public class ChatList {
    private String pet_img;
    private String chat_room;
    private String pet_name;
    private String chat_state;
    private String chat_request_id;
    private String message;
    private String date;

    public ChatList(String pet_img, String chat_room, String pet_name, String chat_state,String chat_request_id,String message,String date) {
        this.pet_img = pet_img;
        this.chat_room = chat_room;
        this.pet_name = pet_name;
        this.chat_state=chat_state;
        this.chat_request_id = chat_request_id;
        this.message = message;
        this.date = date;
    }

    public String getPet_img() {
        return pet_img;
    }

    public String getChat_room() {
        return chat_room;
    }

    public String getPet_name() {
        return pet_name;
    }

    public String getChat_state() {
        return chat_state;
    }

    public String getChat_request_id() {
        return chat_request_id;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }
}
