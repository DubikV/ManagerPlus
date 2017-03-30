package com.gmail.vanyadubik.managerplus.service;

import com.gmail.vanyadubik.managerplus.model.json.UploadTrackListRequest;
import com.gmail.vanyadubik.managerplus.model.json.UploadTrackListResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface SyncService {

//    @GET("mobilemanager/hs/exchange/dataDTO")
//    Call<DownloadResponse> download();
//
//    @Multipart
//    @POST("mobilemanager/hs/exchange/dataDTO")
//    Call<UploadResponse> uploadWithDocuments(@Part("data") UploadRequest request,
//                                             @Part List<MultipartBody.Part> documents);

//    @GET("cooperativetest/hs/managerplus.exchange/trackDTO")
//    Call<DownloadTrackListResultDTO> downloadTrackListOnlyResult();

    @POST("cooperativetest/hs/managerplus.exchange/trackDTO")
    Call<UploadTrackListResponse> uploadTrackListOnly(@Body UploadTrackListRequest request);
}
