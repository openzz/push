package com.example.ah.push;

/**
 * Created by ah on 7/10/2018.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import com.microsoft.windowsazure.notifications.NotificationsHandler;
import com.google.gson.*;

public class MyHandler extends NotificationsHandler {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context ctx;
    JsonParser parser = new JsonParser();

    @Override
    public void onReceive(Context context, Bundle bundle) {
        ctx = context;
        String nhMessage = bundle.getString("message");
        JsonObject message = parser.parse(nhMessage).getAsJsonObject();

        if(message.get("type").getAsString().equals("info")){
            sendInfo(message.get("body").toString());
        }

        if(message.get("type").getAsString().equals("alert")){
            sendAlert(message.get("body").toString());
            if (DisplayActivity.isVisible) {
                DisplayActivity.displayActivity.ToastNotify(message.get("body").toString());
            }
        }

        if(message.get("type").getAsString().equals("update")){
            //sendUpdate(message.get("body").toString());
            if (DisplayActivity.isVisible) {
                DisplayActivity.displayActivity.UpdateRealTimeVals(nhMessage);
            }
        }
    }

    /*
    @Override
    public void onReceive(Context context, Bundle bundle) {
        ctx = context;
        String nhMessage = bundle.getString("message");
        sendNotification(nhMessage);
        if (DisplayActivity.isVisible) {
            DisplayActivity.displayActivity.ToastNotify(nhMessage);
        }
    }
    */


    private void sendInfo(String msg) {

        Intent intent = new Intent(ctx, DisplayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Info")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setSound(defaultSoundUri)
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void sendUpdate(String msg) {

        Intent intent = new Intent(ctx, DisplayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Notification Hub Demo")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setSound(defaultSoundUri)
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private void sendAlert(String msg) {

        Intent intent = new Intent(ctx, DisplayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Iot hub alert.")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setSound(defaultSoundUri)
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}