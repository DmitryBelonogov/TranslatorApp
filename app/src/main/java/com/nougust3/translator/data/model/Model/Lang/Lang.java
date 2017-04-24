package com.nougust3.translator.data.model.Model.Lang;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Lang {

    @SerializedName("lang")
    @Expose
    private String lang;

    @SerializedName("code")
    @Expose
    private String code;

    public Lang(String code, String lang) {
        this.lang = lang;
        this.code = code;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return lang;
    }

    public int compareThem(Lang compLang) {
        return getLang().compareTo(compLang.getLang());
    }
}
