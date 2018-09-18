package com.example.ah.push;

public class DeviceObject {

    String NotificationHubName;
    String SenderId;
    String NotHubConnectionString;
    String TableName;
    String StorageConnectionString;
    String IotHubConnectionString;
    String DeviceName;


    public DeviceObject(String NotificationHubName, String SenderId, String NotHubConnectionString, String TableName, String StorageConnectionString, String IotHubConnectionString, String DeviceName) { ;
        this.NotificationHubName=NotificationHubName;
        this.SenderId=SenderId;
        this.NotHubConnectionString=NotHubConnectionString;
        this.TableName=TableName;
        this.StorageConnectionString=StorageConnectionString;
        this.IotHubConnectionString=IotHubConnectionString;
        this.DeviceName=DeviceName;

    }

    public String getNotificationHubName() {
        return NotificationHubName;
    }

    public String getSenderId() {
        return SenderId;
    }

    public String getNotHubConnectionString() {
        return NotHubConnectionString;
    }

    public String getTableName() {
        return TableName;
    }

    public String getStorageConnectionString() {
        return StorageConnectionString;
    }

    public String getIotHubConnectionString() {
        return IotHubConnectionString;
    }

    public String getDeviceName() {
        return DeviceName;
    }


}
