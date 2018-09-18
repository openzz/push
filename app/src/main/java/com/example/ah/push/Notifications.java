package com.example.ah.push;

import java.util.HashSet;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import android.view.View;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.microsoft.windowsazure.messaging.NotificationHub;

public class Notifications {
    private static final String PREFS_NAME = "BreakingNewsCategories";
    private GoogleCloudMessaging gcm;
    private NotificationHub hub;
    private Context context;
    private String senderId;

    public Notifications(Context context, String senderId, String hubName,
                         String listenConnectionString) {
        this.context = context;
        this.senderId = senderId;

        gcm = GoogleCloudMessaging.getInstance(context);
        hub = new NotificationHub(hubName, listenConnectionString, context);
    }

    public void storeCategoriesAndSubscribe(Set<String> categories)
    {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        settings.edit().putStringSet("categories", categories).commit();
        subscribeToCategories(categories);
    }

    public Set<String> retrieveCategories() {
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        return settings.getStringSet("categories", new HashSet<String>());
    }

    public void subscribeToCategories(final Set<String> categories) {
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object... params) {
                try {
                    String regid = gcm.register(senderId);

                    String templateBodyGCM = "{\"data\":{\"message\":\"$(messageParam)\"}}";

                    hub.registerTemplate(regid,"simpleGCMTemplate", templateBodyGCM,
                            categories.toArray(new String[categories.size()]));
                } catch (Exception e) {
                    Log.e("MainActivity", "Failed to register - " + e.getMessage());
                    return e;
                }
                return null;
            }

            protected void onPostExecute(Object result) {
                String message = "Subscribed for categories: "
                        + categories.toString();
                Toast.makeText(context, message,
                        Toast.LENGTH_LONG).show();
            }
        }.execute(null, null, null);
    }

}