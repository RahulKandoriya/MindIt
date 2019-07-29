package com.abotstudio.apps.rememberanything.mindit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class PeriodicTask extends Worker {

    private final String CHANNEL_ID = "mindit_code";
    private Context mContext;



    Data data = getInputData();
    public String title = data.getString("post_title");
    public String content = data.getString("post_content");
    public int NOTIFICATION_ID = data.getInt("post_id", 1) ;
    public int r;


    public PeriodicTask(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public Result doWork() {

        try {

            mContext = getApplicationContext();


            displayNotification();
            return Result.success();


        } catch (Throwable throwable) {

            return Result.failure();
        }
    }

    private void buildNotificationChannel()
    {
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O)
        {
            CharSequence name="Quotes Notifications";
            String description="Contains all Quotes notification";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,name, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(description);
            NotificationManager notificationManager=(NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            try {
                notificationManager.createNotificationChannel(notificationChannel);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

        }
    }

    public void displayNotification(){

        PendingIntent pendingActionIntent, pendingDismissIntent;
        Intent actionIntent, backIntent, dismissIntent;

        buildNotificationChannel();

        actionIntent = new Intent(mContext, PostDetailActivity.class);
        backIntent = new Intent(mContext, MainActivity.class);
        actionIntent.putExtra("title_post", title);
        actionIntent.putExtra("content_post", content);


        pendingActionIntent = PendingIntent.getActivities(mContext, NOTIFICATION_ID, new Intent[] {backIntent, actionIntent}, PendingIntent.FLAG_UPDATE_CURRENT);



        dismissIntent = new Intent(mContext, DismissReceiver.class);
        dismissIntent.putExtra("notification_id", NOTIFICATION_ID);

        pendingDismissIntent = PendingIntent.getBroadcast(mContext, NOTIFICATION_ID, dismissIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        nBuilder.setSmallIcon(R.drawable.ic_repeat_black_24dp);
        nBuilder.setDefaults(Notification.DEFAULT_ALL);
        nBuilder.setContentTitle(title);



        if (content.length() > 500 ) {

            r = (int) (Math.random() * (content.length()-300));
            nBuilder.setContentText(content.substring(0,200));
            nBuilder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText("\"" + content.substring(r,r+300)  + "\""));
        } else {
            nBuilder.setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(content));
            nBuilder.setContentText(content);
        }
        nBuilder.setContentIntent(pendingActionIntent);
        nBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        nBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);

        nBuilder.setOngoing(true);
        nBuilder.addAction(R.drawable.ic_close_black_24dp, "DISMISS", pendingDismissIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(NOTIFICATION_ID,nBuilder.build());

    }



}
