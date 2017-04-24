package com.nougust3.translator.ui;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.Toast;
import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nougust3.translator.Translator;
import com.nougust3.translator.data.DataRepository;
import com.nougust3.translator.data.DataSource;
import com.nougust3.translator.data.model.Model.Lang.Lang;
import com.nougust3.translator.data.model.Model.Translation.Translation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@InjectViewState
public class MainPresenter extends MvpPresenter<MainView> {

    private DataRepository dataRepository;
    private List<Lang> languages = new ArrayList<>();
    private ArrayList<Translation> translations = new ArrayList<>();

    MainPresenter() {
        dataRepository = DataRepository.getInstance();
    }

    void onStart() {
        dataRepository.getLangs(Locale.getDefault().getLanguage(), new DataSource.GetLangsCallback() {

            @Override
            public void onSuccess(List<Lang> res) {
                languages = res;
                setLangs();
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(Translator.getAppContext(), "Failure", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNetworkFailure() {
                Toast.makeText(Translator.getAppContext(), "Network Failure", Toast.LENGTH_SHORT).show();
            }
        });

        dataRepository.getTranslations(new DataSource.GetTranslationsCallback() {
            @Override
            public void onSuccess(ArrayList<Translation> translations) {
                getViewState().updateList(translations);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(Translator.getAppContext(), "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setLangs() {
        List<String> names = new ArrayList<>();

        Collections.sort(languages, new Comparator<Lang>() {
            @Override
            public int compare(Lang o1, Lang o2) { return o1.compareThem(o2); }
        });

        for (Lang lang: languages) names.add(lang.getLang());
        getViewState().populateSpinners(names);

        String origLang = dataRepository.getDefaultLang("original"),
                translLang = dataRepository.getDefaultLang("translation");

        for(int i = 0; i <  languages.size(); i++) {
            if(languages.get(i).getCode().contains(origLang))
                getViewState().setOrigLang(i);
            if(languages.get(i).getCode().contains(translLang))
                getViewState().setTranslLang(i);
        }
    }

    void OnChangeOriginal(String original, String originalLang, String translateLang) {
        if(original.length() == 0) {
            getViewState().updateTranslate("");
            getViewState().hideDictionary();
            return;
        }

        StringBuilder dir = new StringBuilder();

        dir.append(getLangCode(originalLang));
        dir.append("-");
        dir.append(getLangCode(translateLang));

        dataRepository.getTranslation(dir.toString(), original, new DataSource.GetTranslationCallback() {

            @Override
            public void onSuccess(Translation translation) {
                getViewState().updateTranslate(translation.getTranslation());
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(Translator.getAppContext(), "Failure", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNetworkFailure() {
                Toast.makeText(Translator.getAppContext(), "Network Failure", Toast.LENGTH_SHORT).show();
            }
        });

        dataRepository.getWord(original, dir.toString(), new DataSource.GetWordCallback() {

            @Override
            public void onSuccess(JsonObject word) {
                if(word.get("def").getAsJsonArray().size() == 0)
                    getViewState().hideDictionary();
                else
                    getViewState().populateDictionary(parseDefinition(word.get("def").getAsJsonArray()));
            }

            @Override
            public void onFailure(Throwable throwable) {
                Toast.makeText(Translator.getAppContext(), "Failure", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNetworkFailure() {
                Toast.makeText(Translator.getAppContext(), "Network Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void OnChangeLang(int id, String lang) {
        dataRepository.putDefaultLang(lang, languages.get(id).getCode());
    }

    void onClearTranslation(String originalLang, String original,
                            String translationLang, String translation, int favorite) {
        for(Translation tr: translations) {
            if(Objects.equals(tr.getOriginal(), original)) {
                tr.setId(new Date().getTime());
                getViewState().clearViews();
                Collections.sort(translations, new Comparator<Translation>() {
                    @Override
                    public int compare(Translation o1, Translation o2) { return o2.compareThem(o1); }
                });
                dataRepository.storeTranslation(tr);
                getViewState().updateList(translations);
                return;
            }
        }

        translations.add(new Translation(
                new Date().getTime(), original,
                getLangCode(originalLang), translation,
                getLangCode(translationLang), favorite));

        dataRepository.storeTranslation(new Translation(
                new Date().getTime(), original,
                getLangCode(originalLang), translation,
                getLangCode(translationLang), favorite));

        getViewState().clearViews();
        getViewState().updateList(translations);
    }

    void OnOpenTranslate(int pos) {
        int id = pos;
        getViewState().setTranslation(getLangId(translations.get(id).getOriginalLang()),
                getLangId(translations.get(id).getTranslationLang()),
                translations.get(id).getOriginal());
    }

    void OnUpdateTranslate(long id) {
        for(int i = 0; i < translations.size(); i++) {
            if(id == translations.get(i).getId()) {
                Log.i("Update", "Update fav: " + translations.get(i).getOriginal());

                Translation translation = translations.get(i);
                if(translation.getFavorite() == 1) translation.setFavorite(0);
                else translation.setFavorite(1);
                translations.set(i, translation);
                dataRepository.storeTranslation(translation);
            }
        }

        getViewState().updateList(translations);
    }

    private Spannable parseDefinition(JsonArray defArray) {
        SpannableStringBuilder result = new SpannableStringBuilder();
        int start = 0;

        result.append("Варианты перевода\n");
        result.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, result.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        start = result.length();

        for(int i = 0; i < defArray.size(); i++) {
            JsonObject defObj = defArray.get(i).getAsJsonObject();
            JsonArray trArray = defObj.get("tr").getAsJsonArray();

            result.append(defObj.get("pos").getAsString()).append("\n");
            result.setSpan(new ForegroundColorSpan(0xFF007700), start, result.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            result.setSpan(new StyleSpan(Typeface.ITALIC), start, result.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


            for(int k = 0; k < trArray.size(); k++) {
                JsonObject trObj = trArray.get(k).getAsJsonObject();

                result.append(" - ").append(trObj.get("text").getAsString());

                if(trObj.get("syn") != null) {
                    JsonArray synArray = trObj.get("syn").getAsJsonArray();
                    JsonArray meanArray = trObj.get("mean").getAsJsonArray();

                    result.append(", ");

                    for (int j = 0; j < synArray.size(); j++) {
                        result.append(synArray.get(j).getAsJsonObject().get("text").getAsString());
                        if (j != synArray.size() - 1) result.append(", ");
                    }

                    start = result.length();
                    result.append(" (");

                    for (int j = 0; j < meanArray.size(); j++) {
                        result.append(meanArray.get(j).getAsJsonObject().get("text").getAsString());
                        if (j != meanArray.size() - 1) result.append(", ");
                    }
                    result.append(")");
                    result.setSpan(new ForegroundColorSpan(0xFF80494b), start, result.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = result.length();
                }
                else if(trArray.get(k).getAsJsonObject().get("mean") != null) {
                    start = result.length();
                    result.append(" (");
                    result.append(trArray.get(k).getAsJsonObject().get("mean").getAsJsonArray().get(0).getAsJsonObject().get("text").getAsString());
                    result.append(")");
                    result.setSpan(new ForegroundColorSpan(0xFF80494b), start, result.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = result.length();
                }
                result.append("\n");
            }
        }
        return result;
    }

    private String getLangCode(String name) {
        for(Lang lang: languages)
            if(lang.getLang().equals(name))
                return lang.getCode();
        return "";
    }

    private int getLangId(String code) {
        for(int i = 0; i < languages.size(); i++)
            if(languages.get(i).getCode().equals(code))
                return i;
        return 0;
    }
}
