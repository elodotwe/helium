package com.jacobarau.helium.ui;

import android.app.Activity;
import android.os.Bundle;

import com.jacobarau.helium.R;
import com.jacobarau.helium.model.Item;

import java.util.List;

public class EpisodesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodes);
        Long id = savedInstanceState.getLong(SubscriptionsActivity.SUBSCRIPTION_ID);
    }

    public void onEpisodesListChanged(List<Item> items) {

    }
}
