package com.example.capstone.model;

public class Log {
    private String timestamp;
    private float temperature;
    private float humidity;
    private float soilMoisture;
    private float soilMoisture2;

    public Log() {
    }

    public Log(String timestamp, float temperature, float humidity, float soilMoisture1, float soilMoisture2) {
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.humidity = humidity;
        this.soilMoisture = soilMoisture1;
        this.soilMoisture2 = soilMoisture2;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public void setHumidity(float humidity) {
        this.humidity = humidity;
    }

    public float getSoilMoisture() {
        return soilMoisture;
    }

    public void setSoilMoisture1(float soilMoisture1) {
        this.soilMoisture = soilMoisture1;
    }

    public float getSoilMoisture2() {
        return soilMoisture2;
    }

    public void setSoilMoisture2(float soilMoisture2) {
        this.soilMoisture2 = soilMoisture2;
    }
}
