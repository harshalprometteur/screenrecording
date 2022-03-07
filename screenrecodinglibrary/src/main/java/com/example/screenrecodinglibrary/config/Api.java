package com.example.screenrecodinglibrary.config;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {

    String baseUrl = "http://34.66.233.90:7090/";

    @Multipart
    @POST("addVideo")
    Call<ResponseBody> addVideo(
            @Part MultipartBody.Part file);

}
