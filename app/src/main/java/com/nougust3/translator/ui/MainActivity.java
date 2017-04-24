package com.nougust3.translator.ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nougust3.translator.R;
import com.nougust3.translator.data.OnSetFavorite;
import com.nougust3.translator.data.Translate;
import com.nougust3.translator.data.TranslationsAdapter;
import com.nougust3.translator.data.model.Model.Translation.Translation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends MvpAppCompatActivity implements MainView, OnSetFavorite {

    @InjectPresenter
    MainPresenter mainPresenter;

    private ConstraintLayout listLayout;
    private ConstraintLayout dictLayout;

    private Spinner originalSpinner;
    private Spinner translateSpinner;
    private ImageButton swapBtn;
    private Button clearBtn;
    private Button favoriteBtn;
    private EditText originalView;
    private EditText translateView;
    private TextView defView;
    private ListView translationsList;

    ArrayAdapter<String> adapterOrgiginal;
    ArrayAdapter<String> adapterTranslate;
    TranslationsAdapter translationsAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        listLayout = (ConstraintLayout) findViewById(R.id.history);
        dictLayout = (ConstraintLayout) findViewById(R.id.dict_layout);

        originalSpinner = (Spinner) findViewById(R.id.original_spinner);
        originalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onNothingSelected(AdapterView<?> parent) {}
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mainPresenter.OnChangeLang(position, "original");
            }
        });

        translateSpinner = (Spinner) findViewById(R.id.translate_spinner);
        translateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onNothingSelected(AdapterView<?> parent) {}
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mainPresenter.OnChangeLang(position, "translation");
            }
        });

        translateView = (EditText) findViewById(R.id.translateView);
        translateView.setTextIsSelectable(true);
        translateView.setKeyListener(null);
        defView = (TextView) findViewById(R.id.def);

        originalView = (EditText) findViewById(R.id.editText);
        originalView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 0) {
                    clearBtn.setVisibility(View.GONE);
                    favoriteBtn.setVisibility(View.GONE);
                    translateView.setText("");
                    hideDictionary();
                    return;
                }

                clearBtn.setVisibility(View.VISIBLE);
                favoriteBtn.setVisibility(View.VISIBLE);
                requestTranslate();
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void afterTextChanged(Editable s) { }
        });

        swapBtn = (ImageButton) findViewById(R.id.swap_button);
        swapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapLangs();
            }
        });

        clearBtn = (Button) findViewById(R.id.clear_btn);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainPresenter.onClearTranslation(originalSpinner.getSelectedItem().toString(),
                        originalView.getText().toString(),
                        translateSpinner.getSelectedItem().toString(),
                        translateView.getText().toString(), 0);
            }
        });

        favoriteBtn = (Button) findViewById(R.id.favorite_btn);
        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainPresenter.onClearTranslation(originalSpinner.getSelectedItem().toString(),
                        originalView.getText().toString(),
                        translateSpinner.getSelectedItem().toString(),
                        translateView.getText().toString(), 1);
            }
        });

        translationsAdapter = new TranslationsAdapter(this);
        translationsAdapter.showFavorites(false);

        translationsList = (ListView) findViewById(R.id.translations_list);
        translationsList.setAdapter(translationsAdapter);
        translationsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadTranslate(position);
                mainPresenter.OnOpenTranslate(position);
            }
        });


        mainPresenter.onStart();

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("history");
        tabSpec.setContent(R.id.linearLayout);
        tabSpec.setIndicator("История");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("favorites");
        tabSpec.setContent(R.id.linearLayout2);
        tabSpec.setIndicator("Избранное");
        tabHost.addTab(tabSpec);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                translationsAdapter.showFavorites(Objects.equals(tabId, "favorites"));
                translationsAdapter.notifyDataSetChanged();
            }
        });

        tabHost.setCurrentTabByTag("history");

    }

    @Override
    public void onSetFavorite(long id) {
        mainPresenter.OnUpdateTranslate(id);
    }

    @Override
    public void swapLangs() {
        originalView.setText(translateView.getText().toString());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int tmp = originalSpinner.getSelectedItemPosition();
                originalSpinner.setSelection(translateSpinner.getSelectedItemPosition(), true);
                translateSpinner.setSelection(tmp, true);
                requestTranslate();
            }
        }, 130);

        animateLangs();
    }

    @Override
    public void populateSpinners(List<String> langs) {
        adapterOrgiginal = new ArrayAdapter<>(this, R.layout.language_item, langs);
        adapterTranslate = new ArrayAdapter<>(this, R.layout.language_item, langs);
        originalSpinner.setAdapter(adapterOrgiginal);
        translateSpinner.setAdapter(adapterTranslate);
    }

    @Override
    public void setOrigLang(int orig) {
        originalSpinner.setSelection(orig);
    }

    @Override
    public void setTranslLang(int transl) {
        translateSpinner.setSelection(transl);
    }

    @Override
    public void setTranslation(int origLang, int translLang, String orig) {
        setOrigLang(origLang);
        setTranslLang(translLang);
        originalView.setText(orig);
    }

    @Override
    public void updateTranslate(String translate) {
        translateView.setText(translate);
    }

    private void requestTranslate() {
        mainPresenter.OnChangeOriginal(originalView.getText().toString(),
                originalSpinner.getSelectedItem().toString(),
                translateSpinner.getSelectedItem().toString());
    }

    private void loadTranslate(int id) {
        Translation translation = (Translation) translationsAdapter.getItem(id);

        originalView.setText(translation.getOriginal());
        translateView.setText(translation.getTranslation());
    }

    @Override
    public void updateList(ArrayList<Translation> translations) {
        translationsAdapter.setTranslations(translations);
        translationsAdapter.notifyDataSetChanged();
    }

    @Override
    public void clearViews() {
        originalView.setText("");
        translateView.setText("");
        hideDictionary();
    }

    @Override
    public void showMessage(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void animateLangs() {
        RotateAnimation rotateAnim = new RotateAnimation(0, 180,
                swapBtn.getWidth()/2, swapBtn.getHeight()/2);
        rotateAnim.setDuration(250);
        swapBtn.startAnimation(rotateAnim);

        YoYo.with(Techniques.SlideOutLeft).duration(150).playOn(originalSpinner);
        YoYo.with(Techniques.SlideOutRight).duration(150).playOn(translateSpinner);
        YoYo.with(Techniques.SlideInLeft).duration(150).delay(150).playOn(originalSpinner);
        YoYo.with(Techniques.SlideInRight).duration(150).delay(150).playOn(translateSpinner);
    }

    @Override
    public void showDictionary() {
        listLayout.setVisibility(View.GONE);
        dictLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideDictionary() {
        listLayout.setVisibility(View.VISIBLE);
        dictLayout.setVisibility(View.GONE);
    }

    @Override
    public void populateDictionary(Spannable definition) {
        showDictionary();
        defView.setText(definition);
    }
}
