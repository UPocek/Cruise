package com.cruisemobile.cruise.tools;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cruisemobile.cruise.activities.NewRideActivity;

import java.util.Calendar;

public class MyTimePickerForFragments extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();

        int hour = c.get(Calendar.HOUR);
        int minutes = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getContext(), this, hour, minutes, true);

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        ((NewRideActivity) getActivity()).processTimePickerResult(hourOfDay, minute);
    }
}
