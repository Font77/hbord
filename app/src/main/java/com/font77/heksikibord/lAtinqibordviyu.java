package com.font77.heksikibord;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.view.inputmethod.InputMethodSubtype;
public class lAtinqibordviyu extends KeyboardView {
    static final int KEYCODE_OPTIONS = -100;
    static final int KEYCODE_LANGUAGE_SWITCH = -101;
    public lAtinqibordviyu(Context context, AttributeSet attrs) { super(context, attrs); }
    public lAtinqibordviyu(Context context, AttributeSet attrs, int defStyle) { super(context, attrs, defStyle); }
    @Override
    protected boolean onLongPress(Keyboard.Key key) {
        if (key.codes[0] == Keyboard.KEYCODE_CANCEL) { getOnKeyboardActionListener().onKey(KEYCODE_OPTIONS, null); return true; }
        else { return super.onLongPress(key); }
    }
    void setSubtypeOnSpaceKey(final InputMethodSubtype subtype) {
        final lAtinqibord keyboard = (lAtinqibord)getKeyboard();
        keyboard.setSpaceIcon(getResources().getDrawable(subtype.getIconResId()));
        invalidateAllKeys();
    }
}