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
import android.widget.TextView;

import com.example.ah.push.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class SensGyroscopeActivity extends AppCompatActivity implements SensorEventListener {

    float x = 0.0f;
    float y = 0.0f;
    float z = 0.0f;

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

            handler.postDelayed(this, 10);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);

        sensorText = (TextView)findViewById(R.id.sensorTextView);

        xView = (TextView)findViewById(R.id.xAxisTextView);
        yView = (TextView)findViewById(R.id.yAxisTextView);
        zView = (TextView)findViewById(R.id.zAxisTextView);

        graphX = (GraphView)findViewById(R.id.graphX);
        graphX.getViewport().setXAxisBoundsManual(true);
        graphX.getViewport().setMaxX(10000);
        graphX.addSeries(seriesX);

        graphY = (GraphView)findViewById(R.id.graphY);
        graphY.getViewport().setXAxisBoundsManual(true);
        graphY.getViewport().setMaxX(10000);
        graphY.addSeries(seriesY);

        graphZ = (GraphView)findViewById(R.id.graphZ);
        graphZ.getViewport().setXAxisBoundsManual(true);
        graphZ.getViewport().setMaxX(10000);
        graphZ.addSeries(seriesZ);

        Intent intent = getIntent();
        int sensorType = intent.getIntExtra("sensorType", 0);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensor = sensorManager.getDefaultSensor(sensorType);

        sensorText.setText(mSensor.getName());

        handler.post(appendData);

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

        //long time = SystemClock.currentThreadTimeMillis();

        x = event.values[0];
        xView.setText(String.valueOf(event.values[0]));
        //seriesX.appendData(new DataPoint(time, x), true, 1000);

        y = event.values[1];
        yView.setText(String.valueOf(event.values[1]));
        //seriesY.appendData(new DataPoint(time, y), true, 1000);

        z = event.values[2];
        zView.setText(String.valueOf(event.values[2]));
        //seriesZ.appendData(new DataPoint(time, z), true, 1000);
    }
}
