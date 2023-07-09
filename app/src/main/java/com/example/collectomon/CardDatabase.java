package com.example.collectomon;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class CardDatabase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "card_database";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "cards";
    public static final String COLUMN_ID = "id";
    public static final String CARD_ID = "";
    public static final String COLUMN_IMAGE_SRC = "image_src";
    public static final String COLUMN_CARD_NAME = "card_name";
    public static final String COLUMN_SET_DETAILS = "set_details";
    public static final String COLUMN_CARD_DETAILS = "card_details";
    public static final String COLUMN_CHECKED = "checked";


    public CardDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CARD_ID + " TEXT, " +
                COLUMN_IMAGE_SRC + " TEXT, " +
                COLUMN_CARD_NAME + " TEXT, " +
                COLUMN_SET_DETAILS + " TEXT, " +
                COLUMN_CARD_DETAILS + " TEXT, " +
                COLUMN_CHECKED + " INTEGER)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableQuery = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(dropTableQuery);
        onCreate(db);
    }
    public void addCard(CardItem card) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CARD_NAME, card.getCardName());
        values.put(COLUMN_CHECKED, card.isChecked() ? 1 : 0);
        long cardId = db.insert(TABLE_NAME, null, values);
        card.setId(cardId); // Set the card ID
        db.close();
    }
    public void updateCardChecked(String cardId, boolean isChecked) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (isChecked) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CHECKED, 1);
            db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{cardId});
        } else {
            db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{cardId});
            System.out.println("It has been deleted");
        }

        db.close();
    }
    public List<CardItem> getAllCardItems() {
        List<CardItem> cardList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                @SuppressLint("Range") String cardName = cursor.getString(cursor.getColumnIndex(COLUMN_CARD_NAME));
                @SuppressLint("Range") int isChecked = cursor.getInt(cursor.getColumnIndex(COLUMN_CHECKED));
                boolean isCheckedBoolean = isChecked == 1;
                //CardItem card = new CardItem(id, cardName, isCheckedBoolean);
                //cardList.add(card);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return cardList;
    }
}
