package com.example.screenrecodinglibrary;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;

import com.example.screenrecodinglibrary.config.ServiceCall;
import com.example.screenrecodinglibrary.recever.ServiceRestarter;
import com.example.screenrecodinglibrary.service.OnClearFromRecentService;
import com.example.screenrecodinglibrary.service.ScreenRecoderService;

import java.text.DecimalFormat;

public class ScreenRecoder {

    public static void startScreenRecoder(Activity activity, int mScreenWidth, int mScreenHeight, int mScreenDensity, int resultCode, Intent data) {
        Intent service = new Intent(activity, ScreenRecoderService.class);
        service.setAction(ScreenRecoderService.ACTION_START);
        service.putExtra("code", resultCode);
        service.putExtra("data", data);
        service.putExtra("width", mScreenWidth);
        service.putExtra("height", mScreenHeight);
        service.putExtra("density", mScreenDensity);
        activity.startService(service);
    }

    public static boolean getIsBusyRecording(Activity activity) {
        boolean isRecordingIsBusy = false;
        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (ScreenRecoderService.class.getName().equals(service.service.getClassName())) {
                    isRecordingIsBusy = true;
                }
            }
        }
        return isRecordingIsBusy;
    }

    public static void stopScreenRecoder(Activity activity) {
        Intent service = new Intent(activity, ScreenRecoderService.class);
        service.setAction(ScreenRecoderService.ACTION_STOP);
        activity.stopService(service);
    }

    public static void pauseRecoding(Activity activity) {
        Intent service = new Intent(activity, ScreenRecoderService.class);
        service.setAction(ScreenRecoderService.ACTION_PAUSE);
        activity.startService(service);
    }

    public static void resumeRecoding(Activity activity) {
        Intent service = new Intent(activity, ScreenRecoderService.class);
        service.setAction(ScreenRecoderService.ACTION_RESUME);
        activity.startService(service);
    }

    public static String getFileSize(long size) {
        String hrSize = null;

        double b = size;
        double k = size * 1024.0;
        double m = ((size * 1024.0) * 1024.0);

        DecimalFormat dec = new DecimalFormat("");
        hrSize = dec.format(m);
        return hrSize;
    }

    public static void onPause(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            activity.startService(new Intent(activity, OnClearFromRecentService.class));
            if (getIsBusyRecording(activity)) {
                pauseRecoding(activity);
            }
        } else {
            if (getIsBusyRecording(activity)) {
                stopScreenRecoder(activity);

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("restartservice");
                broadcastIntent.setClass(activity, ServiceRestarter.class);
                activity.sendBroadcast(broadcastIntent);
            }
        }
    }

    public static void onDestroy(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            activity.startService(new Intent(activity, OnClearFromRecentService.class));
            if (getIsBusyRecording(activity)) {
                pauseRecoding(activity);
            }
        } else {
            if (getIsBusyRecording(activity)) {
                stopScreenRecoder(activity);

                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("restartservice");
                broadcastIntent.setClass(activity, ServiceRestarter.class);
                activity.sendBroadcast(broadcastIntent);
            }
        }
    }
}
