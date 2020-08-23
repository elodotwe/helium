package com.jacobarau.helium.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.jacobarau.helium.HeliumApplication;
import com.jacobarau.helium.R;

public class AddPodcastDialogFragment extends DialogFragment {
    private EditText url;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.popup_add_podcast, null);

        url = view.findViewById(R.id.editPodcastURL);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.add_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        SubscriptionListViewModel viewModel = HeliumApplication.wiring.provideSubscriptionListViewModel();
                        viewModel.subscribeTo(url.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AddPodcastDialogFragment.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        populateURLFromClipboard();
    }

    private void populateURLFromClipboard() {
        ClipboardManager clipboardManager = (ClipboardManager) HeliumApplication.wiring.appContext.getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboardManager == null) return;

        ClipData clipData = clipboardManager.getPrimaryClip();
        if (clipData == null) return;

        CharSequence clipboardContent = clipData.getItemAt(0).coerceToText(HeliumApplication.wiring.appContext);
        // Crappy heuristics to detect if current clip "looks like" a url
        if (!clipboardContent.toString().startsWith("http")) return;

        url.setText(clipboardContent);
    }
}
