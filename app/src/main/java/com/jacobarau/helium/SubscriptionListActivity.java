package com.jacobarau.helium;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.jacobarau.helium.data.JDataListener;

public class SubscriptionListActivity extends Activity {
    TextView textView;
    JDataListener<String> listener = new JDataListener<String>() {
        @Override
        public void onDataUpdated(String value) {
            textView.setText(value);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription_list);
        textView = findViewById(R.id.textView);

    }

    @Override
    protected void onResume() {
        super.onResume();
        HeliumApplication.data.subscribe(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        HeliumApplication.data.unsubscribe(listener);
    }
}