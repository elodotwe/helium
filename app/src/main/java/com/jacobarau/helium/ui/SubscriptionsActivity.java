package com.jacobarau.helium.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jacobarau.helium.HeliumApplication;
import com.jacobarau.helium.R;
import com.jacobarau.helium.jdata.JDataListListener;
import com.jacobarau.helium.model.Subscription;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionsActivity extends Activity {
    public static final String SUBSCRIPTION_ID = "com.jacobarau.mincast.SUBSCRIPTION_ID";
    private static final String TAG = "SubscriptionsActivity";
    private ListView listView;
    private SubscriptionListViewModel viewModel;
    private SubscriptionListAdapter subscriptionListAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.add_podcast:
                showAddPodcastDialog();
                break;
            case R.id.update_podcasts:
                viewModel.updatePodcasts();
                break;
            case R.id.settings:
                Intent intent = new Intent(SubscriptionsActivity.this, SettingsActivity.class);
                startActivity(intent);
                break;
            default:
                return false;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.podcasts_listview);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.main_activity_context, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.unsubscribe:
                        SparseBooleanArray positions = listView.getCheckedItemPositions();
                        List<Subscription> toDelete = new ArrayList<>(positions.size());
                        for (int i = 0; i < positions.size(); i++) {
                            if (positions.valueAt(i)) {
                                toDelete.add((Subscription)listView.getItemAtPosition(positions.keyAt(i)));
                            }
                        }
                        viewModel.unsubscribeFrom(toDelete);
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SubscriptionsActivity.this, EpisodesActivity.class);
                intent.putExtra(SUBSCRIPTION_ID, ((Subscription)listView.getItemAtPosition(position)).id);
                startActivity(intent);
            }
        });

        subscriptionListAdapter = new SubscriptionListAdapter();
        listView.setAdapter(subscriptionListAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewModel = HeliumApplication.wiring.provideSubscriptionListViewModel();
        viewModel.subscriptions.subscribe(subscriptionListAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewModel.subscriptions.unsubscribe(subscriptionListAdapter);
    }

    private void showAddPodcastDialog() {
        AddPodcastDialogFragment fragment = new AddPodcastDialogFragment();
        fragment.show(getFragmentManager(), "addPodcast");
    }

    private class SubscriptionListAdapter extends BaseAdapter implements JDataListListener<Subscription> {
        private List<Subscription> subscriptions = new ArrayList<>();

        @Override
        public int getCount() {
            return subscriptions.size();
        }

        @Override
        public Object getItem(int position) {
            return subscriptions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.podcasts_listview_item, parent, false);
            }
            TextView title = convertView.findViewById(R.id.podcast_title);
            title.setText(subscriptions.get(position).title);
            TextView detail = convertView.findViewById(R.id.podcast_summary);
            detail.setText(subscriptions.get(position).description);
            Button downloadButton = convertView.findViewById(R.id.podcast_download_button);
            downloadButton.setVisibility(View.GONE);

            return convertView;
        }

        @Override
        public void onAdd(Subscription newElement, int index) {

        }

        @Override
        public void onDelete(Subscription deletedElement, int index) {

        }

        @Override
        public void onDataUpdated(List<Subscription> value) {
            this.subscriptions = value;
            notifyDataSetChanged();
        }
    }
}
