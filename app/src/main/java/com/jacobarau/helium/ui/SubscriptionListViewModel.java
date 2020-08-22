package com.jacobarau.helium.ui;

import com.jacobarau.helium.data.JDataList;
import com.jacobarau.helium.data.JDataListListener;
import com.jacobarau.helium.db.PodcastRepository;
import com.jacobarau.helium.model.Subscription;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SubscriptionListViewModel {
    public final JDataList<Subscription> subscriptions;
    private PodcastRepository podcastRepository;

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

    }

    public void unsubscribeFrom(List<Subscription> subscriptions) {

    }

    public void updatePodcasts() {

    }
}
