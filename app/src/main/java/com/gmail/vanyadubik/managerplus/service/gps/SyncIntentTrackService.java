package com.gmail.vanyadubik.managerplus.service.gps;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.activity.StartActivity;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.model.APIError;
import com.gmail.vanyadubik.managerplus.model.db.LocationPoint;
import com.gmail.vanyadubik.managerplus.model.json.LocationPointDTO;
import com.gmail.vanyadubik.managerplus.model.json.UploadTrackListRequest;
import com.gmail.vanyadubik.managerplus.model.json.UploadTrackListResponse;
import com.gmail.vanyadubik.managerplus.model.json.UploadTrackListResultDTO;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.service.SyncService;
import com.gmail.vanyadubik.managerplus.task.SyncServiceFactory;
import com.gmail.vanyadubik.managerplus.utils.ErrorUtils;
import com.gmail.vanyadubik.managerplus.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;

import static com.gmail.vanyadubik.managerplus.common.Consts.DEFAULT_NOTIFICATION_GPS_TRACER_ID;
import static com.gmail.vanyadubik.managerplus.common.Consts.DEFAULT_NOTIFICATION_SYNC_TRACER_ID;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG_SYNC_TRACK;
import static com.gmail.vanyadubik.managerplus.utils.Db2JsonModelConverter.convertLocationPoint;

public class SyncIntentTrackService extends IntentService{
    public static final String MIN_COUNT = "minCount";

    @Inject
    DataRepository dataRepository;
    @Inject
    NetworkUtils networkUtils;
    @Inject
    ErrorUtils errorUtils;

    private Context mContext;
    private SyncService syncService;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    public SyncIntentTrackService() {
        super(SyncIntentTrackService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((ManagerPlusAplication) getApplication()).getComponent().inject(this);

        mContext = getApplicationContext();

        mNotificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);

        startNotification();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int minCount = 0;
        Bundle extras = intent.getExtras();
        if (extras != null && !extras.isEmpty()) {
            minCount = (int) extras.getLong(MIN_COUNT);
        }

        if (!networkUtils.checkEthernet()) {
            return;
        }

        syncService = SyncServiceFactory.createService(
                SyncService.class,
                this.getBaseContext());

        List<LocationPoint> trackList = null;
        if(minCount > 0){
            trackList = dataRepository.getUloadedLocationTrack(minCount);
        }else {
            trackList = dataRepository.getUloadedLocationTrack();
        }

        if (trackList != null) {
            final UploadTrackListRequest request = new UploadTrackListRequest(getUploadData(trackList));
            sendNotification(mContext.getString(R.string.sync_upload), 50, false, false);
            try {
                Response<UploadTrackListResponse> uploadResponse = syncService.uploadTrackListOnly(request).execute();
                if (uploadResponse.isSuccessful()) {
                    Log.i(TAGLOG_SYNC_TRACK, getResources().getString(R.string.sync_success));
                    UploadTrackListResponse response = uploadResponse.body();
                    if (response.getResultDTO() != null) {
                        sendNotification(mContext
                                .getString(R.string.sync_processing), 75, false, true);
                        updateDb(response.getResultDTO());
                    }
                    if (response.getInfo() != null) {
                        Log.i(TAGLOG_SYNC_TRACK, response.getInfo());
                    }
                } else {
                    APIError error = errorUtils.parseErrorCode(uploadResponse.code());
                    Log.e(TAGLOG_SYNC_TRACK, error.getMessage());
                    sendNotification(mContext
                            .getString(R.string.sync_tracklist_upload_error), 50, true, true);
                    return;
                }
            } catch (Exception exception) {
                APIError error = errorUtils.parseErrorMessage(exception);
                sendNotification(mContext
                        .getString(R.string.sync_tracklist_upload_error), 50, true, true);
                Log.e(TAGLOG_SYNC_TRACK, error.getMessage());
                return;
            }
        }

        sendNotification(mContext
                .getString(R.string.sync_success), 100, false, true);

    }

    private List<LocationPointDTO> getUploadData(List<LocationPoint> trackList) {

        List<LocationPointDTO> result = new ArrayList<>();

        for (LocationPoint locationPoint : trackList) {

            result.add(convertLocationPoint(locationPoint));

        }
        return result;
    }

    private void updateDb(UploadTrackListResultDTO response) {

        dataRepository.SetTrackListUloadedLocationTrack(response.getDateStart(),
                response.getDateEnd());

    }

    //Send custom notification
    public void startNotification() {

        Intent notificationIntent = new Intent(this, StartActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentIntent(contentIntent)
                .setOngoing(true)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setProgress(100, 0, false)
                .setSmallIcon(R.drawable.ic_sync_success)
                .setContentTitle(mContext.getString(R.string.app_name) +" |"+
                        mContext.getString(R.string.sync_tracklist))
                .setContentText(mContext.getString(R.string.sync_processing))
                .setWhen(System.currentTimeMillis());

        Notification notification;
        if (Build.VERSION.SDK_INT<=15) {
            notification = mBuilder.getNotification(); // API-15 and lower
        }else{
            notification = mBuilder.build();
        }

        startForeground(DEFAULT_NOTIFICATION_SYNC_TRACER_ID, notification);
    }

    public void sendNotification(String text, int proggress, boolean error, boolean close) {

        mBuilder.setContentText(text);
        mBuilder.setProgress(100, proggress, false);

        if(error){
            mBuilder.setSmallIcon(R.drawable.ic_sync_error);
        }

        Notification notification;
        if (Build.VERSION.SDK_INT<=15) {
            notification = mBuilder.getNotification(); // API-15 and lower
        }else{
            notification = mBuilder.build();
        }

        if(close){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Sleep for 5 seconds
                        Thread.sleep(10 * 1000);
                    } catch (InterruptedException e) {
                        mNotificationManager.cancel(DEFAULT_NOTIFICATION_GPS_TRACER_ID);
                    }
                }
            }).start();
            mNotificationManager.cancel(DEFAULT_NOTIFICATION_GPS_TRACER_ID);
            stopForeground(true);
        }else {
            startForeground(DEFAULT_NOTIFICATION_SYNC_TRACER_ID, notification);
        }
    }
}
