package com.example.screenrecodinglibrary.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.screenrecodinglibrary.config.Constant;
import com.example.screenrecodinglibrary.config.ServiceCall;
import com.example.screenrecodinglibrary.config.ServiceCallListner;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class VideoUplodingService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
//            startMyOwnForeground();
//        else
//            startForeground(1, new Notification());
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();

        startForeground(2, notification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Constant.setupRetrofit();
        uploadVideos();
        return START_STICKY;
    }

    private void uploadVideos() {
        MediaType mediaType = MediaType.parse("multipart/from-data");
        MultipartBody.Part profile_pic = null;
        RequestBody pro_pic = null;

        if (StringUtils.isNotEmpty(ScreenRecoderService.filePath)) {
            File file = new File(ScreenRecoderService.filePath);
            pro_pic = RequestBody.create(mediaType, file);
            profile_pic = MultipartBody.Part.createFormData("file", file.getName(), pro_pic);
        }

        new ServiceCall(getApplicationContext(), Constant.getApi().addVideo(
                profile_pic
        ), new ServiceCallListner() {
            @Override
            public void onSuccess(JSONObject jsonObject, String response_msg) {
                Log.e("MESSAGE","Video Uploding Successfully");
            }

            @Override
            public void onFailuer(JSONObject jsonObject, String response_msg) {
                Log.e("MESSAGE","Video Not Uploded");
            }

            @Override
            public void showProgress() {

            }

            @Override
            public void hideProgress() {
                stopSelf();
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
