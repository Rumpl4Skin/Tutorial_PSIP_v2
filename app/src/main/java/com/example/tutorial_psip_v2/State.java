package com.example.tutorial_psip_v2;

public class State {

    private String name_lang; // название
    private android.graphics.Bitmap flagResource; // ресурс флага

    public State(String name, android.graphics.Bitmap flag){

        this.name_lang=name;
        this.flagResource=flag;
    }

    public String getName() {
        return this.name_lang;
    }

    public void setName(String name) {
        this.name_lang = name;
    }

    public android.graphics.Bitmap getFlagResource() {
        return this.flagResource;
    }

    public void setFlagResource(android.graphics.Bitmap flagResource) {
        this.flagResource = flagResource;
    }
}
