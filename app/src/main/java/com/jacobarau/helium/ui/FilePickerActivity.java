package com.jacobarau.helium.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class FilePickerActivity extends Activity {
    private PickerListener listener;

    public interface PickerListener {
        void onURI(Uri uri);
    }

    public void pickOpen(PickerListener listener) {
        this.listener = listener;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,1);
    }

    public void pickSave(PickerListener listener) {
        this.listener = listener;
        // TODO implement solution for pre-KitKat.
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/xml");
        intent.putExtra(Intent.EXTRA_TITLE, "helium_feeds.opml");

        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            listener.onURI(data.getData());
        }
    }
}
