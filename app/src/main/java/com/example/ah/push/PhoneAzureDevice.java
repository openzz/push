package com.example.ah.push;


import android.os.SystemClock;

import com.google.gson.Gson;
import com.microsoft.azure.sdk.iot.device.DeviceClient;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.Device;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.DeviceMethodData;
import com.microsoft.azure.sdk.iot.device.DeviceTwin.Property;
import com.microsoft.azure.sdk.iot.device.IotHubClientProtocol;
import com.microsoft.azure.sdk.iot.device.IotHubEventCallback;
import com.microsoft.azure.sdk.iot.device.IotHubMessageResult;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;
import com.microsoft.azure.sdk.iot.device.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class PhoneAzureDevice {

    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss,SSS");

    private static class Board{
        public String boardid;
        public Sensor sensors[];

    }

    private static class SystemProp{
        public String contentType;
        public String contentEncoding;
        public String time;

        public SystemProp(String time){
            this.time = time;
            this.contentType = "application/json";
            this.contentEncoding = "utf-8";
        }
    }

    private  static class Mes{
        public SystemProp systemProperties;
        public Board body;
    }

    private static class Sensor{
        public String sensorid;
        public String value;
    }
    // Specify the telemetry to send to your IoT hub.
    private static class TelemetryDataPoint {
        public Mes message;

        // Serialize object to JSON format.
        public String serialize() {
            Gson gson = new Gson();
            return gson.toJson(this);
        }
    }
    // Serialize object to JSON format.
    public static String serialize(Board b) {
        Gson gson = new Gson();
        return gson.toJson(b);
    }



    // Print the acknowledgement received from IoT Hub for the telemetry message sent.
    private static class EventCallback implements IotHubEventCallback {
        public void execute(IotHubStatusCode status, Object context) {
            System.out.println("IoT Hub responded to message with status: " + status.name());

            if (context != null) {
                synchronized (context) {
                    context.notify();
                }
            }
        }
    }

    public static class MessageSender implements Runnable {
        String boardId;
        String sensorId;
        float [] value;
        DeviceClient client;

        public MessageSender(DeviceClient client, String boardId, String sensorId){
            this.client = client;
            this.boardId = boardId;
            this.sensorId = sensorId;
        }

        public void setValue(float [] values) {
            this.value = values;
        }

        public float [] getValue() {
            return value;
        }

        public String getBoardId() {
            return boardId;
        }

        public String getSensorId() {
            return sensorId;
        }

        public void run() {
            try {
                    // Simulate telemetry.

                String time = dateFormat.format(Calendar.getInstance().getTime());

                Board board = new Board();
                board.boardid = boardId;
                board.sensors = new Sensor[value.length];

                for(int i = 0; i<value.length; i++){
                    board.sensors[i] = new Sensor();
                    board.sensors[i].sensorid = sensorId+" "+i;
                    board.sensors[i].value = String.valueOf(value[i]);
                }

                Mes mes = new Mes();
                mes.systemProperties = new SystemProp(time);

                TelemetryDataPoint telemetryDataPoint = new TelemetryDataPoint();
                telemetryDataPoint.message = mes;
                telemetryDataPoint.message.body = board;

                String msgStr = telemetryDataPoint.serialize();

                byte messageBytes[] = msgStr.getBytes(Charset.forName("UTF-8"));
                Message msg = new Message(messageBytes);

                Object lockobj = new Object();

                EventCallback callback = new EventCallback();
                client.sendEventAsync(msg, callback, lockobj);

                 synchronized (lockobj) {
                     lockobj.wait();
                 }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException, URISyntaxException {



        // Create new thread and start sending messages

    }
}
