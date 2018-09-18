package com.example.ah.push;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ah on 25/07/2018.
 */

public class MyParser {

    public Map parseQr(String connstr) {
        Map<String, String> deviceInfo = new HashMap<String, String>();
        String[] parts = connstr.split("%");
        deviceInfo.put("NotificationHubName", parts[0]);
        deviceInfo.put("SenderId", parts[1]);
        deviceInfo.put("NotHubConnectionString", parts[2]);
        deviceInfo.put("TableName", parts[3]);
        deviceInfo.put("StorageConnectionString", parts[4]);

        return deviceInfo;
    }
}
