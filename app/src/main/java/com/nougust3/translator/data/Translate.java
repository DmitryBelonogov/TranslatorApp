package com.nougust3.translator.data;

/**
 * Created by Perceval Balonezov on 4.4.17.
 */

public class Translate {

    private int id;
    private String original;
    private String originalLang;
    private String translate;
    private String translateLang;

    public Translate(String original, String originalLang,
                     String translate, String translateLang) {
        this.original = original;
        this.originalLang = originalLang;
        this.translate = translate;
        this.translateLang = translateLang;
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

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    public String getTranslateLang() {
        return translateLang;
    }

    public void setTranslateLang(String translateLang) {
        this.translateLang = translateLang;
    }
}
