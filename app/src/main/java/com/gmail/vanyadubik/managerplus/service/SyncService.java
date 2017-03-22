package com.gmail.vanyadubik.managerplus.service;

import com.gmail.vanyadubik.managerplus.model.json.DownloadTrackListResultDTO;
import com.gmail.vanyadubik.managerplus.model.json.UploadTrackListRequest;
import com.gmail.vanyadubik.managerplus.model.json.UploadTrackListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface SyncService {

//    @GET("mobilemanager/hs/exchange/dataDTO")
//    Call<DownloadResponse> download();
//
//    @Multipart
//    @POST("mobilemanager/hs/exchange/dataDTO")
//    Call<UploadResponse> uploadWithDocuments(@Part("track") UploadRequest request,
//                                             @Part List<MultipartBody.Part> documents);

    @GET("managerplus/hs/exchange/trackDTO")
    Call<DownloadTrackListResultDTO> downloadTrackListOnlyResult();

    @POST("managerplus/hs/exchange/trackDTO")
    Call<UploadTrackListResponse> uploadTrackListOnly(@Part("track_list") UploadTrackListRequest request);
}
