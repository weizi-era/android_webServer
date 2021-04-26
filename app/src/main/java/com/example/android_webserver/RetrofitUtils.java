package com.example.android_webserver;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtils {

    private static Retrofit mInstance;

    public static Retrofit getInstance() {
        if (mInstance == null) {
            synchronized (RetrofitUtils.class) {
                mInstance = new Retrofit.Builder()
                        .client(new OkHttpClient())
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl("http://192.168.60.232:8080/")
                        .build();
            }
        }

        return mInstance;
    }
}
