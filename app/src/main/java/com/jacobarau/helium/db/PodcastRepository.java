package com.jacobarau.helium.db;

import com.jacobarau.helium.jdata.JDataList;
import com.jacobarau.helium.model.Item;
import com.jacobarau.helium.model.Subscription;

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
        podcastDatabase.save(subscription);
    }

    public void save(Subscription subscription) {
        podcastDatabase.save(subscription);
    }
}
