package com.jacobarau.helium.db;

import android.net.Uri;
import android.util.Log;

import com.jacobarau.helium.HeliumApplication;
import com.jacobarau.helium.jdata.JDataList;
import com.jacobarau.helium.model.Item;
import com.jacobarau.helium.model.Subscription;
import com.jacobarau.helium.update.UpdateService;

import java.util.List;

public class PodcastRepository {
    public final JDataList<Subscription> subscriptions;
    public final JDataList<Item> items;

    private PodcastDatabase podcastDatabase;

    public PodcastRepository(PodcastDatabase podcastDatabase) {
        subscriptions = podcastDatabase.subscriptions;
        items = podcastDatabase.items;
        this.podcastDatabase = podcastDatabase;
    }

    public void subscribeTo(String url) {
        Subscription subscription = new Subscription(url);
        podcastDatabase.save(subscription, new Runnable() {
            @Override
            public void run() {
                updatePodcasts();
            }
        });
    }

    public void save(Subscription subscription) {
        podcastDatabase.save(subscription);
    }

    public void unsubscribeFrom(List<Subscription> subscriptions) {
        podcastDatabase.delete(subscriptions);
    }

    public void unsubscribeFrom(Subscription subscription) {
        podcastDatabase.delete(subscription);
    }

    public void save(List<Item> items) {
        podcastDatabase.save(items);
    }

    public void deleteAllItems(Subscription subscription) {
        podcastDatabase.deleteSubscriptionItems(subscription);
    }

    public void updatePodcasts() {
        UpdateService.startUpdate(HeliumApplication.wiring.appContext);
    }

    public void importOPML(Uri toImport) {
        Log.i("foo", "importing opml at " + toImport);
    }

    public void exportOPML(Uri destination) {

    }
}
