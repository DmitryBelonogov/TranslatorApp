package com.nougust3.translator.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nougust3.translator.data.model.Model.Translation.Translation;

import java.util.ArrayList;

class LocalDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_TRANSLATIONS = "translates";

    private static final String KEY_ID = "id";
    private static final String KEY_ORIGINAL = "original";
    private static final String KEY_ORIGINAL_LANG= "original_lang";
    private static final String KEY_TRANSLATION = "translation";
    private static final String KEY_TRANSLATION_LANG = "translation_lang";
    private static final String KEY_FAVORITE = "favorite";


    private static final String DICTIONARY_TABLE_CREATE =
            "CREATE TABLE " + TABLE_TRANSLATIONS + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY, " +
                    KEY_ORIGINAL + " TEXT, " +
                    KEY_ORIGINAL_LANG + " TEXT, " +
                    KEY_TRANSLATION + " TEXT, " +
                    KEY_TRANSLATION_LANG + " TEXT, " +
                    KEY_FAVORITE + " INTEGER);";

    LocalDatabase(Context context) {
        super(context, TABLE_TRANSLATIONS, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DICTIONARY_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    Translation update(Translation translation) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues values = new ContentValues();

        db.beginTransaction();

        values.put(KEY_ID, translation.getId());
        values.put(KEY_ORIGINAL, translation.getOriginal());
        values.put(KEY_ORIGINAL_LANG, translation.getOriginalLang());
        values.put(KEY_TRANSLATION, translation.getTranslation());
        values.put(KEY_TRANSLATION_LANG, translation.getTranslationLang());
        values.put(KEY_FAVORITE, translation.getFavorite());

        db.insertWithOnConflict(TABLE_TRANSLATIONS, KEY_ID, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.setTransactionSuccessful();
        db.endTransaction();

        return translation;
    }

    ArrayList<Translation> getTranslations() {
        ArrayList<Translation> translationsList = new ArrayList<>();

        String query = "SELECT " + KEY_ID + ","
                + KEY_ORIGINAL + ","
                + KEY_ORIGINAL_LANG + ","
                + KEY_TRANSLATION + ","
                + KEY_TRANSLATION_LANG + ","
                + KEY_FAVORITE + " FROM "
                + TABLE_TRANSLATIONS
                + " ORDER BY "
                + KEY_ID + " ASC ";

        Cursor cursor = null;

        try {
            cursor = getReadableDatabase().rawQuery(query, null);

            if(cursor.moveToFirst())
                do {
                    Translation translation = new Translation();

                    translation.setId(Long.parseLong(cursor.getString(0)));
                    translation.setOriginal(cursor.getString(1));
                    translation.setOriginalLang(cursor.getString(2));
                    translation.setTranslation(cursor.getString(3));
                    translation.setTranslationLang(cursor.getString(4));
                    translation.setFavorite(Integer.parseInt(cursor.getString(5)));

                    translationsList.add(translation);
                } while (cursor.moveToNext());
        }
        finally { if (cursor != null) cursor.close(); }

        return translationsList;
    }
}
