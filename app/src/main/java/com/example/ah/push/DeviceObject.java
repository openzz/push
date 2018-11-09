package com.example.ah.push;

import android.os.Parcel;
import android.os.Parcelable;

public class DeviceObject implements Parcelable {

    String NotificationHubName;
    String SenderId;
    String NotHubConnectionString;
    String TableName;
    String StorageConnectionString;
    String IotHubConnectionString;
    String DeviceName;
    Boolean IsOnline = false;
    String FullConnString;


    public DeviceObject(String NotificationHubName, String SenderId, String NotHubConnectionString, String TableName, String StorageConnectionString, String IotHubConnectionString, String DeviceName, String FullConnString) {
        this.NotificationHubName=NotificationHubName;
        this.SenderId=SenderId;
        this.NotHubConnectionString=NotHubConnectionString;
        this.TableName=TableName;
        this.StorageConnectionString=StorageConnectionString;
        this.IotHubConnectionString=IotHubConnectionString;
        this.DeviceName=DeviceName;
        this.IsOnline=IsOnline;
        this.FullConnString = FullConnString;

    }


    public DeviceObject(Parcel in) {
        this.NotificationHubName=in.readString();
        this.SenderId=in.readString();
        this.NotHubConnectionString=in.readString();
        this.TableName=in.readString();
        this.StorageConnectionString=in.readString();
        this.IotHubConnectionString=in.readString();
        this.DeviceName=in.readString();
        this.FullConnString = in.readString();
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


    public String getFullConnString() {
        return FullConnString;
    }

    public void setFullConnString(String FullConnString) {
        this.FullConnString=FullConnString;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(NotificationHubName);
        dest.writeString(SenderId);
        dest.writeString(NotHubConnectionString);
        dest.writeString(TableName);
        dest.writeString(StorageConnectionString);
        dest.writeString(IotHubConnectionString);
        dest.writeString(DeviceName);
    }

    public static  final Parcelable.Creator<DeviceObject> CREATOR = new Parcelable.Creator<DeviceObject>(){
        public DeviceObject createFromParcel(Parcel in){
            return new DeviceObject(in);
        }

        public DeviceObject[] newArray(int size){
            return new DeviceObject[size];
        }
    };
}
