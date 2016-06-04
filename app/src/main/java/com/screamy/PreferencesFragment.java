package com.screamy;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by benkaufman on 22/05/2016.
 */

public class PreferencesFragment extends PreferenceFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        addPreferencesFromResource(R.xml.pref_general);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
