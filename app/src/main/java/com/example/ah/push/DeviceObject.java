package com.example.ah.push;

public class DeviceObject {

    String NotificationHubName;
    String SenderId;
    String NotHubConnectionString;
    String TableName;
    String StorageConnectionString;
    String IotHubConnectionString;
    String DeviceName;
    Boolean IsOnline;


    public DeviceObject(String NotificationHubName, String SenderId, String NotHubConnectionString, String TableName, String StorageConnectionString, String IotHubConnectionString, String DeviceName) {
        this.NotificationHubName=NotificationHubName;
        this.SenderId=SenderId;
        this.NotHubConnectionString=NotHubConnectionString;
        this.TableName=TableName;
        this.StorageConnectionString=StorageConnectionString;
        this.IotHubConnectionString=IotHubConnectionString;
        this.DeviceName=DeviceName;
        this.IsOnline=IsOnline;

    }

    public String getNotificationHubName() {
        return NotificationHubName;
    }

    public void setNotificationHubName(String NotificationHubName) {
        this.NotificationHubName=NotificationHubName;
    }


    public String getSenderId() {
        return SenderId;
    }

    public void setSenderId(String SenderId) {
        this.SenderId=SenderId;
    }


    public String getNotHubConnectionString() {
        return NotHubConnectionString;
    }

    public void setNotHubConnectionString(String NotHubConnectionString) {
        this.NotHubConnectionString=NotHubConnectionString;
    }


    public String getTableName() {
        return TableName;
    }

    public void setTableName(String TableName) {
        this.TableName=TableName;
    }


    public String getStorageConnectionString() {
        return StorageConnectionString;
    }

    public void setStorageConnectionString(String StorageConnectionString) {
        this.StorageConnectionString=StorageConnectionString;
    }


    public String getIotHubConnectionString() {
        return IotHubConnectionString;
    }

    public void setIotHubConnectionString(String IotHubConnectionString) {
        this.IotHubConnectionString=IotHubConnectionString;
    }


    public String getDeviceName() {
        return DeviceName;
    }

    public void setDeviceName(String DeviceName) {
        this.DeviceName=DeviceName;
    }


    public Boolean getIsOnline() {
        return IsOnline;
    }

    public void setIsOnline(Boolean IsOnline) {
        this.IsOnline=IsOnline;
    }

}
