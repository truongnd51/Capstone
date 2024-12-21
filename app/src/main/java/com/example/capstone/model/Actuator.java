package com.example.capstone.model;

public class Actuator {
    private String name;
    private int Image;
    private boolean isOn;

    public Actuator(String name, int Image) {
        this.name = name;
        this.Image = Image;
        this.isOn = false;
    }

    public String getName() {
        return name;
    }

    public int getImage() {
        return Image;
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        isOn = on;
    }
}
