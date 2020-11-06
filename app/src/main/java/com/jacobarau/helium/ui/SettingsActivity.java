package com.jacobarau.helium.ui;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jacobarau.helium.HeliumApplication;
import com.jacobarau.helium.R;

public class SettingsActivity extends FilePickerActivity {
    private ListView listView;
    private SettingsAdapter settingsAdapter;
    private SettingsItem[] settingsItems = {
            SettingsItem.OPML_EXPORT,
            SettingsItem.OPML_IMPORT
    };
    private SettingsActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = HeliumApplication.wiring.provideSettingsActivityViewModel();
        setContentView(R.layout.activity_settings);
        listView = findViewById(R.id.settingsList);
        settingsAdapter = new SettingsAdapter();
        listView.setAdapter(settingsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (settingsItems[position]) {
                    case OPML_IMPORT:
                        pickOpen(new PickerListener() {
                            @Override
                            public void onURI(Uri uri) {
                                viewModel.importOPML(uri);
                            }
                        });
                        break;
                    case OPML_EXPORT:
                        pickSave(new PickerListener() {
                            @Override
                            public void onURI(Uri uri) {
                                viewModel.exportOPML(uri);
                            }
                        });
                }
            }
        });
    }

    private class SettingsAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return settingsItems.length;
        }

        @Override
        public Object getItem(int position) {
            return settingsItems[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = getLayoutInflater();
                convertView = inflater.inflate(R.layout.settings_item, parent, false);
            }
            SettingsItem item = settingsItems[position];

            TextView bigText = convertView.findViewById(R.id.settingName);
            bigText.setText(item.bigText);
            TextView description = convertView.findViewById(R.id.settingDescription);
            if (item.description == null) {
                description.setVisibility(View.GONE);
            } else {
                description.setVisibility(View.VISIBLE);
                description.setText(item.description);
            }

            ImageView icon = convertView.findViewById(R.id.imageView);
            if (item.icon == null) {
                icon.setVisibility(View.GONE);
            } else {
                icon.setVisibility(View.VISIBLE);
                icon.setImageResource(item.icon);
            }

            return convertView;
        }
    }
}

enum SettingsItem {
    OPML_EXPORT(R.string.setting_title_opml_export, R.string.setting_description_opml_export, android.R.drawable.ic_menu_upload),
    OPML_IMPORT(R.string.setting_title_opml_import, R.string.setting_description_opml_import, android.R.drawable.ic_menu_save);

    SettingsItem(int bigText, Integer description, Integer icon) {
        this.bigText = bigText;
        this.description = description;
        this.icon = icon;
    }

    int bigText;
    Integer description;
    Integer icon;
}