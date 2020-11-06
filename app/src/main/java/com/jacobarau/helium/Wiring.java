package com.jacobarau.helium;

import android.content.Context;

import com.jacobarau.helium.db.PodcastDatabase;
import com.jacobarau.helium.db.PodcastRepository;
import com.jacobarau.helium.ui.SettingsActivity;
import com.jacobarau.helium.ui.SettingsActivityViewModel;
import com.jacobarau.helium.ui.SubscriptionListViewModel;

public class Wiring {
    public final PodcastRepository podcastRepository;
    public final Context appContext;
    private SubscriptionListViewModel subscriptionListViewModel;
    private SettingsActivityViewModel settingsActivityViewModel;

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

    // TODO probably should think about what this is actually retaining.
    // Can easily be a memory leak. But I do not care right now.
    public SettingsActivityViewModel provideSettingsActivityViewModel() {
        if (settingsActivityViewModel == null) {
            settingsActivityViewModel = new SettingsActivityViewModel(podcastRepository);
        }
        return settingsActivityViewModel;
    }
}
