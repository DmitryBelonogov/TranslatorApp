package com.nougust3.translator.data.remote;

import com.google.gson.JsonObject;
import com.nougust3.translator.Constants;
import com.nougust3.translator.Injector;
import com.nougust3.translator.data.DataSource;
import com.nougust3.translator.data.model.Model.Lang.Lang;
import com.nougust3.translator.data.model.Model.Lang.LangResponse;
import com.nougust3.translator.data.model.Model.Translation.TranslResponse;
import com.nougust3.translator.data.model.Model.Translation.Translation;
import com.nougust3.translator.utils.MainUiThread;
import com.nougust3.translator.utils.ThreadExecutor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

public class RemoteDataSource extends DataSource {


    private static RemoteDataSource remoteDataSource;

    private ApiService apiService;
    private DictService dictService;

    private RemoteDataSource(MainUiThread mainUiThread, ThreadExecutor threadExecutor) {
        super(mainUiThread, threadExecutor);
        apiService = Injector.provideTranslService();
        dictService = Injector.provideDictService();
    }

    public static synchronized RemoteDataSource getInstance(MainUiThread mainUiThread,
                                                            ThreadExecutor threadExecutor) {
        if (remoteDataSource == null)
            remoteDataSource = new RemoteDataSource(mainUiThread, threadExecutor);
        return remoteDataSource;
    }

    @Override
    public void getLang(String text, final GetLangCallback callback) {
        Map<String, String> queryMap = new HashMap<>();

        queryMap.put("key", Constants.TRANSLATOR_KEY);
        queryMap.put("text", text);

        retrofit2.Call<LangResponse> call = apiService.getLang(queryMap);

        call.enqueue(new retrofit2.Callback<LangResponse>() {

            @Override
            public void onResponse(Call<LangResponse> call, retrofit2.Response<LangResponse> response) {
                if (response.isSuccessful()) {
                    LangResponse langResponse = response.body();
                    callback.onSuccess(langResponse.getLang());
                }
                else {
                    callback.onFailure(new Throwable());
                }
            }

            @Override
            public void onFailure(Call<LangResponse> call, Throwable t) {
                callback.onFailure(t);
            }

        });
    }

    @Override
    public void getLangs(String ui, final GetLangsCallback callback) {
        Map<String, String> queryMap = new HashMap<>();

        queryMap.put("key", Constants.TRANSLATOR_KEY);
        queryMap.put("ui", ui);

        retrofit2.Call<JsonObject> call = apiService.getLangs(queryMap);

        call.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject res = new JSONObject(response.body().toString()).getJSONObject("langs");
                        List<Lang> langs = new ArrayList<>();

                        for (int i = 0; i < res.length(); i++)
                            langs.add(new Lang(
                                    res.names().get(i).toString(),
                                    res.get(res.names().getString(i)).toString()
                            ));

                        callback.onSuccess(langs);
                    } catch (JSONException e) { e.printStackTrace(); }
                }
                else callback.onFailure(new Throwable());
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) { callback.onFailure(t); }
        });
    }

    public void getWord(String word, String dir, final GetWordCallback callback) {
        Map<String, String> queryMap = new HashMap<>();

        queryMap.put("lang", dir);
        queryMap.put("text", word);
        queryMap.put("ui", "ru");

        retrofit2.Call<JsonObject> call = dictService.getWord(Constants.DICTIONARY_KEY, queryMap);

        call.enqueue(new retrofit2.Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (response.isSuccessful()) callback.onSuccess(response.body());
                else callback.onFailure(new Throwable());
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) { callback.onFailure(t); }
        });
    }

    @Override
    public void getTranslation(final String dir, final String text, final GetTranslationCallback callback) {
        Map<String, String> queryMap = new HashMap<>();

        queryMap.put("key", Constants.TRANSLATOR_KEY);
        queryMap.put("lang", dir);
        queryMap.put("text", text);

        retrofit2.Call<TranslResponse> call = apiService.getTranslation(queryMap);

        call.enqueue(new retrofit2.Callback<TranslResponse>() {
            @Override
            public void onResponse(Call<TranslResponse> call, Response<TranslResponse> response) {
                if (response.isSuccessful()) {
                    TranslResponse translResponse = response.body();
                    Translation translation = new Translation();
                    translation.setId(new Date().getTime());
                    translation.setOriginal(text);
                    translation.setTranslation(translResponse.getText()[0]);
                    translation.setOriginalLang(dir.substring(0, 1));
                    translation.setTranslationLang(dir.substring(3, 4));
                    translation.setFavorite(0);
                    callback.onSuccess(translation);
                }
                else callback.onFailure(new Throwable());
            }
            @Override
            public void onFailure(Call<TranslResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        });
    }

    @Override
    public void getTranslations(GetTranslationsCallback callback) { }
}
