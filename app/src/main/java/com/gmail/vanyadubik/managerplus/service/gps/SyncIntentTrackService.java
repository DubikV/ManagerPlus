package com.gmail.vanyadubik.managerplus.service.gps;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.gmail.vanyadubik.managerplus.R;
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

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Response;

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

    public SyncIntentTrackService() {
        super(SyncIntentTrackService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((ManagerPlusAplication) getApplication()).getComponent().inject(this);
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

    private UploadTrackListRequest getUploadData(List<LocationPoint> trackList) {

        List<LocationPointDTO> result = new ArrayList<>();

        for (LocationPoint locationPoint : trackList) {

            result.add(convertLocationPoint(locationPoint));

        }
        return new UploadTrackListRequest(result);
    }

    private void updateDb(DownloadTrackListResultDTO response) {

        dataRepository.SetTrackListUloadedLocationTrack(response.getDateTimeStart(),
                response.getDateTimeEnd());

    }
}
