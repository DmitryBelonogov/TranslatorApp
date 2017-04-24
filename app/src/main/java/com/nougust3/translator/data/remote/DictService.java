package com.nougust3.translator.data.remote;

import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface DictService {

    @GET("/api/v1/dicservice.json/lookup")
    Call<JsonObject> getWord(@Query("key") String key, @QueryMap Map<String, String> queryMap);

}
