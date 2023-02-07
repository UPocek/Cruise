package com.cruisemobile.cruise.tools;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.cruisemobile.cruise.R;

public class ChangePassengerTransition {
    public static void to(Fragment newFragment, FragmentActivity activity) {
        to(newFragment, activity, true);
    }

    public static void to(Fragment newFragment, FragmentActivity activity, boolean addToBackstack) {
        FragmentTransaction transaction = activity.getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.fragmentPlaceholder, newFragment);
        if (addToBackstack) transaction.addToBackStack(null);
        transaction.commit();
    }

    public static void remove(FragmentActivity activity) {
        activity.getSupportFragmentManager().popBackStack();
    }
}
