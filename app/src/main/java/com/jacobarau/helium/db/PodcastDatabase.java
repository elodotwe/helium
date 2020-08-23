package com.jacobarau.helium.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.jacobarau.helium.jdata.JDataList;
import com.jacobarau.helium.model.Item;
import com.jacobarau.helium.model.Subscription;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.jacobarau.helium.db.PodcastDatabaseContract.*;

public class PodcastDatabase {
    private ExecutorService databaseExecutor = Executors.newSingleThreadExecutor();
    private SQLiteDatabase database;
    private Handler handler;

    public final JDataList<Subscription> subscriptions = new JDataList<>();
    public final JDataList<Item> items = new JDataList<>();

    public PodcastDatabase(final Context appContext) {
        handler = new Handler(appContext.getMainLooper());
        databaseExecutor.submit(new Runnable() {
            @Override
            public void run() {
                PodcastDatabaseHelper helper = new PodcastDatabaseHelper(appContext);
                database = helper.getWritableDatabase();
                refreshSubscriptions();
                refreshItems();
            }
        });
    }

    private void refreshSubscriptions() {
        subscriptions.setValue(getSubscriptions());
    }

    private void refreshItems() {
        items.setValue(getItems());
    }

    public void save(final Subscription subscription) {
        save(subscription, null);
    }

    public void save(final Subscription subscription, final Runnable onCompleted) {
        databaseExecutor.submit(new Runnable() {
            @Override
            public void run() {
                if (subscription.id != null) {
                    int affected = database.update(Subscriptions.TABLE_NAME,
                            subscriptionToValues(subscription),
                            Subscriptions._ID + " = ?", new String[]{String.valueOf(subscription.id)});
                    if (affected != 1) {
                        throw new RuntimeException("Updating Subscription didn't affect 1 row as expected; sub: " + subscription);
                    }
                } else {
                    subscription.id = database.insertOrThrow(Subscriptions.TABLE_NAME, null, subscriptionToValues(subscription));
                }
                refreshSubscriptions();
                if (onCompleted != null) {
                    handler.post(onCompleted);
                }
            }
        });
    }

    public void delete(final List<Subscription> subscriptions) {
        databaseExecutor.submit(new Runnable() {
            @Override
            public void run() {
                for (Subscription subscription: subscriptions) {
                    database.delete(Subscriptions.TABLE_NAME, Subscriptions._ID + " = ?", new String[]{String.valueOf(subscription.id)});
                }
                refreshSubscriptions();
                // TODO validate that when you delete a subscription, its child items go away too
                refreshItems();
            }
        });
    }

    public void delete(final Subscription subscription) {
        List<Subscription> subscriptions = new ArrayList<>();
        subscriptions.add(subscription);
        delete(subscriptions);
    }

    private Date getDate(Cursor cursor, int columnIndex) {
        if (cursor.isNull(columnIndex)) {
            return null;
        }
        return new Date(cursor.getLong(columnIndex) * 1000);
    }

    private String getString(Cursor cursor, int columnIndex) {
        if (cursor.isNull(columnIndex)) {
            return null;
        }
        return cursor.getString(columnIndex);
    }

    private Integer getInteger(Cursor cursor, @SuppressWarnings("SameParameterValue") int columnIndex) {
        if (cursor.isNull(columnIndex)) {
            return null;
        }
        return cursor.getInt(columnIndex);
    }

    private List<Subscription> getSubscriptions() {
        Cursor cursor = database.query(Subscriptions.TABLE_NAME,
                new String[]{
                        Subscriptions._ID,
                        Subscriptions.COLUMN_NAME_URL,
                        Subscriptions.COLUMN_NAME_TITLE,
                        Subscriptions.COLUMN_NAME_DESCRIPTION,
                        Subscriptions.COLUMN_NAME_IMAGE_URL,
                        Subscriptions.COLUMN_NAME_LINK,
                        Subscriptions.COLUMN_NAME_LAST_UPDATED},
                null, null, null, null,
                Subscriptions.COLUMN_NAME_TITLE + " ASC");
        List<Subscription> subscriptions = new ArrayList<>();
        while (cursor.moveToNext()) {
            Subscription subscription = new Subscription(cursor.getString(1));
            subscription.id = cursor.getLong(0);
            subscription.title = getString(cursor, 2);
            subscription.description = getString(cursor, 3);
            subscription.imageUrl = getString(cursor, 4);
            subscription.link = getString(cursor, 5);
            subscription.lastUpdated = getDate(cursor, 6);
            subscriptions.add(subscription);
        }
        cursor.close();
        return subscriptions;
    }

    private Long dateToLong(Date date) {
        if (date == null) {
            return null;
        }
        return date.getTime() / 1000;
    }

    private ContentValues subscriptionToValues(Subscription subscription) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Subscriptions.COLUMN_NAME_DESCRIPTION, subscription.description);
        contentValues.put(Subscriptions.COLUMN_NAME_IMAGE_URL, subscription.imageUrl);
        contentValues.put(Subscriptions.COLUMN_NAME_LAST_UPDATED, dateToLong(subscription.lastUpdated));
        contentValues.put(Subscriptions.COLUMN_NAME_LINK, subscription.link);
        contentValues.put(Subscriptions.COLUMN_NAME_TITLE, subscription.title);
        contentValues.put(Subscriptions.COLUMN_NAME_URL, subscription.url);
        return contentValues;
    }

    private ContentValues itemToValues(Item item) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(PodcastDatabaseContract.Items.COLUMN_NAME_DESCRIPTION, item.description);
        contentValues.put(PodcastDatabaseContract.Items.COLUMN_NAME_ENCLOSURE_LENGTH_BYTES, item.enclosureLengthBytes);
        contentValues.put(PodcastDatabaseContract.Items.COLUMN_NAME_ENCLOSURE_MIME_TYPE, item.enclosureMimeType);
        contentValues.put(PodcastDatabaseContract.Items.COLUMN_NAME_ENCLOSURE_URL, item.enclosureUrl);
        contentValues.put(PodcastDatabaseContract.Items.COLUMN_NAME_PUBLISH_DATE, dateToLong(item.publishDate));
        contentValues.put(PodcastDatabaseContract.Items.COLUMN_NAME_SUBSCRIPTION_ID, item.subscriptionId);
        contentValues.put(PodcastDatabaseContract.Items.COLUMN_NAME_TITLE, item.title);
        return contentValues;
    }

    private List<Item> getItems() {
        Cursor cursor = database.query(PodcastDatabaseContract.Items.TABLE_NAME,
                new String[]{
                        PodcastDatabaseContract.Items._ID,
                        PodcastDatabaseContract.Items.COLUMN_NAME_SUBSCRIPTION_ID,
                        PodcastDatabaseContract.Items.COLUMN_NAME_TITLE,
                        PodcastDatabaseContract.Items.COLUMN_NAME_DESCRIPTION,
                        PodcastDatabaseContract.Items.COLUMN_NAME_PUBLISH_DATE,
                        PodcastDatabaseContract.Items.COLUMN_NAME_ENCLOSURE_URL,
                        PodcastDatabaseContract.Items.COLUMN_NAME_ENCLOSURE_LENGTH_BYTES,
                        PodcastDatabaseContract.Items.COLUMN_NAME_ENCLOSURE_MIME_TYPE},
                null, null, null, null,
                PodcastDatabaseContract.Items.COLUMN_NAME_TITLE + " ASC");
        List<Item> items = new ArrayList<>();
        while (cursor.moveToNext()) {
            Item item = new Item();
            item.id = cursor.getLong(0);
            item.subscriptionId = cursor.getLong(1);
            item.title = getString(cursor, 2);
            item.description = getString(cursor, 3);
            item.publishDate = getDate(cursor, 4);
            item.enclosureUrl = getString(cursor, 5);
            item.enclosureLengthBytes = getInteger(cursor, 6);
            item.enclosureMimeType = getString(cursor, 7);
            items.add(item);
        }
        cursor.close();
        return items;
    }

    public void save(final List<Item> items) {
        databaseExecutor.submit(new Runnable() {
            @Override
            public void run() {
                for (Item item: items) {
                    if (item.id != null) {
                        int affected = database.update(PodcastDatabaseContract.Items.TABLE_NAME,
                                itemToValues(item),
                                PodcastDatabaseContract.Items._ID + " = ?", new String[]{String.valueOf(item.id)});
                        if (affected != 1) {
                            throw new RuntimeException("Updating Item didn't affect 1 row as expected; item: " + item);
                        }
                    } else {
                        item.id = database.insertOrThrow(PodcastDatabaseContract.Items.TABLE_NAME, null, itemToValues(item));
                    }
                }
                refreshItems();
            }
        });
    }

    public void delete(final Item item) {
        databaseExecutor.submit(new Runnable() {
            @Override
            public void run() {
                database.delete(PodcastDatabaseContract.Items.TABLE_NAME, PodcastDatabaseContract.Items._ID + " = ?", new String[]{String.valueOf(item.id)});
                refreshItems();
            }
        });
    }

    public void deleteSubscriptionItems(final Subscription subscription) {
        databaseExecutor.submit(new Runnable() {
            @Override
            public void run() {
                database.delete(Items.TABLE_NAME, Items.COLUMN_NAME_SUBSCRIPTION_ID + " = ?", new String[]{String.valueOf(subscription.id)});
                refreshItems();
            }
        });
    }
}
