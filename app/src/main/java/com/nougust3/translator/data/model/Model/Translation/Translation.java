package com.nougust3.translator.data.model.Model.Translation;

import com.nougust3.translator.data.model.Model.Lang.Lang;

public class Translation {

    private long id;

    private String original;
    private String originalLang;
    private String translation;
    private String translationLang;

    private int favorite;

    public Translation() {
        this.id = 0;
        this.original = "";
        this.originalLang = "";
        this.translation = "";
        this.translationLang = "";
        this.favorite = 0;
    }

    public Translation(long id, String original, String originalLang, String translation, String translationLang, int favorite) {
        this.id = id;
        this.original = original;
        this.originalLang = originalLang;
        this.translation = translation;
        this.translationLang = translationLang;
        this.favorite = favorite;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getOriginalLang() {
        return originalLang;
    }

    public void setOriginalLang(String originalLang) {
        this.originalLang = originalLang;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public String getTranslationLang() {
        return translationLang;
    }

    public void setTranslationLang(String translationLang) {
        this.translationLang = translationLang;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public int compareThem(Translation comp) {
        return getId() < comp.getId() ? 1 : -1;
    }

}
