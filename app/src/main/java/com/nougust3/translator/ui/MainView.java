package com.nougust3.translator.ui;

import android.text.Spannable;

import com.arellomobile.mvp.MvpView;
import com.nougust3.translator.data.model.Model.Translation.Translation;

import java.util.ArrayList;
import java.util.List;

interface MainView extends MvpView {

    void swapLangs();
    void setOrigLang(int orig);
    void setTranslLang(int transl);
    void setTranslation(int origLang, int translLang, String orig);
    void populateSpinners(List<String> langs);

    void updateTranslate(String translate);
    void updateList(ArrayList<Translation> translations);
    void clearViews();
    void showMessage(String msg);

    void showDictionary();
    void hideDictionary();
    void populateDictionary(Spannable definition);

}
