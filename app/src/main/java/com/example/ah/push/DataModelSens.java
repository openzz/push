package com.example.ah.push;

/**
 * Created by ah on 12/07/2018.
 */

public class DataModelSens {

    String sensorId;
    int color;
    String value;


    public DataModelSens(String sensorid, String val, int color) {
        this.sensorId = sensorid;
        this.color = color;
        this.value=val;
    }

    public String getSensor() {
        return sensorId;
    }
    public String getValue() {
        return value;
    }
    public int getColor() { return  color;}

    public void setValue(String v){
        value = v;
    }

    public void setColor(int c){
        color = c;
    }

}
