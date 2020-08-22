package com.jacobarau.helium.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.jacobarau.helium.db.PodcastDatabaseContract.*;

public class PodcastDatabaseHelper extends SQLiteOpenHelper {
    static final String dbName = "PodcastDB.db";
    private static final int version = 1;

    private static final String SQL_CREATE_SUBSCRIPTIONS =
            "CREATE TABLE " + Subscriptions.TABLE_NAME +
            " (" + Subscriptions._ID + " INTEGER PRIMARY KEY," +
            Subscriptions.COLUMN_NAME_URL + " TEXT," +
            Subscriptions.COLUMN_NAME_TITLE + " TEXT," +
            Subscriptions.COLUMN_NAME_LINK + " TEXT," +
            Subscriptions.COLUMN_NAME_DESCRIPTION + " TEXT," +
            Subscriptions.COLUMN_NAME_IMAGE_URL + " TEXT," +
            Subscriptions.COLUMN_NAME_LAST_UPDATED + " LONG )";

    private static final String SQL_CREATE_ITEMS =
            "CREATE TABLE " + Items.TABLE_NAME +
            " (" + Items._ID + " INTEGER PRIMARY KEY, " +
            Items.COLUMN_NAME_SUBSCRIPTION_ID + " INTEGER, " +
            Items.COLUMN_NAME_TITLE + " TEXT, " +
            Items.COLUMN_NAME_DESCRIPTION + " TEXT, " +
            Items.COLUMN_NAME_PUBLISH_DATE + " LONG, " +
            Items.COLUMN_NAME_ENCLOSURE_URL + " TEXT, " +
            Items.COLUMN_NAME_ENCLOSURE_MIME_TYPE + " TEXT, " +
            Items.COLUMN_NAME_ENCLOSURE_LENGTH_BYTES + " LONG, " +
            "FOREIGN KEY(" + Items.COLUMN_NAME_SUBSCRIPTION_ID + ") REFERENCES " + Subscriptions.TABLE_NAME + "(" + Subscriptions._ID + "))";

    private static final String SQL_DELETE_SUBSCRIPTIONS =
            "DROP TABLE IF EXISTS " + Subscriptions.TABLE_NAME;

    private static final String SQL_DELETE_ITEMS =
            "DROP TABLE IF EXISTS " + Items.TABLE_NAME;

    public PodcastDatabaseHelper(Context appContext) {
        super(appContext, dbName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_SUBSCRIPTIONS);
        db.execSQL(SQL_CREATE_ITEMS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_SUBSCRIPTIONS);
        db.execSQL(SQL_DELETE_ITEMS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
