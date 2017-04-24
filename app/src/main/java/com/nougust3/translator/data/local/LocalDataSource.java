package com.nougust3.translator.data.local;

import android.content.SharedPreferences;

import com.nougust3.translator.data.DataSource;
import com.nougust3.translator.data.model.Model.Translation.Translation;
import com.nougust3.translator.Translator;
import com.nougust3.translator.utils.MainUiThread;
import com.nougust3.translator.utils.ThreadExecutor;

import java.util.ArrayList;

public class LocalDataSource extends DataSource {

    private static LocalDataSource dataSource;
    private LocalDatabase database;
    private SharedPreferences preferences;

    private LocalDataSource(MainUiThread mainUiThread, ThreadExecutor threadExecutor) {
        super(mainUiThread, threadExecutor);
        database = new LocalDatabase(Translator.getAppContext());
        preferences = Translator.getAppContext().getSharedPreferences("langs", 0);
    }

    public static synchronized LocalDataSource getInstance(MainUiThread mainUiThread,
                                                           ThreadExecutor threadExecutor) {
        if (dataSource == null)
            dataSource = new LocalDataSource(mainUiThread, threadExecutor);
        return dataSource;
    }

    public void getTranslations(final GetTranslationsCallback callback) {
        threadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final ArrayList<Translation> translations = database.getTranslations();

                mainUiThread.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(translations);
                    }
                });
            }
        });
    }

    @Override
    public void getWord(String word, String dir, GetWordCallback callback) { }

    @Override
    public void getLang(String text, GetLangCallback callback) { }

    @Override
    public void getLangs(String ui, GetLangsCallback callback) { }

    @Override
    public void getTranslation(String dir, String text, final GetTranslationCallback callback) { }

    public void storeTranslate(Translation translation) {
        database.update(translation);
    }

    public void putDefaultLangs(String lang, String code) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(lang, code);
        editor.apply();
    }

    public String getDefaultLang(String lang) {
        if(lang.contains("original"))
            return preferences.getString("original", "ru");
        else return preferences.getString("translation", "en");
    }

}
