package com.example.myapplication;

// ApiService.java
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

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
}

