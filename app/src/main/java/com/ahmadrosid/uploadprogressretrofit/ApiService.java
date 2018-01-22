package com.ahmadrosid.uploadprogressretrofit;

import io.reactivex.Flowable;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by mymacbook on 1/22/18.
 */

public interface ApiService {
    @POST("/upload")
    Flowable<ResponseUpload> upload(@Body ProgressRequestBody body);
}
