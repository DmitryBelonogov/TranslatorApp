package com.nougust3.translator.data;

import com.google.gson.JsonObject;
import com.nougust3.translator.data.model.Model.Lang.Lang;
import com.nougust3.translator.data.model.Model.Translation.Translation;
import com.nougust3.translator.utils.MainUiThread;
import com.nougust3.translator.utils.ThreadExecutor;

import java.util.ArrayList;
import java.util.List;

public abstract class DataSource {

    protected MainUiThread mainUiThread;
    protected ThreadExecutor threadExecutor;

    public DataSource(MainUiThread mainUiThread, ThreadExecutor threadExecutor) {
        this.mainUiThread = mainUiThread;
        this.threadExecutor = threadExecutor;
    }

    public interface GetLangCallback {
        void onSuccess(String lang);
        void onFailure(Throwable throwable);
        void onNetworkFailure();
    }

    public interface GetLangsCallback {
        void onSuccess(List<Lang> langs);
        void onFailure(Throwable throwable);
        void onNetworkFailure();
    }

    public interface GetTranslationCallback {
        void onSuccess(Translation translation);
        void onFailure(Throwable throwable);
        void onNetworkFailure();
    }

    public interface GetTranslationsCallback {
        void onSuccess(ArrayList<Translation> translations);
        void onFailure(Throwable throwable);
    }

    public interface GetWordCallback {
        void onSuccess(JsonObject word);
        void onFailure(Throwable throwable);
        void onNetworkFailure();
    }

    public abstract void getLang(String text, GetLangCallback callback);
    public abstract void getLangs(String ui, GetLangsCallback callback);
    public abstract void getTranslation(String dir, String text, GetTranslationCallback callback);
    public abstract void getTranslations(GetTranslationsCallback callback);
    public abstract void getWord(String word, String dir, GetWordCallback callback);

}
