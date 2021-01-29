package com.font77.heksikibord;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.android.inputmethodcommon.InputMethodSettingsFragment;
public class ImePreferences extends PreferenceActivity {
    @Override public Intent getIntent() {
        final Intent modIntent = new Intent(super.getIntent());
        modIntent.putExtra(EXTRA_SHOW_FRAGMENT, Settings.class.getName());
        modIntent.putExtra(EXTRA_NO_HEADERS, true);
        return modIntent;
    }
    @Override protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState); setTitle(R.string.settings_name);
    }
    @Override protected boolean isValidFragment(final String fragmentName) {
        return Settings.class.getName().equals(fragmentName);
    }
    public static class Settings extends InputMethodSettingsFragment {
        @Override public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setInputMethodSettingsCategoryTitle(R.string.language_selection_title);
            setSubtypeEnablerTitle(R.string.select_language);
            addPreferencesFromResource(R.xml.ime_preferences);
        }
    }
}
