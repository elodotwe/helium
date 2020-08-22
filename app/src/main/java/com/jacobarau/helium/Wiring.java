package com.jacobarau.helium;

import android.content.Context;

import com.jacobarau.helium.db.PodcastDatabase;
import com.jacobarau.helium.db.PodcastRepository;
import com.jacobarau.helium.ui.SubscriptionListViewModel;

public class Wiring {
    public final PodcastRepository podcastRepository;
    public final Context appContext;
    private SubscriptionListViewModel subscriptionListViewModel;

    public Wiring(Context appContext) {
        this.appContext = appContext;
        podcastRepository = new PodcastRepository(
                new PodcastDatabase(appContext)
        );
    }

    public SubscriptionListViewModel provideSubscriptionListViewModel() {
        if (subscriptionListViewModel == null) {
            subscriptionListViewModel = new SubscriptionListViewModel(podcastRepository);
        }
        return subscriptionListViewModel;
    }
}
