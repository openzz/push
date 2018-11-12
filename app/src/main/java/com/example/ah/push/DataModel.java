package com.example.ah.push;

/**
 * Created by ah on 12/07/2018.
 */

public class DataModel {

    String sensorId;
    String value;
    int color;


    public DataModel(String sensorid, String val, int color) {
        this.sensorId=sensorid;
        this.value=val;
        this.color = color;

    }

    public String getSensor() {
        return sensorId;
    }

    public String getValue() {
        return value;
    }

    public int getColor() { return  color;}

}
