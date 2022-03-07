package com.example.screenrecodinglibrary.recever;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.example.screenrecodinglibrary.service.VideoUplodingService;

public class ServiceRestarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Broadcast Listened", "Service tried to stop");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, VideoUplodingService.class));
        } else {
            context.startService(new Intent(context, VideoUplodingService.class));
        }
    }
}