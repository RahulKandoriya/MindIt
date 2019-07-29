package com.abotstudio.apps.rememberanything.mindit;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DismissReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        Log.i("id2",String.valueOf(intent.getIntExtra("notification_id", 1)));
        notificationManager.cancel(intent.getIntExtra("notification_id", 1));

    }
}
