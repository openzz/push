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

    private static String connString = "HostName=AHHub.azure-devices.net;DeviceId=Redmi;SharedAccessKey=7yhVXWIo8yh2Fu536WI2cfSpKqMnwusmA8PlDNHKuM8=";
    private static IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;
    private static DeviceClient client;
    private static String deviceName = "Redmi";

    float x = 0.0f;
    float y = 0.0f;
    float z = 0.0f;
    float [] sensorValues;

    int samplingPeriod = 1000;

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

    Handler handler = new Handler();

    private Runnable appendData = new Runnable() {
        @Override
        public void run() {
            long time = SystemClock.currentThreadTimeMillis();
            seriesX.appendData(new DataPoint(time, x), true, 1000);
            seriesY.appendData(new DataPoint(time, y), true, 1000);
            seriesZ.appendData(new DataPoint(time, z), true, 1000);

            handler.postDelayed(this, 50);
        }
    };


    private Runnable sendData = new Runnable() {
        @Override
        public void run() {

            try{
                client.open();
                PhoneAzureDevice.MessageSender sender = new PhoneAzureDevice.MessageSender(client, "66", mSensor.getName());
                sender.setValue(sensorValues);
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
        graphX.addSeries(seriesX);

        graphY = (GraphView)findViewById(R.id.graphY);
        graphY.getViewport().setXAxisBoundsManual(true);
        graphY.getViewport().setMaxX(2000);
        graphY.addSeries(seriesY);

        graphZ = (GraphView)findViewById(R.id.graphZ);
        graphZ.getViewport().setXAxisBoundsManual(true);
        graphZ.getViewport().setMaxX(2000);
        graphZ.addSeries(seriesZ);

        Intent intent = getIntent();
        int sensorType = intent.getIntExtra("sensorType", 0);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensor = sensorManager.getDefaultSensor(sensorType);

        sensorText.setText(mSensor.getName());

        EditText samplingFrequency = (EditText)findViewById(R.id.editText_frequency);

        Button startMeasureButton = (Button)findViewById(R.id.button_measure);

        //Upadte graphs by asynch runnable
        //handler.post(appendData);

        startMeasureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sending == false){
                    samplingPeriod = Integer.parseInt(samplingFrequency.getText().toString());
                    handler.post(sendData);
                    startMeasureButton.setText("Stop");
                    sending = true;
                }else {
                    handler.removeCallbacks(sendData);
                    startMeasureButton.setText("Start");
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

        sensorValues = event.values;

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
