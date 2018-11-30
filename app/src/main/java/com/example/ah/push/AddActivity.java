package com.example.ah.push;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.HashMap;
import java.util.Map;


public class AddActivity extends Activity implements View.OnClickListener {

    String connString;

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";

    MyParser parser = new MyParser();

    EditText connStr;
    TextView deviceName;
    TextView tableName;
    TextView IotHubConnStr;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        findViewById(R.id.read_barcode).setOnClickListener(this);
        connStr = (EditText)findViewById(R.id.input);
        deviceName = (TextView)findViewById(R.id.textView_deviceNameVal);
        tableName = (TextView)findViewById(R.id.textView_tableNameVal);
        IotHubConnStr = (TextView)findViewById(R.id.textView_connStrVal);



        connStr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();

                Map<String, String> deviceInfo = new HashMap<String, String>();

                deviceInfo = parser.parseQrWithIotHub(text);
                deviceName.setText(deviceInfo.get("DeviceName"));
                tableName.setText(deviceInfo.get("TableName"));
                IotHubConnStr.setText(deviceInfo.get("IotHubConnectionString"));


            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            // launch barcode activity.
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }
        if (v.getId() == R.id.buttonOk){
            // save and return to previous activity
            if(connStr != null) {
                connString = connStr.getText().toString();
                Intent retIntent = new Intent();
                retIntent.putExtra("ConnectionString", connString);
                setResult(CommonStatusCodes.SUCCESS, retIntent);
                finish();
            }else{
                finish();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    //barcodeValue.setText(barcode.displayValue);
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                    connStr.setText(barcode.displayValue);
                } else {
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                CommonStatusCodes.getStatusCodeString(resultCode);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        //connStr.setText(connString);
    }
}