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
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.EntityProperty;
import com.microsoft.azure.storage.table.EntityResolver;
import com.microsoft.azure.storage.table.TableQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class CheckDevices extends AsyncTask<DeviceObject, Void, ArrayList<String>> {

    public ArrayList<String> onlineDevices = new ArrayList<>();
    DeviceTwin twinClient;
    Query twinQuery;

    @Override
    protected ArrayList<String> doInBackground(DeviceObject... devices) {

        try {
            twinClient = DeviceTwin.createFromConnectionString(devices[0].getIotHubConnectionString());
            DeviceTwinDevice device = new DeviceTwinDevice(devices[0].getDeviceName());

            SqlQuery myQuery = SqlQuery.createSqlQuery("*", SqlQuery.FromType.DEVICES, "connectionState='Connected'", null);
            twinQuery = twinClient.queryTwin(myQuery.getQuery(), 500);

            while (twinClient.hasNextDeviceTwin(twinQuery)) {
                DeviceTwinDevice d = twinClient.getNextDeviceTwin(twinQuery);
                if(!onlineDevices.isEmpty()) {
                    for (String arrEl : onlineDevices) {
                        if (!arrEl.equals(devices[0].getDeviceName())) {
                            onlineDevices.add(devices[0].getDeviceName());
                        }
                    }
                }else{
                    onlineDevices.add(devices[0].getDeviceName());
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

