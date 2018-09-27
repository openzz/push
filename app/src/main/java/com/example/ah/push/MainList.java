package com.example.ah.push;

/**
 * Created by ah on 7/10/2018.
 */
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.microsoft.azure.sdk.iot.service.devicetwin.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.*;
import static android.app.Activity.RESULT_OK;

public class MainList extends ListFragment implements OnClickListener {

    String stringFromFile;
    private static final int STRING_CAPTURE = 12;
    public static final String APP_PREFERENCES = "mysettings";
    private SharedPreferences mSettings;

    ArrayList<String> onlineDevices;
    ArrayList<DeviceObject> devices = new ArrayList<>();

    MyParser parser = new MyParser();

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment, container, false);
        Button button = (Button)v.findViewById(R.id.buttonAdd);
        button.setOnClickListener(this);
        return v;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        updateList();
        showList(devices);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonAdd) {
            Intent intent = new Intent(getActivity(), AddActivity.class);
            startActivityForResult(intent, STRING_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        try {
            if (requestCode == STRING_CAPTURE) {
                if (resultCode == CommonStatusCodes.SUCCESS) {
                    stringFromFile = data.getStringExtra("ConnectionString");

                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    DeviceListFormatter dlf = new DeviceListFormatter();
                    dlf.devices = new ArrayList<String>();
                    try {
                        dlf = gson.fromJson(mSettings.getString(APP_PREFERENCES, stringFromFile), DeviceListFormatter.class);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    //DeviceListFormatter dlf = new DeviceListFormatter();
                    if(!dlf.devices.contains(stringFromFile)){
                        dlf.devices.add(stringFromFile);
                    }else{

                    }


                    String newJson = new Gson().toJson(dlf);
                    SharedPreferences.Editor editor = mSettings.edit();
                    //editor.clear();
                    editor.putString(APP_PREFERENCES, newJson);
                    editor.apply();

                    //Map<String, String> deviceInfo = parseConnStr(data.getStringExtra("ConnectionString"));
                }
            }

            if (requestCode == 0) {
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    int indexToDelete = bundle.getInt("indexToDelete", -1);

                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    DeviceListFormatter dlf = new DeviceListFormatter();
                    dlf.devices = new ArrayList<String>();
                    try {
                        dlf = gson.fromJson(mSettings.getString(APP_PREFERENCES, stringFromFile), DeviceListFormatter.class);
                        dlf.devices.remove(indexToDelete);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    SharedPreferences.Editor editor = mSettings.edit();

                    if (dlf.devices.size() == 0){
                        editor.clear();
                    }else {
                        String newJson = new Gson().toJson(dlf);
                        editor.putString(APP_PREFERENCES, newJson);
                    }
                    editor.apply();

                    //Map<String, String> deviceInfo = parseConnStr(data.getStringExtra("ConnectionString"));
                }
            }
        }catch (Exception e){
            System.out.println("No incoming connection data.");
        }
        showList(devices);
    }

    @Override
    public void onListItemClick(ListView list, View v, int position, long id) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        DeviceListFormatter dlf = new DeviceListFormatter();
        dlf.devices = new ArrayList<String>();
        try {
            dlf = gson.fromJson(mSettings.getString(APP_PREFERENCES, stringFromFile), DeviceListFormatter.class);
        }catch (Exception e){
            e.printStackTrace();
        }

        String[] tmpArr = new String[dlf.devices.size()];
        tmpArr = dlf.devices.toArray(tmpArr);
        Intent intent = new Intent(getActivity(), DisplayActivity.class);
        intent.putExtra("ConnectionString", tmpArr[position]);
        intent.putExtra("index", position);
        startActivityForResult(intent,0);
    }

    public void updateList(){
        mSettings = this.getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(APP_PREFERENCES)) {
            // Get connStrings from the file
            stringFromFile = mSettings.getString(APP_PREFERENCES, stringFromFile);
            Map<String, ?> deviceList = mSettings.getAll();
            for (Map.Entry<String, ?> el: deviceList.entrySet()){
                Map<String, String> tempInfo = parser.parseQrWithIotHub((String)el.getValue());
                devices.add(new DeviceObject(tempInfo.get("NotificationHubName"), tempInfo.get("SenderId"), tempInfo.get("NotHubConnectionString"), tempInfo.get("TableName"),
                        tempInfo.get("StorageConnectionString"), tempInfo.get("IotHubConnectionString"), tempInfo.get("DeviceName")));
            }
        }
    }

    public void checkDevicesConnection(){

    }

    public void showList(ArrayList<DeviceObject> deviceList){

        for(DeviceObject dev: deviceList){

            CheckDevices checkDevices = new CheckDevices();
            checkDevices.execute(dev);
            onlineDevices = checkDevices.get();
        }

        ArrayList<MyMainListItem> arrayToShow = new ArrayList<>();

        for(DeviceObject device: deviceList) {
            arrayToShow.add(new MyMainListItem(R.drawable.green, device.DeviceName));
        }

        MyIndicatorAdapter adapter = new MyIndicatorAdapter(getActivity(), arrayToShow);
        setListAdapter(adapter);
    }

}