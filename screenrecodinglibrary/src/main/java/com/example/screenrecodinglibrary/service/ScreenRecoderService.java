package com.example.screenrecodinglibrary.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.screenrecodinglibrary.R;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class ScreenRecoderService extends Service {
    private static final String TAG = ScreenRecoderService.class.getSimpleName();
    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;
    private int mResultCode;
    private Intent mResultData;

    private MediaProjection mMediaProjection;
    private static MediaRecorder mMediaRecorder;
    private VirtualDisplay mVirtualDisplay;

    public static final String ACTION_STOP = "ACTION_STOP";
    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String ACTION_RESUME = "ACTION_RESUME";

    public static String filePath = "";
    public String fileSize = "";

    public ScreenRecoderService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service onCreate() is called");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Command Started", "Command Started");
        if (intent != null) {
            if (intent.getAction().equals(ACTION_START)) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    String channelId = "001";
                    String channelName = "myChannel";
                    NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE);
                    channel.setLightColor(Color.BLUE);
                    channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    Intent stopnotificationIntent = new Intent(this, ScreenRecoderService.class);
                    stopnotificationIntent.setAction(ACTION_STOP);
                    PendingIntent Intent = PendingIntent.getService(this, 0, stopnotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//          .addAction(android.R.drawable.ic_media_pause, "Stop Recording", Intent)
                    if (manager != null) {
                        manager.createNotificationChannel(channel);
                        Notification notification;

                        notification = new Notification.
                                Builder(getApplicationContext(), channelId)
                                .setOngoing(true)
                                .setContentTitle(getString(R.string.app_name))
                                .setContentText("Recording Start")
//                                .setSmallIcon(R.drawable.ic_launcher_round)
                                .setCategory(Notification.CATEGORY_SERVICE)
                                .build();

                        startForeground(101, notification);
                    }
                } else {
                    startForeground(101, new Notification());
                }

                mResultCode = intent.getIntExtra("code", -1);
                mResultData = intent.getParcelableExtra("data");
                mScreenWidth = intent.getIntExtra("width", 720);
                mScreenHeight = intent.getIntExtra("height", 1280);
                mScreenDensity = intent.getIntExtra("density" , 1);
                fileSize = intent.getStringExtra("fileSize");
                mMediaProjection = createMediaProjection();
                mMediaRecorder = createMediaRecorder();
                mVirtualDisplay = createVirtualDisplay();
                mMediaRecorder.start();

            } else if (intent.getAction().equals(ACTION_STOP)) {
                Toast.makeText(getApplicationContext(), "STOP SERVICE", Toast.LENGTH_SHORT).show();
                onDestroy();
            } else if (intent.getAction().equals(ACTION_PAUSE)) {
                mMediaRecorder.pause();
            } else if (intent.getAction().equals(ACTION_RESUME)) {
                mMediaRecorder.resume();
            }
        }
        return Service.START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private MediaProjection createMediaProjection() {
        Log.i(TAG, "Create MediaProjection");
        // delay needed because getMediaProjection() throws an error if it's called too soon

        return ((MediaProjectionManager) Objects.requireNonNull(getSystemService(Context.MEDIA_PROJECTION_SERVICE))).getMediaProjection(mResultCode, mResultData);

    }

    private MediaRecorder createMediaRecorder() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault());
        Date curDate = new Date(System.currentTimeMillis());
        String curTime = formatter.format(curDate).replace(" ", "");
        String videoQuality = "HD";

        Log.i(TAG, "Create MediaRecorder");
        MediaRecorder mediaRecorder = new MediaRecorder();
//
//        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
//
//        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//
//        mediaRecorder.setVideoEncodingBitRate(512 * 1000);
//
//        //mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
//
//        mediaRecorder.setVideoSize(720, 1520);
//        mediaRecorder.setVideoFrameRate(30);
//        mediaRecorder.setOutputFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/DemoRecoder" + videoQuality + curTime + ".mp4");
//
//        try {
//            mediaRecorder.prepare();
//        } catch (Exception e) {
//            e.printStackTrace();
//
//        }

//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

//        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);  //after setOutputFormat()
        mediaRecorder.setVideoSize(720, 1520);  //after setVideoSource(), setOutFormat()
        mediaRecorder.setVideoFrameRate(60); //after setVideoSource(), setOutFormat()
        int bitRate;
        bitRate = 5 * mScreenWidth * mScreenHeight / 1000;
        mediaRecorder.setMaxFileSize(100000000);

//        File file = null;
//        try {
//            String rootPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
//                    .getAbsolutePath() + "/ZIPYRecoder/";
//            File root = new File(rootPath);
//            if (!root.exists()) {
//                root.mkdirs();
//            }
//            file = new File(rootPath + "ZIPYRecoder" + videoQuality + curTime + ".mp4");
//            if (file.exists()) {
//                file.delete();
//            }
//            file.createNewFile();
//
//            FileOutputStream out = new FileOutputStream(file);
//
//            out.flush();
//            out.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        mediaRecorder.setOutputFile(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/DemoRecoder" + videoQuality + curTime + ".mp4");

//        mediaRecorder.setOutputFile(String.valueOf(file));

        mediaRecorder.setVideoEncodingBitRate(5 * mScreenWidth * mScreenHeight);

        filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/DemoRecoder" + videoQuality + curTime + ".mp4";

//        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);  //after setOutputFormat()

        if (StringUtils.isNotEmpty(fileSize)) {
            mediaRecorder.setMaxFileSize(Long.parseLong(fileSize.replace(",", "")));
            mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mediaRecorder, int what, int extra) {
                    if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
                        Toast.makeText(getApplicationContext(), "Recording Completed", Toast.LENGTH_SHORT).show();
//                        if (ZipyaiReactNativeStagingModule.countDownTimer != null) {
//                            ZipyaiReactNativeStagingModule.countDownTimer.cancel();
//                        }
//                        ZipyaiReactNativeStagingModule.callback.invoke(true);
                        onDestroy();
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException | IOException e) {
            Log.e(TAG, "createMediaRecorder: e = " + e.toString());
        }

        return mediaRecorder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private VirtualDisplay createVirtualDisplay() {
        Log.i(TAG, "Create VirtualDisplay");
        return mMediaProjection.createVirtualDisplay(TAG,
                720,
                1520,
                mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mMediaRecorder.getSurface(),
                null,
                null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "Service onDestroy");
        stopForeground(true);
//      sendMessageToActivity(filePath);
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            mMediaProjection.stop();
            mMediaRecorder.reset();
        }
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

//    private void sendMessageToActivity(String videoUri) {
//        Intent intent = new Intent("MessageSendActivity");
//        Bundle bundle = new Bundle();
//        bundle.putString("videoUri", videoUri);
//        intent.putExtras(bundle);
//        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
//    }

}
