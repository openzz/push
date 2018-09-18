package com.example.ah.push;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;


//for queue connection


/**
 * Created by AH on 5/2/2018.
 */

public class ListItem extends AppCompatActivity {

    String connString;
    static String json = "Waiting for data";
    static String text = "Waiting for message";
    int count = 0;

    // Connection String for the namespace can be obtained from the Azure portal under the
    // 'Shared Access policies' section.

    public static void main(String[] args) throws Exception {

    }

    private Map parseConnStr(String connstr) {
        Map<String, String> deviceInfo = new HashMap<String, String>();
        String[] parts = connstr.split("%");
        deviceInfo.put("NotificationHubName", parts[0]);
        deviceInfo.put("SenderId", parts[1]);
        deviceInfo.put("ConnectionString", parts[2]);
        return deviceInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item);

        Intent intent = getIntent();
        connString = intent.getStringExtra("ConnectionString");
        TextView device = (TextView) findViewById(R.id.device);
        Map<String, String> deviceInfo = parseConnStr(connString);
        device.setText(deviceInfo.get("EntityPath"));
        TextView message = (TextView) findViewById(R.id.message);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph.addSeries(series);
    }


}
