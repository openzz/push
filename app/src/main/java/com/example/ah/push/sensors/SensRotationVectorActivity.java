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

public class SensRotationVectorActivity extends AppCompatActivity implements SensorEventListener {

    private ArrayList<Float> x = new ArrayList<>();
    private ArrayList<Float> y = new ArrayList<>();
    private ArrayList<Float> z = new ArrayList<>();
    private ArrayList<Float> sc = new ArrayList<>();

    private TextView sensorText;

    private TextView xView;
    private TextView yView;
    private TextView zView;
    private TextView scView;

    GraphView graphX;
    GraphView graphY;
    GraphView graphZ;
    GraphView graphSc;

    LineGraphSeries<DataPoint> seriesX = new LineGraphSeries<>();
    LineGraphSeries<DataPoint> seriesY = new LineGraphSeries<>();
    LineGraphSeries<DataPoint> seriesZ = new LineGraphSeries<>();
    LineGraphSeries<DataPoint> seriesSc = new LineGraphSeries<>();

    SensorManager sensorManager;
    Sensor mSensor;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotationvectorsensor);

        sensorText = (TextView)findViewById(R.id.sensorTextView);

        xView = (TextView)findViewById(R.id.xAxisTextView);
        yView = (TextView)findViewById(R.id.yAxisTextView);
        zView = (TextView)findViewById(R.id.zAxisTextView);
        scView = (TextView)findViewById(R.id.scalarTextView);

        graphX = (GraphView)findViewById(R.id.graphX);
        graphX.getViewport().setXAxisBoundsManual(true);
        graphX.getViewport().setMaxX(SystemClock.currentThreadTimeMillis()+10);
        graphX.addSeries(seriesX);

        graphY = (GraphView)findViewById(R.id.graphY);
        graphY.getViewport().setXAxisBoundsManual(true);
        graphY.getViewport().setMaxX(SystemClock.currentThreadTimeMillis()+10);
        graphY.addSeries(seriesY);

        graphZ = (GraphView)findViewById(R.id.graphZ);
        graphZ.getViewport().setXAxisBoundsManual(true);
        graphZ.getViewport().setMaxX(SystemClock.currentThreadTimeMillis()+10);
        graphZ.addSeries(seriesZ);

        graphSc = (GraphView)findViewById(R.id.graphScalar);
        graphSc.getViewport().setXAxisBoundsManual(true);
        graphSc.getViewport().setMaxX(SystemClock.currentThreadTimeMillis()+10);
        graphSc.addSeries(seriesSc);

        Intent intent = getIntent();
        int sensorType = intent.getIntExtra("sensorType", 0);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensor = sensorManager.getDefaultSensor(sensorType);

        sensorText.setText(mSensor.getName());

        x.add(0.0f);
        y.add(0.0f);
        z.add(0.0f);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long time = SystemClock.currentThreadTimeMillis();

                seriesX.appendData(new DataPoint(time, x.get(x.size()-1)), true, 1000);
                seriesY.appendData(new DataPoint(time, y.get(y.size()-1)), true, 1000);
                seriesZ.appendData(new DataPoint(time, z.get(z.size()-1)), true, 1000);
                seriesSc.appendData(new DataPoint(time, sc.get(sc.size()-1)), true, 1000);
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

        x.add(event.values[0]);
        //seriesX.appendData(new DataPoint(time, event.values[0]), true, 100);
        xView.setText(String.valueOf(event.values[0]));

        y.add(event.values[1]);
        //seriesY.appendData(new DataPoint(time, event.values[1]), true, 100);
        yView.setText(String.valueOf(event.values[1]));

        z.add(event.values[2]);
        //seriesZ.appendData(new DataPoint(time, event.values[2]), true, 100);
        zView.setText(String.valueOf(event.values[2]));

        sc.add(event.values[3]);
        //seriesSc.appendData(new DataPoint(time, event.values[2]), true, 100);
        scView.setText(String.valueOf(event.values[2]));
    }

}
