package com.example.myapplication;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {

    @Multipart
    @POST("register")
    Call<ResponseBody> registerVisitor(
            @Part("name") RequestBody name,
            @Part("phone") RequestBody phone,
            @Part("email") RequestBody email,
            @Part("idProof") RequestBody idProof,
            @Part MultipartBody.Part photo
    );

    @POST("approveVisitor")
    Call<ResponseBody> approveVisitor(
            @Query("id") int id,
            @Query("status") String status
    );

    // Get visitor details by ID
    @GET("visitor")
    Call<Visitor> getVisitorById(@Query("id") int visitorId);

    // Get the next visitor based on the current visitorId
    @GET("nextVisitor")
    Call<Visitor> getNextVisitor(@Query("id") int visitorId);
}
