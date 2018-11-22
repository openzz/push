package com.example.ah.push.sensors;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.ah.push.R;

public class SensSignificantMotionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_significantmotionsensor);

        Intent intent = getIntent();
        int sensorType = intent.getIntExtra("sensorType", 0);

        SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sensor mSemsor = sensorManager.getDefaultSensor(sensorType);
    }
}
