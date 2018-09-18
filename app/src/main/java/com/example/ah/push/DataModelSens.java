package com.example.ah.push;

/**
 * Created by ah on 12/07/2018.
 */

public class DataModelSens {

    String sensorId;
    int color;


    public DataModelSens(String sensorid, int color) {
        this.sensorId = sensorid;
        this.color = color;
    }

    public String getSensor() {
        return sensorId;
    }
    public int getColor() { return  color;}

}
