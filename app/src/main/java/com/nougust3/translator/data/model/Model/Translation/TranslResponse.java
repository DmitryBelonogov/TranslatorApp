package com.nougust3.translator.data.model.Model.Translation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TranslResponse {

    @SerializedName("code")
    @Expose
    String resCode;

    @SerializedName("lang")
    @Expose
    String dir;

    @SerializedName("text")
    @Expose
    String[] text;

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public void setText(String[] text) {
        this.text = text;
    }

    public String getResCode() {
        return resCode;
    }

    public String getDir() {
        return dir;
    }

    public String[] getText() {
        return text;
    }

}
