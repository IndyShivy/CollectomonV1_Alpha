package com.example.collectomon;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CardDatabase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "card_database";
    public static final int DATABASE_VERSION = 2;
    public static final String TABLE_NAME = "cards";
    public static final String ARTIST_NAME = "artist_name";
    public static final String CARD_ID = "card_id";
    public static final String COLUMN_IMAGE_SRC = "image_src";
    public static final String COLUMN_CARD_NAME = "card_name";
    public static final String COLUMN_SET_DETAILS = "set_details";
    public static final String COLUMN_CARD_DETAILS = "card_details";


    public CardDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_NAME + " (" +
                ARTIST_NAME + " TEXT, " +
                CARD_ID + " TEXT, " +
                COLUMN_IMAGE_SRC + " TEXT, " +
                COLUMN_CARD_NAME + " TEXT, " +
                COLUMN_SET_DETAILS + " TEXT, " +
                COLUMN_CARD_DETAILS + " TEXT)";
        db.execSQL(createTableQuery);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTableQuery = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(dropTableQuery);
        onCreate(db);
    }

    /*
    public void addCards(List<CardItem> cards) {
        SQLiteDatabase db = getWritableDatabase();

        for (CardItem card : cards) {
            ContentValues values = new ContentValues();
            values.put(ARTIST_NAME, card.getArtistName()); // Add artist name to the ContentValues
            values.put(CARD_ID, card.getCardId());
            values.put(COLUMN_IMAGE_SRC, card.getImageUrl());
            values.put(COLUMN_CARD_NAME, card.getCardName());
            values.put(COLUMN_SET_DETAILS, card.getSetDetails());
            values.put(COLUMN_CARD_DETAILS, card.getCardDetails());

            db.insert(TABLE_NAME, null, values);
            Log.d("CardDatabase", "Added Card: Name" + card.getArtistName()+"ID:"+card.getCardId()+"IAMGE"+card.getImageUrl()+card.getCardName()+card.getCardDetails()+card.getSetDetails());
        }

        db.close();
    }
     */

    public List<CardItem> getCardsByArtist(String artistName) {
        List<CardItem> cardList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {
                ARTIST_NAME,
                CARD_ID,
                COLUMN_IMAGE_SRC,
                COLUMN_CARD_NAME,
                COLUMN_SET_DETAILS,
                COLUMN_CARD_DETAILS
        };

        String selection = ARTIST_NAME + " = ?";
        String[] selectionArgs = {artistName};

        Cursor cursor = db.query(
                TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            do {
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(ARTIST_NAME));
                String cardId = cursor.getString(cursor.getColumnIndexOrThrow(CARD_ID));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_SRC));
                String cardName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CARD_NAME));
                String setDetails = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SET_DETAILS));
                String cardDetails = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CARD_DETAILS));

                CardItem cardItem = new CardItem(artist, cardId, imageUrl, cardName, setDetails, cardDetails);
                cardList.add(cardItem);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return cardList;
    }


    public void addCards(List<CardItem> cards) {
        SQLiteDatabase db = getWritableDatabase();

        for (CardItem card : cards) {
            if (!isCardIdExists(db, card.getCardId())) {
                ContentValues values = new ContentValues();
                values.put(ARTIST_NAME, card.getArtistName()); // Add artist name to the ContentValues
                values.put(CARD_ID, card.getCardId());
                values.put(COLUMN_IMAGE_SRC, card.getImageUrl());
                values.put(COLUMN_CARD_NAME, card.getCardName());
                values.put(COLUMN_SET_DETAILS, card.getSetDetails());
                values.put(COLUMN_CARD_DETAILS, card.getCardDetails());

                db.insert(TABLE_NAME, null, values);
                Log.d("CardDatabase", "Added Card: Name" + card.getArtistName() + "ID:" + card.getCardId() + "IAMGE" + card.getImageUrl() + card.getCardName() + card.getCardDetails() + card.getSetDetails());
            } else {
                Log.d("CardDatabase", "Skipped Card: ID " + card.getCardId() + " already exists in the database.");
            }
        }

        db.close();
    }

    private boolean isCardIdExists(SQLiteDatabase db, String cardId) {
        String[] columns = {CARD_ID};
        String selection = CARD_ID + " = ?";
        String[] selectionArgs = {cardId};
        String limit = "1";

        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null, limit);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) {
            cursor.close();
        }

        return exists;
    }



}

