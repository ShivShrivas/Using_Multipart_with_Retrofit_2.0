package com.example.multipartexampleforretrofit;


import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public  interface ApiService {



   @Headers("ApiKey:A1413083489FA750112FEE859535F76CF7086151344535324538")
   @Multipart
    @POST("PhysicalParam/SBiometricv2")
    Call<List<JsonObject>> uploadDataInBioMetric(@Part MultipartBody.Part part,@Part("RequestData") RequestBody object);




}
