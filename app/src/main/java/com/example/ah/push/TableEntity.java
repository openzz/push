package com.example.ah.push;

import com.microsoft.azure.storage.table.TableServiceEntity;

public class TableEntity extends TableServiceEntity {

    String sensorid;
    String value;

    public TableEntity(String pk, String rk) {
        this.partitionKey = pk;
        this.rowKey = rk;
    }

    public TableEntity() { }



    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSensorid() {
        return this.sensorid;
    }

    public void setSensorid(String sensorid) {
        this.sensorid = sensorid;
    }

}
