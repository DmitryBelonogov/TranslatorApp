package com.nougust3.translator.data;

import com.google.gson.JsonObject;
import com.nougust3.translator.data.local.LocalDataSource;
import com.nougust3.translator.data.model.Model.Lang.Lang;
import com.nougust3.translator.data.model.Model.Translation.Translation;
import com.nougust3.translator.data.remote.RemoteDataSource;
import com.nougust3.translator.utils.MainUiThread;
import com.nougust3.translator.utils.ThreadExecutor;

import java.util.ArrayList;
import java.util.List;

public class DataRepository {

    private DataSource remoteDataSource;
    private DataSource localDataSource;

    private static DataRepository dataRepository;

    private DataRepository() {
        remoteDataSource = RemoteDataSource.getInstance(MainUiThread.getInstance(),
                ThreadExecutor.getInstance());
        localDataSource = LocalDataSource.getInstance(MainUiThread.getInstance(),
                ThreadExecutor.getInstance());
    }

    public static synchronized DataRepository getInstance() {
        if (dataRepository == null)
            dataRepository = new DataRepository();
        return dataRepository;
    }

    public void getLangs(String locale, final DataSource.GetLangsCallback callback) {
        remoteDataSource.getLangs(locale, new DataSource.GetLangsCallback() {
            @Override
            public void onSuccess(List<Lang> langs) {
                callback.onSuccess(langs);
            }
            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }
            @Override
            public void onNetworkFailure() {
                callback.onNetworkFailure();
            }
        });
    }

    public void getWord(String original, String dir, final DataSource.GetWordCallback callback) {
        remoteDataSource.getWord(original, dir, new DataSource.GetWordCallback() {
            @Override
            public void onSuccess(JsonObject word) { callback.onSuccess(word); }
            @Override
            public void onFailure(Throwable throwable) { callback.onFailure(throwable); }
            @Override
            public void onNetworkFailure() { callback.onNetworkFailure(); }
        });
    }

    public void getTranslation(String dir, String text, final DataSource.GetTranslationCallback callback) {
        remoteDataSource.getTranslation(dir, text, new DataSource.GetTranslationCallback() {
            @Override
            public void onSuccess(Translation translation) { callback.onSuccess(translation); }
            @Override
            public void onFailure(Throwable throwable) {
                    callback.onFailure(throwable);
                }
            @Override
            public void onNetworkFailure() {
                    callback.onNetworkFailure();
                }
        });
    }

    public void getTranslations(final DataSource.GetTranslationsCallback callback) {
        localDataSource.getTranslations(new DataSource.GetTranslationsCallback() {
            @Override
            public void onSuccess(ArrayList<Translation> translations) { callback.onSuccess(translations); }
            @Override
            public void onFailure(Throwable throwable) { callback.onFailure(throwable); }
        });
    }

    public void storeTranslation(Translation translation) {
        ((LocalDataSource) localDataSource).storeTranslate(translation);
    }

    public void putDefaultLang(String lang, String code) {
        ((LocalDataSource) localDataSource).putDefaultLangs(lang, code);
    }

    public String getDefaultLang(String lang) {
        return ((LocalDataSource) localDataSource).getDefaultLang(lang);
    }
}
