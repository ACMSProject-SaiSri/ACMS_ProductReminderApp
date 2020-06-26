package com.e.reminder;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;


import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
public class AlarmReceiver  extends BroadcastReceiver{

    private String TAG = "notification";
    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("in receiver class");
        String name=intent.getExtras().getString("item_name");
        Intent notificationIntent = new Intent(context, NotificationActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,100,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        String CHANNEL_ID="MYCHANNEL";
        Log.d(TAG, "databases content"+ " in receiver");

        NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"name",NotificationManager.IMPORTANCE_DEFAULT);

        Notification notification=new Notification.Builder(context.getApplicationContext(),CHANNEL_ID)
                .setContentText("Your Product -"+name+"..expires today")
                .setContentTitle("My Reminder App")
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.sym_action_chat,"go to app",pendingIntent)
                .setChannelId(CHANNEL_ID)
                .setSmallIcon(android.R.drawable.sym_action_chat)
                .setCategory(Notification.CATEGORY_REMINDER)
                .setAutoCancel(true)
                .build();







        NotificationManager notificationManager=(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(100,notification);

    }
}