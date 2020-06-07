package com.android.inputmethodcommon;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.android.inputmethodcommon.InputMethodSettingsInterface;
public abstract class InputMethodSettingsFragment extends PreferenceFragment implements InputMethodSettingsInterface {
    private final InputMethodSettingsImpl mSettings = new InputMethodSettingsImpl();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = getActivity();
        setPreferenceScreen(getPreferenceManager().createPreferenceScreen(context));
        mSettings.init(context, getPreferenceScreen());
    }
    @Override
    public void setInputMethodSettingsCategoryTitle(int resId) { mSettings.setInputMethodSettingsCategoryTitle(resId); }
    @Override
    public void setInputMethodSettingsCategoryTitle(CharSequence title) { mSettings.setInputMethodSettingsCategoryTitle(title); }
    @Override
    public void setSubtypeEnablerTitle(int resId) {
        mSettings.setSubtypeEnablerTitle(resId);
    }
    @Override
    public void setSubtypeEnablerTitle(CharSequence title) { mSettings.setSubtypeEnablerTitle(title); }
    @Override
    public void setSubtypeEnablerIcon(int resId) {
        mSettings.setSubtypeEnablerIcon(resId);
    }
    @Override
    public void setSubtypeEnablerIcon(Drawable drawable) { mSettings.setSubtypeEnablerIcon(drawable); }
    @Override
    public void onResume() { super.onResume();mSettings.updateSubtypeEnabler(); }
}
