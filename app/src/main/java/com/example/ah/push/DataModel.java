package com.example.ah.push;

/**
 * Created by ah on 12/07/2018.
 */

public class DataModel {

    String sensorId;
    String value;


    public DataModel(String sensorid, String val) {
        this.sensorId=sensorid;
        this.value=val;

    }

    public String getSensor() {
        return sensorId;
    }

    public String getValue() {
        return value;
    }

}
