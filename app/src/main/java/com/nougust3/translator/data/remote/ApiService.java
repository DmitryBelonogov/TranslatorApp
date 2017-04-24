package com.nougust3.translator.data.remote;
import com.google.gson.JsonObject;
import com.nougust3.translator.data.model.Model.Lang.LangResponse;
import com.nougust3.translator.data.model.Model.Translation.TranslResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ApiService {

    @GET("/api/v1.5/tr.json/detect")
    Call<LangResponse> getLang(@QueryMap Map<String, String> queryMap);

    @GET("/api/v1.5/tr.json/getLangs")
    Call<JsonObject> getLangs(@QueryMap Map<String, String> queryMap);

    @GET("/api/v1.5/tr.json/translate")
    Call<TranslResponse> getTranslation(@QueryMap Map<String, String> queryMap);

}