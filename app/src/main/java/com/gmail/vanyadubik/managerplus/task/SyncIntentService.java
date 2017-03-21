package com.gmail.vanyadubik.managerplus.task;

import android.app.IntentService;
import android.content.Intent;

import com.gmail.vanyadubik.managerplus.app.ManagerPlusAplication;
import com.gmail.vanyadubik.managerplus.repository.DataRepository;
import com.gmail.vanyadubik.managerplus.service.SyncService;
import com.gmail.vanyadubik.managerplus.utils.ErrorUtils;
import com.gmail.vanyadubik.managerplus.utils.NetworkUtils;

import javax.inject.Inject;

public class SyncIntentService extends IntentService{
    public static final String SYNC_RECEIVER = "sync_receiver";
    public static final String DELIMITER = "_";

    @Inject
    DataRepository dataRepository;
    @Inject
    NetworkUtils networkUtils;
    @Inject
    ErrorUtils errorUtils;

    private SyncService syncService;

    public SyncIntentService() {
        super(SyncIntentService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((ManagerPlusAplication) getApplication()).getComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        final ResultReceiver receiver = intent.getParcelableExtra(SYNC_RECEIVER);
//        final Bundle bundle = new Bundle();
//
//        if (!networkUtils.checkEthernet()) {
//            bundle.putString(Intent.EXTRA_TEXT, getResources().getString(R.string.error_internet_connecting));
//            receiver.send(STATUS_ERROR_SYNC, bundle);
//            return;
//        }
//
//        if (networkUtils.checkUSBconnectionToEcomilk()) {
//            bundle.putString(Intent.EXTRA_TEXT, getResources().getString(R.string.wrong_connection));
//            receiver.send(STATUS_ERROR_SYNC, bundle);
//            return;
//        }
//
//        syncService = SyncServiceFactory.createService(
//                SyncService.class,
//                this.getBaseContext());
//
//        receiver.send(STATUS_STARTED_SYNC, bundle);
//        Track latestTrack = dataRepository.getLatestTrack();
//        if (latestTrack != null) {
//            final UploadRequest request = new UploadRequest(getUploadData(latestTrack));
//            List<MultipartBody.Part> documents = getDocuments(latestTrack);
//            try {
//                Response<UploadResponse> uploadResponse = syncService.uploadWithDocuments(request, documents).execute();
//                if (uploadResponse.isSuccessful()) {
//                    UploadResponse response = uploadResponse.body();
//                    if (response.getExternalIdPairs() != null) {
//                        for (ExternalIdPair externalIdPair : response.getExternalIdPairs()) {
//                            dataRepository.updateMemberExternalId(
//                                    request.getTrack().getId(),
//                                    externalIdPair.getAppExternalId(),
//                                    externalIdPair.getNewExternalId());
//                        }
//                    }
//
//                    if (response.getUploadedDocuments() != null) {
//                        clearDocuments(response.getUploadedDocuments());
//                    }
//
//                } else {
//                    APIError error = errorUtils.parseErrorCode(uploadResponse.code());
//                    bundle.putString(Intent.EXTRA_TEXT, error.getMessage());
//                    receiver.send(STATUS_ERROR_SYNC, bundle);
//                    return;
//                }
//            } catch (Exception exception) {
//                APIError error = errorUtils.parseErrorMessage(exception);
//                bundle.putString(Intent.EXTRA_TEXT, error.getMessage());
//                receiver.send(STATUS_ERROR_SYNC, bundle);
//                return;
//            }
//        }
//        try {
//            Response<DownloadResponse> downloadResponse = syncService.download().execute();
//            if (downloadResponse.isSuccessful()) {
//                updateDb(downloadResponse.body());
//                bundle.putString(Intent.EXTRA_TEXT, getResources().getString(R.string.sync_success));
//                receiver.send(STATUS_FINISHED_SYNC, bundle);
//            } else {
//                APIError error = errorUtils.parseErrorCode(downloadResponse.code());
//                bundle.putString(Intent.EXTRA_TEXT, error.getMessage());
//                receiver.send(STATUS_ERROR_SYNC, bundle);
//
//            }
//        } catch (Exception exception) {
//            APIError error = errorUtils.parseErrorMessage(exception);
//            bundle.putString(Intent.EXTRA_TEXT, error.getMessage());
//            receiver.send(STATUS_ERROR_SYNC, bundle);
//        }

    }
}
