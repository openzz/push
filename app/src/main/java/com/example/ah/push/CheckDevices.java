package com.example.ah.push;

/**
 * Created by ah on 11/07/2018.
 */

// Include the following imports to use table APIs

import android.os.AsyncTask;

import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwin;
import com.microsoft.azure.sdk.iot.service.devicetwin.DeviceTwinDevice;
import com.microsoft.azure.sdk.iot.service.devicetwin.Query;
import com.microsoft.azure.sdk.iot.service.devicetwin.SqlQuery;
import com.microsoft.azure.sdk.iot.service.exceptions.IotHubException;

import java.io.IOException;
import java.util.ArrayList;


public class CheckDevices extends AsyncTask<DeviceObject, Void, ArrayList<String>> {

    public ArrayList<String> onlineDevices = new ArrayList<>();

    @Override
    protected ArrayList<String> doInBackground(DeviceObject... devices) {

        if(android.os.Debug.isDebuggerConnected())
            android.os.Debug.waitForDebugger();

        System.out.println("Hello");
        try {
            DeviceTwin twinClient = DeviceTwin.createFromConnectionString(devices[0].getIotHubConnectionString());
            //DeviceTwinDevice device = new DeviceTwinDevice(devices[0].getDeviceName());

            SqlQuery myQuery = SqlQuery.createSqlQuery("*", SqlQuery.FromType.DEVICES, "connectionState='Connected'", null);
            Query twinQuery = twinClient.queryTwin(myQuery.getQuery(), 100);

            while (twinClient.hasNextDeviceTwin(twinQuery)) {
                DeviceTwinDevice d = twinClient.getNextDeviceTwin(twinQuery);

                    if (!onlineDevices.contains(d.getDeviceId())) {
                        onlineDevices.add(d.getDeviceId());
                    }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IotHubException e) {
            e.printStackTrace();
        }

    return onlineDevices;
    }

}

