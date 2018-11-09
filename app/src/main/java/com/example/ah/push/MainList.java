package com.example.ah.push;

/**
 * Created by ah on 7/10/2018.
 */
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.google.android.gms.common.api.CommonStatusCodes;
import java.util.ArrayList;
import java.util.Map;
import android.widget.ListView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

    FirebaseOperations FBOpers = new FirebaseOperations();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment, container, false);
        Button button = v.findViewById(R.id.buttonAdd);
        button.setOnClickListener(this);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        updateListFromFirebase(FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("devices"));

        //String connStr = "AnotherNotHub%823910043359%Endpoint=sb://namespaceforanotherhub.servicebus.windows.net/;SharedAccessKeyName=DefaultListenSharedAccessSignature;SharedAccessKey=sM1ehJRmCynrWKBEp2bzFsK0SirGvsZU/wBi+m3psB0=%AnotherTable%DefaultEndpointsProtocol=http;AccountName=arduappba0e;AccountKey=p+en8MZCfLJ4ATCn25CGp8IbNUZy/UfsHBWlT5YKU9dyCHZhq02WUWcNxIidlYjZWrU1UmtJUn5g2+ya3n3Ewg==;%HostName=AHHub.azure-devices.net;SharedAccessKeyName=service;SharedAccessKey=HLW6hFlQrEf9uOlu/Yr7+xOrBJYZx0AM/aP1ECxdYqc=%Hello_Andrew!";
        //FBOpers.addDeviceToDb(FirebaseDatabase.getInstance(), user, connStr);
        //FBOpers.removeDeviceFromDb(FirebaseDatabase.getInstance(), user, connStr);

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
                    String stringFromQr = data.getStringExtra("ConnectionString");

                    Map<String, String> tempInfo = parser.parseQrWithIotHub(stringFromQr);
                    DeviceObject tmpObject = new DeviceObject(tempInfo.get("NotificationHubName"), tempInfo.get("SenderId"), tempInfo.get("NotHubConnectionString"), tempInfo.get("TableName"),
                            tempInfo.get("StorageConnectionString"), tempInfo.get("IotHubConnectionString"), tempInfo.get("DeviceName"), stringFromQr);

                    FBOpers.addDeviceToDb(FirebaseDatabase.getInstance(), user, tmpObject.getFullConnString());

                    //TODO

                    if(!devices.contains(tmpObject)){
                        devices.add(tmpObject);
                    }else{
                        MainActivity.mainActivity.ToastNotify("Already in list.");
                    }
                }
            }

            if (requestCode == 0) {
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    int indexToDelete;
                    try{
                        indexToDelete = bundle.getInt("indexToDelete", -1);
                    }catch (Exception e){
                        e.printStackTrace();
                        indexToDelete = -1;
                    }
                    //TODO delete device from FB
                    //devices.remove(indexToDelete);
                    FBOpers.removeDeviceFromDb(FirebaseDatabase.getInstance(), user, devices.get(indexToDelete).getFullConnString());

                }
            }
        }catch (Exception e){
            System.out.println("No incoming connection data.");
        }
        updateListFromFirebase(FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("devices"));
        //FBOpers.getDevices(FirebaseDatabase.getInstance().getReference().child("users").child("openzzggl-gmail-com"));
        //checkDevicesConnection(devices);
        //showList(devices);
    }

    //TODO
    @Override
    public void onListItemClick(ListView list, View v, int position, long id) {

        Intent intent = new Intent(getActivity(), DisplayActivity.class);
        intent.putExtra("Device", devices.get(position));
        intent.putExtra("index", position);
        startActivityForResult(intent,0);
    }
/*
    public void updateList(){
        JsonParser jParser = new JsonParser();
        devices.clear();
        //get file with MySettings
        mSettings = this.getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        if (mSettings.contains(APP_PREFERENCES)) {
            // Get connStrings from the file
            stringFromFile = mSettings.getString(APP_PREFERENCES, stringFromFile);
            //convert plainJson to jsonObject

            JsonObject formStringJson = (JsonObject) jParser.parse(stringFromFile);
            for (JsonElement el: formStringJson.getAsJsonArray("devices")){
                Map<String, String> tempInfo = parser.parseQrWithIotHub(el.toString());
                devices.add(new DeviceObject(tempInfo.get("NotificationHubName"), tempInfo.get("SenderId"), tempInfo.get("NotHubConnectionString"), tempInfo.get("TableName"),
                        tempInfo.get("StorageConnectionString"), tempInfo.get("IotHubConnectionString"), tempInfo.get("DeviceName"), stringFromFile));
            }
        }
    }
*/
    public void updateListFromFirebase(DatabaseReference dbRef){

        ArrayList<String> my_list = new ArrayList<>();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                my_list.clear();
                Log.v("Async101", "Done loading bookmarks");
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String connStr = ds.getValue().toString();
                    my_list.add(connStr);
                    //Log.d("TAG", connStr);
                }
                devices.clear();
                for (String el: my_list){
                    Map<String, String> tempInfo = parser.parseQrWithIotHub(el);
                    devices.add(new DeviceObject(tempInfo.get("NotificationHubName"), tempInfo.get("SenderId"), tempInfo.get("NotHubConnectionString"), tempInfo.get("TableName"),
                            tempInfo.get("StorageConnectionString"), tempInfo.get("IotHubConnectionString"), tempInfo.get("DeviceName"), el));
                }
                Log.d("AH-TAG", "WOOHOO");
                //checkDevicesConnection(devices);
                showList(devices);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void writeToFriebase(DatabaseReference dbRef, String connstr) {
        dbRef.setValue(connstr);
        Log.d("AH-TAG", "Added to DB");
    }

    public void getDevices(DatabaseReference dbRef){

        ArrayList<String> my_list = new ArrayList<>();

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.v("Async101", "Done loading bookmarks");
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String connStr = ds.getKey();
                    my_list.add(connStr);
                    Log.d("TAG", connStr);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void checkDevicesConnection(ArrayList<DeviceObject> deviceList){
        for(DeviceObject dev: deviceList){
            try{
                CheckDevices checkDevices = new CheckDevices();
                checkDevices.execute(dev);
                onlineDevices = checkDevices.get();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        for (DeviceObject device: deviceList){
            if(onlineDevices.contains(device.DeviceName)){  //TODO bad parser
                device.setIsOnline(true);
            }else{
                device.setIsOnline(false);
            }
        }
    }

    public void showList(ArrayList<DeviceObject> deviceList){

        ArrayList<MyMainListItem> arrayToShow = new ArrayList<>();

        for(DeviceObject device: deviceList) {
            if(device.IsOnline){
                arrayToShow.add(new MyMainListItem(R.drawable.green, device.DeviceName));
            }else{
                arrayToShow.add(new MyMainListItem(R.drawable.grey, device.DeviceName));
            }
        }

        MyIndicatorAdapter adapter = new MyIndicatorAdapter(getActivity(), arrayToShow);
        setListAdapter(adapter);
    }

}