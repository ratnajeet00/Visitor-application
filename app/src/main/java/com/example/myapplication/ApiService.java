package com.example.myapplication;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
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

    @GET("visitors")
    Call<List<Visitor>> getPendingVisitors();

    @POST("approveVisitor/{visitorId}")
    Call<ResponseBody> approveVisitor(
            @Path("visitorId") int visitorId,
            @Body ApprovalRequest status
    );
}
