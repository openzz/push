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

    String connStr;
    private static final int STRING_CAPTURE = 12;
    Map <String, String> devices = new HashMap<String, String>();
    public static final String APP_PREFERENCES = "mysettings";
    private SharedPreferences mSettings;

    ArrayList<String> onlineDevices;
    ArrayList<String> listToShow = new ArrayList<String>();


    MyParser parser = new MyParser();

    public class MyArrayAdapter extends ArrayAdapter<String> {
        private final Context context;
        private final String[] values;

        public MyArrayAdapter(Context context, String[] values) {
            super(context, -1, values);
            this.context = context;
            this.values = values;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
            TextView textView = (TextView) rowView.findViewById(R.id.name);
            textView.setText(values[position]);
            // change the icon for Windows and iPhone
            String s = values[position];
            if (s.startsWith("Android")) {
                textView.setTextColor(256);
            } else {

            }
            return rowView;
        }
    }


    public void showList(){
        //HashMap<String, String> deviceList = new HashMap<String, String>();

        ArrayList<DeviceObject> deviceList = new ArrayList<DeviceObject>();

        try {
            if (mSettings.getAll() != null) {
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();

                for(String el: gson.fromJson(mSettings.getString(APP_PREFERENCES, connStr), DeviceListFormatter.class).devices){
                    Map <String,String> map = parser.parseQrWithIotHub(el);

                    DeviceObject device = new DeviceObject(map.get("NotificationHubName"), map.get("SenderId"), map.get("NotHubConnectionString"),
                            map.get("TableName"), map.get("StorageConnectionString"), map.get("IotHubConnectionString"), map.get("DeviceName"));

                    deviceList.add(device);
                }

                for(DeviceObject dev: deviceList){

                    CheckDevices checkDevices = new CheckDevices();
                    checkDevices.execute(dev);
                    onlineDevices = checkDevices.get();
                    /*
                    DeviceTwin twinClient = DeviceTwin.createFromConnectionString(dev.getIotHubConnectionString());
                    DeviceTwinDevice device = new DeviceTwinDevice(dev.getDeviceName());

                    SqlQuery myQuery = SqlQuery.createSqlQuery("*", SqlQuery.FromType.DEVICES, "connectionState='Connected'", null);
                    //Query twinQuery = twinClient.queryTwin(myQuery.getQuery(), 500);
                    while (twinClient.hasNextDeviceTwin(twinQuery)){
                        DeviceTwinDevice d = twinClient.getNextDeviceTwin(twinQuery);
                        for (String arrEl: onlineDevices){
                            if(!arrEl.equals(dev.getDeviceName())){
                                onlineDevices.add(dev.getDeviceName());
                            }
                        }
                    }
                    */
                }

                for (DeviceObject d: deviceList){
                    listToShow.add(d.getNotificationHubName());
                }

/*
                for(String el: gson.fromJson(mSettings.getString(APP_PREFERENCES, connStr), DeviceListFormatter.class).devices){
                    Map <String,String> map = parser.parseQr(el);
                    deviceList.put("NotHubName", map.get("NotificationHubName"));
                }
*/
                String[] arrayToShow = listToShow.toArray(new String[0]);
                MyArrayAdapter adapter = new MyArrayAdapter(getActivity(), arrayToShow);
                //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listToShow);
                setListAdapter(adapter);

            }else {
                String[] arrayToShow = listToShow.toArray(new String[0]);
                MyArrayAdapter adapter = new MyArrayAdapter(getActivity(), arrayToShow);
                //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listToShow);
                setListAdapter(adapter);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


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
        mSettings = this.getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        //SharedPreferences.Editor editor = mSettings.edit();
        //editor.clear();
        //editor.apply();


        if (mSettings.contains(APP_PREFERENCES)) {
            // Получаем число из настроек
            connStr = mSettings.getString(APP_PREFERENCES, connStr);
            Map<String, String> deviceInfo = parser.parseQr(connStr);
            Map<String, ?> deviceList = mSettings.getAll();
            for (Map.Entry<String, ?> el: deviceList.entrySet()){
                Map<String, String> tempInfo = parser.parseQr((String)el.getValue());
                devices.put(tempInfo.get("NotificationHubName"), "Name");
            }
        }
        showList();
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
                    connStr = data.getStringExtra("ConnectionString");

                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    DeviceListFormatter dlf = new DeviceListFormatter();
                    dlf.devices = new ArrayList<String>();
                    try {
                        dlf = gson.fromJson(mSettings.getString(APP_PREFERENCES, connStr), DeviceListFormatter.class);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    //DeviceListFormatter dlf = new DeviceListFormatter();
                    if(!dlf.devices.contains(connStr)){
                        dlf.devices.add(connStr);
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
                        dlf = gson.fromJson(mSettings.getString(APP_PREFERENCES, connStr), DeviceListFormatter.class);
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
        showList();
    }


    @Override
    public void onListItemClick(ListView list, View v, int position, long id) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        DeviceListFormatter dlf = new DeviceListFormatter();
        dlf.devices = new ArrayList<String>();
        try {
            dlf = gson.fromJson(mSettings.getString(APP_PREFERENCES, connStr), DeviceListFormatter.class);
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


}