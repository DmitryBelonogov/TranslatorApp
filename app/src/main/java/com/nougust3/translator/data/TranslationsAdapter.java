package com.nougust3.translator.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import com.nougust3.translator.R;
import com.nougust3.translator.data.model.Model.Translation.Translation;

import java.util.ArrayList;

public class TranslationsAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private boolean isFavorites = false;
    private ArrayList<Translation> translations;
    private ArrayList<Translation> favorites;

    private OnSetFavorite setFavoriteListener;

    public TranslationsAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        translations = new ArrayList<>();
        favorites = new ArrayList<>();
        setFavoriteListener = (OnSetFavorite) context;
    }

    @Override
    public int getCount() {
        return getList().size();
    }

    @Override
    public Object getItem(int position) {
        return getList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return getList().get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Translation translation = getList().get(position);
        final Holder holder;
        View view = convertView;

        if(view == null) {
            view = inflater.inflate(R.layout.translation_item, parent, false);
            holder = new Holder();
            holder.original = (TextView) view.findViewById(R.id.original);
            holder.originalLang = (TextView) view.findViewById(R.id.original_lang);
            holder.translation = (TextView) view.findViewById(R.id.translation);
            holder.translationLang = (TextView) view.findViewById(R.id.translation_lang);
            holder.favoriteBtn = (Button) view.findViewById(R.id.favorite_btn);

            view.setTag(holder);
        }
        else holder = (Holder) view.getTag();

        holder.original.setText(translation.getOriginal());
        holder.originalLang.setText(translation.getOriginalLang());
        holder.translation.setText(translation.getTranslation());
        holder.translationLang.setText(translation.getTranslationLang());
        holder.favoriteBtn.setBackgroundResource(translation.getFavorite() == 1 ?
                    R.drawable.ic_star : R.drawable.ic_star_border);

        holder.favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFavorite(position);
            }
        });

        return view;
    }

    private void onFavorite(int pos) {
        setFavoriteListener.onSetFavorite(getList().get(pos).getId());
    }

    private ArrayList<Translation> getList() {
        return isFavorites ? favorites : translations;
    }

    public void showFavorites(boolean show) {
        isFavorites = show;

        if(isFavorites)
            for(Translation translation: translations) {
                if (translation.getFavorite() == 1)
                    favorites.add(translation);
            }
        else {
            favorites.clear();
        }
    }

    public void setTranslations(ArrayList<Translation> translations) {
        this.translations = translations;
    }

    private class Holder {
        TextView original;
        TextView originalLang;
        TextView translation;
        TextView translationLang;
        Button favoriteBtn;
    }
}
