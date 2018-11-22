package com.example.ah.push.sensors;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.ah.push.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class SensProximityActivity extends AppCompatActivity implements SensorEventListener {

    private ArrayList<Float> x = new ArrayList<>();

    private TextView sensorText;

    private TextView xView;

    GraphView graphX;

    LineGraphSeries<DataPoint> seriesX = new LineGraphSeries<>();

    SensorManager sensorManager;
    Sensor mSensor;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proximitysensor);

        sensorText = (TextView)findViewById(R.id.sensorTextView);

        xView = (TextView)findViewById(R.id.xAxisTextView);

        graphX = (GraphView)findViewById(R.id.graphX);
        graphX.getViewport().setXAxisBoundsManual(true);
        graphX.getViewport().setMaxX(SystemClock.currentThreadTimeMillis()+10);
        graphX.addSeries(seriesX);

        Intent intent = getIntent();
        int sensorType = intent.getIntExtra("sensorType", 0);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensor = sensorManager.getDefaultSensor(sensorType);

        sensorText.setText(mSensor.getName());

        x.add(0.0f);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long time = SystemClock.currentThreadTimeMillis();

                seriesX.appendData(new DataPoint(time, x.get(x.size()-1)), true, 1000);
            }
        }, 0, 10);
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

        x.add(event.values[0]);
        //seriesX.appendData(new DataPoint(time, event.values[0]), true, 100);
        xView.setText(String.valueOf(event.values[0]));
    }

}