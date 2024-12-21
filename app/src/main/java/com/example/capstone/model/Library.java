package com.example.capstone.model;

import java.io.Serializable;

public class Library  implements Serializable {
    private int ID;
    private int CategoryID;
    private String Name;
    private float Humidity;
    private float HumidityUp;
    private float Temp;
    private float TempUp;
    private float LightOff;
    private float LightOn;
    private double SoilDown;
    private double SoilUp;
    private int UserID;

    public Library() {
    }

    public Library(int ID, int categoryID, String name, float humidity, float humidityUp, float temp, float tempUp, float lightOff, float lightOn, double soilDown, double soilUp, int userID) {
        this.ID = ID;
        CategoryID = categoryID;
        Name = name;
        Humidity = humidity;
        HumidityUp = humidityUp;
        Temp = temp;
        TempUp = tempUp;
        LightOff = lightOff;
        LightOn = lightOn;
        SoilDown = soilDown;
        SoilUp = soilUp;
        UserID = userID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(int categoryID) {
        CategoryID = categoryID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public float getHumidity() {
        return Humidity;
    }

    public void setHumidity(float humidity) {
        Humidity = humidity;
    }

    public float getTemp() {
        return Temp;
    }

    public void setTemp(float temp) {
        Temp = temp;
    }

    public float getLightOff() {
        return LightOff;
    }

    public void setLightOff(float lightOff) {
        LightOff = lightOff;
    }

    public float getLightOn() {
        return LightOn;
    }

    public void setLightOn(float lightOn) {
        LightOn = lightOn;
    }

    public float getHumidityUp() {
        return HumidityUp;
    }

    public void setHumidityUp(float humidityUp) {
        HumidityUp = humidityUp;
    }

    public float getTempUp() {
        return TempUp;
    }

    public void setTempUp(float tempUp) {
        TempUp = tempUp;
    }

    public double getSoilDown() {
        return SoilDown;
    }

    public void setSoilDown(double soilDown) {
        SoilDown = soilDown;
    }

    public double getSoilUp() {
        return SoilUp;
    }

    public void setSoilUp(double soilUp) {
        SoilUp = soilUp;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }
}
