package com.example.vet.clases;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.vet.R;

import java.util.Random;

public class Utils {
    public static final String TAG = "Vetlog";

    public static void showNoti(Context context, String title, String body){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "campains");
        builder.setSmallIcon(R.drawable.vetgword);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.bigText(title);
        bigTextStyle.setBigContentTitle(title);
        bigTextStyle.setSummaryText(title);

        builder.setStyle(bigTextStyle);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelId = "campains.id";
            NotificationChannel channel = new NotificationChannel(channelId, "Vetlog Channel",
                    NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            builder.setChannelId(channelId);
        }
        manager.notify(new Random().nextInt(),builder.build());
    }
}
