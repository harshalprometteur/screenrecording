package com.example.screenrecodinglibrary.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.screenrecodinglibrary.recever.ServiceRestarter;

public class OnClearFromRecentService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("MESSAGE=======>", "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("MESSAGE=======>", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("MESSAGE=======>", "END");
        //Code here
//        if (ZipyaiReactNativeStagingModule.countDownTimer != null) {
//            ZipyaiReactNativeStagingModule.countDownTimer.cancel();
//        }
        Intent service = new Intent(getApplicationContext(), ScreenRecoderService.class);
        service.setAction(ScreenRecoderService.ACTION_STOP);
        stopService(service);

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(getApplicationContext() , ServiceRestarter.class);
        getApplicationContext().sendBroadcast(broadcastIntent);

        stopSelf();
    }
}
