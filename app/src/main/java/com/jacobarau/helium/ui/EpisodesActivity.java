package com.jacobarau.helium.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jacobarau.helium.HeliumApplication;
import com.jacobarau.helium.R;
import com.jacobarau.helium.jdata.JDataListListener;
import com.jacobarau.helium.model.Item;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class EpisodesActivity extends Activity {

    private SubscriptionListViewModel viewModel;
    private EpisodeAdapter episodeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodes);
        Long id = getIntent().getExtras().getLong(SubscriptionsActivity.SUBSCRIPTION_ID);
        episodeAdapter = new EpisodeAdapter();
        viewModel = HeliumApplication.wiring.provideSubscriptionListViewModel();
        viewModel.subscribeToItemsForSubscription(id, episodeAdapter);
        ListView listView = findViewById(R.id.episode_list);
        listView.setAdapter(episodeAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.unsubscribeItemListener(episodeAdapter);
    }

    private class EpisodeAdapter extends BaseAdapter implements JDataListListener<Item> {
        private List<Item> items = new ArrayList<>();
        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.podcasts_listview_item, parent, false);
            }
            TextView title = convertView.findViewById(R.id.podcast_title);
            title.setText(items.get(position).title);
            TextView detail = convertView.findViewById(R.id.podcast_summary);
            DateFormat dateFormat = DateFormat.getDateTimeInstance();
            detail.setText(dateFormat.format(items.get(position).publishDate));

            Button downloadButton = convertView.findViewById(R.id.podcast_download_button);
            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = items.get(position).enclosureUrl;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });

            return convertView;
        }

        @Override
        public void onAdd(Item newElement, int index) {

        }

        @Override
        public void onDelete(Item deletedElement, int index) {

        }

        @Override
        public void onDataUpdated(List<Item> value) {
            this.items = value;
            notifyDataSetChanged();
        }
    }
}
