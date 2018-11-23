package com.example.ah.push.sensors;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ah.push.PhoneAzureDevice;
import com.example.ah.push.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SensAccelerometerActivity extends AppCompatActivity implements SensorEventListener {

    private static String connString = "HostName=AHHub.azure-devices.net;DeviceId=MKR1000;SharedAccessKey=5k+d+VFq3QMNwHQXAgoqKHqaynz9x8NNqEy1E1PUrbw=";
    private static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
    private static DeviceClient client;
    private static String deviceName = "MKR1000";

    float x = 0.0f;
    float y = 0.0f;
    float z = 0.0f;

    boolean sending = false;

    private TextView sensorText;

    private TextView xView;
    private TextView yView;
    private TextView zView;

    GraphView graphX;
    GraphView graphY;
    GraphView graphZ;

    LineGraphSeries<DataPoint> seriesX = new LineGraphSeries<>();
    LineGraphSeries<DataPoint> seriesY = new LineGraphSeries<>();
    LineGraphSeries<DataPoint> seriesZ = new LineGraphSeries<>();

    SensorManager sensorManager;
    Sensor mSensor;

    int samplingPeriod = 1000;

    Handler handler = new Handler();

    private Runnable appendData = new Runnable() {
        @Override
        public void run() {
            long time = SystemClock.currentThreadTimeMillis();
            seriesX.appendData(new DataPoint(time, x), true, 1000);
            seriesY.appendData(new DataPoint(time, y), true, 1000);
            seriesZ.appendData(new DataPoint(time, z), true, 1000);

            try{
                client.open();
                PhoneAzureDevice.MessageSender sender = new PhoneAzureDevice.MessageSender(client, "66", mSensor.getName());
                sender.setValue(String.valueOf(x)+";"+String.valueOf(y)+";"+String.valueOf(z));
                ExecutorService executor = Executors.newFixedThreadPool(1);
                executor.execute(sender);
            }catch (IOException e){
                e.printStackTrace();
            }

            handler.postDelayed(this, samplingPeriod);
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        try {
            client = new DeviceClient(connString, protocol);
        }catch (URISyntaxException e){
            e.printStackTrace();
        }

        sensorText = (TextView)findViewById(R.id.sensorTextView);

        xView = (TextView)findViewById(R.id.xAxisTextView);
        yView = (TextView)findViewById(R.id.yAxisTextView);
        zView = (TextView)findViewById(R.id.zAxisTextView);

        graphX = (GraphView)findViewById(R.id.graphX);
        graphX.getViewport().setXAxisBoundsManual(true);
        graphX.getViewport().setMaxX(2000);

        graphY = (GraphView)findViewById(R.id.graphY);
        graphY.getViewport().setXAxisBoundsManual(true);
        graphY.getViewport().setMaxX(2000);

        graphZ = (GraphView)findViewById(R.id.graphZ);
        graphZ.getViewport().setXAxisBoundsManual(true);
        graphZ.getViewport().setMaxX(2000);

        graphX.addSeries(seriesX);
        graphY.addSeries(seriesY);
        graphZ.addSeries(seriesZ);

        Intent intent = getIntent();
        int sensorType = intent.getIntExtra("sensorType", 0);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensor = sensorManager.getDefaultSensor(sensorType);

        sensorText.setText(mSensor.getName());

        EditText samplingFrequency = (EditText)findViewById(R.id.editText_frequency);

        Button startMeasureButton = (Button)findViewById(R.id.button_measure);

        startMeasureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sending == false){
                    samplingPeriod = Integer.parseInt(samplingFrequency.getText().toString());
                    handler.post(appendData);
                    startMeasureButton.setText("Stop");
                    sending = true;
                }else {
                    handler.removeCallbacks(appendData);
                    startMeasureButton.setText("Start measure");
                    try {
                        client.closeNow();
                    }catch (IOException e){
                        e.printStackTrace();
                    }

                    sending = false;
                }
            }
        });
    }


    @Override
    protected void onResume(){
        super.onResume();
        sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { //Изменение точности показаний датчика
    }


    @Override
    public void onSensorChanged(SensorEvent event){
        long time = SystemClock.currentThreadTimeMillis();

        x = event.values[0];
        xView.setText(String.valueOf(event.values[0]));
        seriesX.appendData(new DataPoint(time, x), true, 1000);

        y = event.values[1];
        yView.setText(String.valueOf(event.values[1]));
        seriesY.appendData(new DataPoint(time, y), true, 1000);

        z = event.values[2];
        zView.setText(String.valueOf(event.values[2]));
        seriesZ.appendData(new DataPoint(time, z), true, 1000);
    }

}
