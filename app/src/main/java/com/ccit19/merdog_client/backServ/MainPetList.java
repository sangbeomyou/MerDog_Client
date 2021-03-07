package com.ccit19.merdog_client.backServ;

public class MainPetList {
    private String pet_img;
    private String pet_id;
    private String pet_name;

    public MainPetList(String pet_img, String pet_id, String pet_name) {
        this.pet_img = pet_img;
        this.pet_id = pet_id;
        this.pet_name = pet_name;
    }

    public String getPet_img() {
        return pet_img;
    }

    public String getPet_id() {
        return pet_id;
    }

    public String getPet_name() {
        return pet_name;
    }
}
