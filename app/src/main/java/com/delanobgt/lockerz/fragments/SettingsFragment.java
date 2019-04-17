package com.delanobgt.lockerz.fragments;


import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import com.delanobgt.lockerz.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the Preferences from the XML file
        addPreferencesFromResource(R.xml.preferences);
    }

}
