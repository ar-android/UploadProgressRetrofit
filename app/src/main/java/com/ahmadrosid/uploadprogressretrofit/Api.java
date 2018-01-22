package com.ahmadrosid.uploadprogressretrofit;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mymacbook on 1/22/18.
 */

public class Api {
    public static final String main_url = "https://ocit-tutorial.herokuapp.com";

    public static ApiService build(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(main_url)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(ApiService.class);
    }
}
