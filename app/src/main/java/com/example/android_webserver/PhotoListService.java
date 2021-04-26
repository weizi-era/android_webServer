package com.example.android_webserver;

import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface PhotoListService {

    @POST("/login")
    Call<Response> updatePhoto (@Query("des") String description, @Query("image") RequestBody img);

}
