package com.nougust3.translator.data.model.Model.Langs;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.nougust3.translator.data.model.Model.Lang.Lang;

import java.util.List;

public class LangsResponse {

    @SerializedName("langs")
    @Expose
    List<Lang> langs = null;

    public void setLangs(List<Lang> langs) {
        this.langs = langs;
    }

    public List<Lang> getLangs() {
        return langs;
    }

}
