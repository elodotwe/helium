package com.jacobarau.helium.ui;

import com.jacobarau.helium.HeliumApplication;
import com.jacobarau.helium.jdata.JDataList;
import com.jacobarau.helium.jdata.JDataListListener;
import com.jacobarau.helium.db.PodcastRepository;
import com.jacobarau.helium.model.Item;
import com.jacobarau.helium.model.Subscription;
import com.jacobarau.helium.update.UpdateService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class SubscriptionListViewModel {
    public final JDataList<Subscription> subscriptions;
    private PodcastRepository podcastRepository;
    private JDataListListener<Item> itemListener;

    public SubscriptionListViewModel(PodcastRepository podcastRepository) {
        subscriptions = new JDataList<>();
        this.podcastRepository = podcastRepository;
        podcastRepository.subscriptions.subscribe(new JDataListListener<Subscription>() {
            @Override
            public void onAdd(Subscription newElement, int index) {

            }

            @Override
            public void onDelete(Subscription deletedElement, int index) {

            }

            @Override
            public void onDataUpdated(List<Subscription> value) {
                Collections.sort(value, new Comparator<Subscription>() {
                    @Override
                    public int compare(Subscription o1, Subscription o2) {
                        String s1 = o1.title == null ? o1.url : o1.title;
                        String s2 = o2.title == null ? o2.url : o2.title;
                        return s1.compareTo(s2);
                    }
                });
                subscriptions.setValue(value);
            }
        });
    }

    public void subscribeTo(String url) {
        podcastRepository.subscribeTo(url);
    }

    public void unsubscribeFrom(Subscription subscription) {
        podcastRepository.unsubscribeFrom(subscription);
    }

    public void unsubscribeFrom(List<Subscription> subscriptions) {
        podcastRepository.unsubscribeFrom(subscriptions);
    }

    public void updatePodcasts() {
        UpdateService.startUpdate(HeliumApplication.wiring.appContext);
    }

    public void subscribeToItemsForSubscription(Subscription subscription, JDataListListener<Item> listener) {
        subscribeToItemsForSubscription(subscription.id, listener);
    }

    public void subscribeToItemsForSubscription(final long subscriptionID, final JDataListListener<Item> listener) {
        if (itemListener != null) {
            throw new RuntimeException("Only one item listener permitted at a time right now");
        }
        itemListener = new JDataListListener<Item>() {
            @Override
            public void onAdd(Item newElement, int index) {
                //TODO lol
            }

            @Override
            public void onDelete(Item deletedElement, int index) {
                //TODO why did i even write this list thingy
            }

            @Override
            public void onDataUpdated(List<Item> value) {
                List<Item> filtered = new ArrayList<>(value.size());
                for (Item item: value) {
                    if (item.subscriptionId.equals(subscriptionID)) {
                        filtered.add(item);
                    }
                }

                Collections.sort(filtered, new Comparator<Item>() {
                    @Override
                    public int compare(Item o1, Item o2) {
                        return o2.publishDate.compareTo(o1.publishDate);
                    }
                });

                listener.onDataUpdated(filtered);
            }
        };
        podcastRepository.items.subscribe(itemListener);
    }

    public void unsubscribeItemListener(JDataListListener<Item> listener) {
        podcastRepository.items.unsubscribe(listener);
        this.itemListener = null;
    }
}
