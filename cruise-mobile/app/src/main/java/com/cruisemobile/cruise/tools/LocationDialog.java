package com.cruisemobile.cruise.tools;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;

import com.cruisemobile.cruise.R;

public class LocationDialog extends AlertDialog.Builder {
    public LocationDialog(Context context) {

        super(context);

        setUpDialog();
    }

    private void setUpDialog() {
        setTitle(R.string.oops);
        setMessage(R.string.location_disabled_message);
        setCancelable(false);

        setPositiveButton(R.string.sure, (dialog, id) -> {
            getContext().startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//				getContext().startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

            dialog.dismiss();
        });

        setNegativeButton(R.string.no, (dialog, id) -> {
            dialog.cancel();
        });

    }

    public AlertDialog prepareDialog() {
        AlertDialog dialog = create();
        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }
}

