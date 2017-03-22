package com.gmail.vanyadubik.managerplus.service.gps;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.gmail.vanyadubik.managerplus.R;
import com.gmail.vanyadubik.managerplus.activity.TrackActivity;
import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.model.APIError;
import com.gmail.vanyadubik.managerplus.model.db.LocationPoint;
import com.gmail.vanyadubik.managerplus.model.json.DownloadTrackListResultDTO;
import com.gmail.vanyadubik.managerplus.model.json.LocationPointDTO;
import com.gmail.vanyadubik.managerplus.model.json.UploadTrackListRequest;
import com.gmail.vanyadubik.managerplus.model.json.UploadTrackListResponse;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.service.SyncService;
import com.gmail.vanyadubik.managerplus.task.SyncServiceFactory;
import com.gmail.vanyadubik.managerplus.utils.ErrorUtils;
import com.gmail.vanyadubik.managerplus.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;

import static com.gmail.vanyadubik.managerplus.common.Consts.DEFAULT_NOTIFICATION_SYNC_TRACER_ID;
import static com.gmail.vanyadubik.managerplus.common.Consts.TAGLOG_SYNC_TRACK;
import static com.gmail.vanyadubik.managerplus.utils.Db2JsonModelConverter.convertLocationPoint;

public class SyncIntentTrackService extends IntentService{

    @Inject
    DataRepository dataRepository;
    @Inject
    NetworkUtils networkUtils;
    @Inject
    ErrorUtils errorUtils;

    private SyncService syncService;
    private NotificationManager notificationManager;

    public SyncIntentTrackService() {
        super(SyncIntentTrackService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((ManagerPlusAplication) getApplication()).getComponent().inject(this);

        notificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (!networkUtils.checkEthernet()) {
            return;
        }

        syncService = SyncServiceFactory.createService(
                SyncService.class,
                this.getBaseContext());

        List<LocationPoint> trackList = dataRepository.getUloadedLocationTrack();
        if (trackList != null) {
            final UploadTrackListRequest request = new UploadTrackListRequest(getUploadData(trackList));
            try {
                Response<UploadTrackListResponse> uploadResponse = syncService.uploadTrackListOnly(request).execute();
                if (uploadResponse.isSuccessful()) {
                    Log.i(TAGLOG_SYNC_TRACK, getResources().getString(R.string.sync_success));
                } else {
                    APIError error = errorUtils.parseErrorCode(uploadResponse.code());
                    Log.e(TAGLOG_SYNC_TRACK, error.getMessage());
                    return;
                }
            } catch (Exception exception) {
                APIError error = errorUtils.parseErrorMessage(exception);
                Log.e(TAGLOG_SYNC_TRACK, error.getMessage());
                return;
            }
        }
        try {
            Response<DownloadTrackListResultDTO> downloadResponse = syncService.downloadTrackListOnlyResult().execute();
            if (downloadResponse.isSuccessful()) {
                updateDb(downloadResponse.body());
                Log.i(TAGLOG_SYNC_TRACK, getResources().getString(R.string.sync_success));
            } else {
                APIError error = errorUtils.parseErrorCode(downloadResponse.code());
                Log.e(TAGLOG_SYNC_TRACK, error.getMessage());

            }
        } catch (Exception exception) {
            APIError error = errorUtils.parseErrorMessage(exception);
            Log.e(TAGLOG_SYNC_TRACK, error.getMessage());
        }

    }

    private List<LocationPointDTO> getUploadData(List<LocationPoint> trackList) {

        List<LocationPointDTO> result = new ArrayList<>();

        for (LocationPoint locationPoint : trackList) {

            result.add(convertLocationPoint(locationPoint));

        }
        return result;
    }

    private void updateDb(DownloadTrackListResultDTO response) {

        dataRepository.SetTrackListUloadedLocationTrack(response.getDateTimeStart(),
                response.getDateTimeEnd());

    }

    //Send custom notification
    public void sendNotification(String Ticker,String Title,String Text, boolean error) {

        //These three lines makes Notification to open main activity after clicking on it
        Intent notificationIntent = new Intent(this, TrackActivity.class);
        notificationIntent.setAction(Intent.ACTION_MAIN);
        notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentIntent(contentIntent)
                .setOngoing(true)
                .setSmallIcon(error ? R.mipmap.ic_gps_track_not_connect : R.mipmap.ic_gps_track_connect)
                //  .setLargeIcon(mContext.getResources().getDrawable(R.drawable.ic_gps_track_connect))   // большая картинка
                .setTicker(Ticker)
                .setContentTitle(Title)
                .setContentText(Text)
                .setWhen(System.currentTimeMillis());

        Notification notification;
        if (Build.VERSION.SDK_INT<=15) {
            notification = builder.getNotification(); // API-15 and lower
        }else{
            notification = builder.build();
        }

        startForeground(DEFAULT_NOTIFICATION_SYNC_TRACER_ID, notification);
    }
}
