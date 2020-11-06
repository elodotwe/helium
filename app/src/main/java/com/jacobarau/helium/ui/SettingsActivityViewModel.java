package com.jacobarau.helium.ui;

import android.net.Uri;

import com.jacobarau.helium.db.PodcastRepository;

public class SettingsActivityViewModel {
    private PodcastRepository podcastRepository;

    public SettingsActivityViewModel(PodcastRepository podcastRepository) {
        this.podcastRepository = podcastRepository;
    }

    public void importOPML(Uri uri) {
        podcastRepository.importOPML(uri);
    }

    public void exportOPML(Uri uri) {
        podcastRepository.exportOPML(uri);
    }
}
