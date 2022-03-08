package com.example.screenrecodinglibrary;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.screenrecodinglibrary.config.ServiceCall;
import com.example.screenrecodinglibrary.recever.ServiceRestarter;
import com.example.screenrecodinglibrary.service.OnClearFromRecentService;
import com.example.screenrecodinglibrary.service.ScreenRecoderService;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.text.DecimalFormat;
import java.util.List;

public class ScreenRecoderModule {

    public static int mScreenWidth;
    public static int mScreenHeight;
    public static int mScreenDensity;
    private static final int REQUEST_CODE = 1000;


    public static void showToat(Activity activity){
        Toast.makeText(activity, "HERE", Toast.LENGTH_SHORT).show();
    }

    public static void startScreenRecoder(Activity activity) {
        Dexter.withActivity(activity).withPermissions(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(new MultiplePermissionsListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (report.areAllPermissionsGranted()) {
                    DisplayMetrics metrics = new DisplayMetrics();
                    activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                    mScreenWidth = metrics.widthPixels;
                    mScreenHeight = metrics.heightPixels;
                    mScreenDensity = metrics.densityDpi;

                    startMediaProjectionApi(activity);

                } else {

                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private static void startMediaProjectionApi(Activity activity) {

        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) activity.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent permissionIntent = mediaProjectionManager != null ? mediaProjectionManager.createScreenCaptureIntent() : null;
        activity.startActivityForResult(permissionIntent, REQUEST_CODE);
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

    public static void activityResult(int requestCode, int resultCode, Intent data, Activity activity){
        if(requestCode == REQUEST_CODE){
            Intent service = new Intent(activity, ScreenRecoderService.class);
            service.setAction(ScreenRecoderService.ACTION_START);
            service.putExtra("code", resultCode);
            service.putExtra("data", data);
            service.putExtra("width", mScreenWidth);
            service.putExtra("height", mScreenHeight);
            service.putExtra("density", mScreenDensity);
            activity.startService(service);
        }else {
            Toast.makeText(activity, "User Cancel the request", Toast.LENGTH_SHORT).show();
        }

    }
}
