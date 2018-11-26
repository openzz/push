package com.example.ah.push;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.microsoft.windowsazure.notifications.NotificationsManager;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.google.gson.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;


public class DisplayPhoneActivity extends AppCompatActivity implements View.OnClickListener {

    public static DisplayPhoneActivity displayPhoneActivity;
    public static Boolean isVisible = false;
    private static final String TAG = "DisplayPhoneActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    ArrayList<DataModel> dataModels;
    ArrayList<DataModelSens> checkListItems;
    ArrayList<String> checkListItems1;
    ListView listView;
    ListView listPush;
    private static CustomAdapter adapter;
    private static CustomAdapterSens adapterSens;
    private static ArrayAdapter<String> adapterArr;
    DeviceObject parcelDevice;

    Calendar dateAndTime = Calendar.getInstance();
    TextView fromDate;
    TextView toDate;

    Button deleteButton;
    Button buttonApply;

    Intent intent;
    String connString;
    String tableName;

    GraphView graph;

    Boolean chklistTest;

    HashMap<String, HashMap<String, String>> fetchedData;

    MyParser parser = new MyParser();

    SimpleDateFormat updateTimeFormatter = new SimpleDateFormat("HH:mm:ss");

    public DataPoint[] mapToDp (Map map){
        DataPoint[] result = new DataPoint[map.size()];
        int i = 0;
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss,SSS");
        //format.setTimeZone(TimeZone.getTimeZone("GMT+02:00"));
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry el = (Map.Entry)it.next();
            DataPoint dp = null;
            try {
                Date date = format.parse((String)el.getKey());
                dp = new DataPoint(date, Double.parseDouble((String)el.getValue()));
            }catch (Exception e){
                e.printStackTrace();
            }
            result[i] = dp;
            i++;
        }
        for (int k = result.length-1; i>0; i--){
            for (int j = 0; j<k; j++){
                if(result[j].getX()>result[j+1].getX()){
                    DataPoint tmp = result[j];
                    result[j] = result[j+1];
                    result[j+1] = tmp;
                }
            }
        }
        return result;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported by Google Play Services.");
                ToastNotify("This device is not supported by Google Play Services.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void registerWithNotificationHubs()
    {
        if (checkPlayServices()) {
            // Start IntentService to register this application with FCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        registerWithNotificationHubs();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_phone);

        chklistTest = false;

        intent = getIntent();
        parcelDevice = intent.getParcelableExtra("Device");

        fromDate = (TextView)findViewById(R.id.fromDate);
        toDate = (TextView)findViewById(R.id.toDate);

        deleteButton = (Button)findViewById(R.id.measureButton);
        buttonApply = (Button)findViewById(R.id.buttonApply);

        deleteButton.setOnClickListener(this);
        buttonApply.setOnClickListener(this);

        setInitialFromDateTime();
        setInitialToDateTime();

        graph = (GraphView) findViewById(R.id.graph);
        listView=(ListView)findViewById(R.id.list);

        //String indexToDelete = intent.getStringExtra("index");
        //Map<String, String> deviceInfo = parser.parseQr(connString);

        listView=(ListView)findViewById(R.id.list);
        //listPush = (ListView)findViewById(R.id.expandable);
        setListViewHeightBasedOnChildren(listView);

        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        NotificationSettings.SenderId = parcelDevice.getSenderId();//deviceInfo.get("SenderId");
        NotificationSettings.HubName = parcelDevice.getNotificationHubName();//deviceInfo.get("NotificationHubName");
        NotificationSettings.HubListenConnectionString = parcelDevice.getNotHubConnectionString();//deviceInfo.get("NotHubConnectionString");

        connString = parcelDevice.getStorageConnectionString();
        tableName = parcelDevice.getTableName();

        displayPhoneActivity = this;
        NotificationsManager.handleNotifications(this, NotificationSettings.SenderId, MyHandler.class);
        registerWithNotificationHubs();



        TextView device = (TextView)findViewById(R.id.device);
        device.setText(parcelDevice.getNotificationHubName());
        //deviceInfo.get("NotificationHubName"));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.measureButton:{
                Intent intent = new Intent(DisplayPhoneActivity.this, SensorsActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.buttonApply:{
                FetchTable fetchTask = new FetchTable();

                try {
                    fetchedData = null;
                    graph.removeAllSeries();
                }catch (Exception e){
                    e.printStackTrace();
                }

                String fromString = fromDate.getText().toString();
                String toString = toDate.getText().toString();

                Date dateFrom = null;
                Date dateTo = null;

                SimpleDateFormat formatter = new SimpleDateFormat();

                try {
                    formatter.applyPattern("dd MMMM yyyy");
                    dateFrom = formatter.parse(fromString);
                    dateTo = formatter.parse(toString);
                }catch (Exception e){
                    e.printStackTrace();
                }

                try {
                    formatter.applyPattern("MMMM dd, yyyy");
                    dateFrom = formatter.parse(fromString);
                    dateTo = formatter.parse(toString);
                }catch (Exception e){
                    e.printStackTrace();
                }


                formatter.applyPattern("dd-MM-yyyy HH:mm:ss,SSS");
                fromString = formatter.format(dateFrom);
                toString = formatter.format(dateTo);
                toString = toString.substring(0,11) + "23:59:59,999";


                fetchTask.execute(connString, tableName, fromString, toString, "0");
                //Back
                //fetchTask.execute(connString,"12-07-2018 17:51:31,830", "12-07-2018 17:51:31,830", "0");

                try {
                    fetchedData = new HashMap<String,HashMap<String, String>>();
                    fetchedData = fetchTask.get();
                    //graphData = new HashMap[fetchedData.size()];
                }catch (ExecutionException e){
                    e.printStackTrace();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }

                checkList(fetchedData); //TODO checkList
                setListViewHeightBasedOnChildren(listView);
            }
        }
    }

    public void setFromDate(View v){
        new DatePickerDialog(DisplayPhoneActivity.this, fd, dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH), dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void setToDate(View v){
        new DatePickerDialog(DisplayPhoneActivity.this, td, dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH), dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void setTime(View v) {
        new TimePickerDialog(DisplayPhoneActivity.this, t, dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true).show();
    }

    private void setInitialFromDateTime(){
        fromDate.setText(DateUtils.formatDateTime(this, dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE| DateUtils.FORMAT_SHOW_YEAR));
    }

    private void setInitialToDateTime(){

        toDate.setText(DateUtils.formatDateTime(this, dateAndTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE| DateUtils.FORMAT_SHOW_YEAR));
    }

    DatePickerDialog.OnDateSetListener fd = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialFromDateTime();
        }
    };

    DatePickerDialog.OnDateSetListener td = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            dateAndTime.set(Calendar.YEAR, year);
            dateAndTime.set(Calendar.MONTH, monthOfYear);
            dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialToDateTime();
        }
    };

    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            //setInitialFromDateTime();
            //setInitialToDateTime();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        isVisible = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
        chklistTest = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
        chklistTest = false;
    }

    public void ToastNotify(final String notificationMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(DisplayPhoneActivity.this, notificationMessage, Toast.LENGTH_LONG).show();
                //TextView helloText = (TextView) findViewById(R.id.text_hello);
                //helloText.setText(notificationMessage);
            }
        });
    }


    public void UpdateRealTimeVals(final String notificationMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                try {
                    String inJson = notificationMessage;
                    Random rand = new Random();

                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    IncomingMessage msg = gson.fromJson(inJson, IncomingMessage.class);

                    if(((msg.body.boardid.equals("0")) & (NotificationSettings.HubName.equals("AndroidPush-AH")))|((msg.body.boardid.equals("1")) & (NotificationSettings.HubName.equals("AnotherNotHub")))) {
                        TextView lastUpdate = (TextView) findViewById(R.id.lastUpdateTime);
                        Date updateDate = Calendar.getInstance().getTime();
                        String updateTime = updateTimeFormatter.format(updateDate);
                        lastUpdate.setText(updateTime);

                        checkListItems = new ArrayList<>();

                        for (IncomingMessage.Board.Sensor sensor : msg.body.sensors) {
                            int col = Color.rgb(rand.nextInt(180), rand.nextInt(180), rand.nextInt(180));
                            checkListItems.add(new DataModelSens(sensor.sensorid, sensor.value, col));
                        }

                        adapterSens = new CustomAdapterSens(checkListItems, getApplicationContext());
                        if(chklistTest){
                            listView.setAdapter(adapterSens);
                        }
                        //setListViewHeightBasedOnChildren(listPush);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


    public void checkList(final HashMap<String, HashMap<String, String>> sensors) {

        chklistTest = true;

        try {
            checkListItems = new ArrayList<>();
            checkListItems1 = new ArrayList<>();
            Random rand = new Random();

            if (sensors.size() == 0){
                adapterSens.clear();
            }else {


                for (Map.Entry<String, HashMap<String, String>> entry : sensors.entrySet()) {
                    if (!checkListItems.contains(entry.getKey())) {
                        int col = Color.rgb(rand.nextInt(120), rand.nextInt(120), rand.nextInt(120));
                        checkListItems.add(new DataModelSens(entry.getKey(), null, col));
                        checkListItems1.add(entry.getKey());
                        //entry.getKey().toString());
                    }
                }
            }

            adapterSens = new CustomAdapterSens(checkListItems, this );
            //CustomAdapterSens(checkListItems, getApplicationContext());
            listView.setAdapter(adapterSens);

        }catch (Exception e){
            e.printStackTrace();
        }

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SparseBooleanArray sp = listView.getCheckedItemPositions();

                ArrayList<String> selectedItems = new ArrayList<String>();

                for (int i = 0; i < checkListItems.size(); i++) {
                    if (sp.get(i)) {
                        selectedItems.add(checkListItems.get(i).getSensor());
                    }
                }


                DataPoint[][] series = new DataPoint[selectedItems.size()][];
                graph.removeAllSeries();

                int k = 0;
                for (Map.Entry<String, HashMap<String, String>> entry : fetchedData.entrySet()) {
                    if(selectedItems.contains(entry.getKey())) {
                        //series[i] = mapToDp(entry.getValue());
                        DataPoint[] x = mapToDp(entry.getValue());
                        int color = checkListItems.get(k).getColor();

                        LineGraphSeries<DataPoint> spline = new LineGraphSeries<>(x);
                        PointsGraphSeries<DataPoint> dots = new PointsGraphSeries<>(x);

                        spline.setColor(color);
                        dots.setColor(color);

                        if (x.length > 1) {
                            graph.addSeries(spline);
                            graph.addSeries(dots);
                            dots.setShape(PointsGraphSeries.Shape.POINT);
                            graph.getViewport().setXAxisBoundsManual(true);
                            graph.getViewport().setMaxX(x[x.length - 1].getX());
                            if (x.length > 2){
                                graph.getViewport().setMinX(x[(x.length-1) - x.length/3].getX());
                            }
                            if(x.length == 2){
                                graph.getViewport().setMinX(x[0].getX());
                            }
                            graph.getViewport().setScalable(true);
                        } else {
                            graph.addSeries(dots);
                            graph.getViewport().setXAxisBoundsManual(false);
                        }
                    }
                    graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(DisplayPhoneActivity.this));
                    graph.getGridLabelRenderer().setNumHorizontalLabels(2);
                    k++;
                }
            }
        });
    }
}
