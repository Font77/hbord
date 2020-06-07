package com.android.inputmethodcommon;
import android.graphics.drawable.Drawable;
public interface InputMethodSettingsInterface {
    public void setInputMethodSettingsCategoryTitle(int resId);
    public void setInputMethodSettingsCategoryTitle(CharSequence title);
    public void setSubtypeEnablerTitle(int resId);
    public void setSubtypeEnablerTitle(CharSequence title);
    public void setSubtypeEnablerIcon(int resId);
    public void setSubtypeEnablerIcon(Drawable drawable);
}

