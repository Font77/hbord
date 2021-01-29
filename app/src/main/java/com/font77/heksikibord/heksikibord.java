package com.font77.heksikibord;
import android.app.Dialog;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.IBinder;
import android.text.InputType;
import android.text.method.MetaKeyKeyListener;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import java.util.ArrayList;
import java.util.List;
public class heksikibord extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    static final boolean DEBUG = true;
    static final boolean PROCESS_HARD_KEYS = true;
    private InputMethodManager mInputMethodManager;private lAtinqibordviyu mInputView;private CandidateView mCandidateView;
    private CompletionInfo[] mCompletions;private StringBuilder mComposing = new StringBuilder();private boolean mPredictionOn;
    private boolean mCompletionOn;private int mLastDisplayWidth;private boolean mCapsLock;private long mLastShiftTime;private long mMetaState;
    private lAtinqibord mSymbolsKeyboard1;private lAtinqibord mSymbolsKeyboard2;private lAtinqibord mSymbolsKeyboard3;
    private lAtinqibord mCurKeyboard;private String mWordSeparators;
    @Override public void onCreate() {
        super.onCreate();
        mInputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        mWordSeparators = getResources().getString(R.string.word_separators);
    }
    @Override public void onInitializeInterface() {
        if (mSymbolsKeyboard1 != null) {
            int displayWidth = getMaxWidth();
            if (displayWidth == mLastDisplayWidth) return;
            mLastDisplayWidth = displayWidth;
        }
        mSymbolsKeyboard1 = new lAtinqibord(this, R.xml.symbols1);
//        mSymbolsKeyboard1 = new lAtinqibord(this, R.xml.indish1);
        mSymbolsKeyboard2 = new lAtinqibord(this, R.xml.symbols2);
        mSymbolsKeyboard3 = new lAtinqibord(this, R.xml.symbols3);
    }
    @Override public View onCreateInputView() {
        mInputView = (lAtinqibordviyu) getLayoutInflater().inflate(R.layout.input, null);
        mInputView.setOnKeyboardActionListener(this);
        setlAtinqibord(mSymbolsKeyboard1);
        return mInputView;
    }
    private void setlAtinqibord(lAtinqibord nextKeyboard) {
        final boolean shouldSupportLanguageSwitchKey = false ;
        // mInputMethodManager.shouldOfferSwitchingToNextInputMethod(getToken());
        nextKeyboard.setLanguageSwitchKeyVisibility(shouldSupportLanguageSwitchKey);
        mInputView.setKeyboard(nextKeyboard);
    }
    @Override public View onCreateCandidatesView() {
        mCandidateView = new CandidateView(this); mCandidateView.setService(this); return mCandidateView;
    }
    @Override public void onStartInput(EditorInfo attribute, boolean restarting) {
        super.onStartInput(attribute, restarting);
        mComposing.setLength(0);
        updateCandidates();
        if (!restarting) { mMetaState = 0; }// Clear shift states.
        mPredictionOn = false; mCompletionOn = false; mCompletions = null;
        switch (attribute.inputType & InputType.TYPE_MASK_CLASS) {
            case InputType.TYPE_CLASS_NUMBER: case InputType.TYPE_CLASS_DATETIME: mCurKeyboard = mSymbolsKeyboard3; break;
            case InputType.TYPE_CLASS_PHONE: mCurKeyboard = mSymbolsKeyboard3; break;
            case InputType.TYPE_CLASS_TEXT: mCurKeyboard = mSymbolsKeyboard1; mPredictionOn = true;
                int variation = attribute.inputType & InputType.TYPE_MASK_VARIATION;
                if (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD || variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    mPredictionOn = false;
                }
                if (variation == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS || variation == InputType.TYPE_TEXT_VARIATION_URI ||
					variation == InputType.TYPE_TEXT_VARIATION_FILTER) { mPredictionOn = false; }

                if ((attribute.inputType & InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE) != 0) {
                    mPredictionOn = false;
                    mCompletionOn = isFullscreenMode();
                }
                break;
            default: mCurKeyboard = mSymbolsKeyboard1; // updateShiftKeyState(attribute);
        }
        mCurKeyboard.setImeOptions(getResources(), attribute.imeOptions);
    }
    @Override public void onFinishInput() { super.onFinishInput();
        mComposing.setLength(0);
        updateCandidates();
        setCandidatesViewShown(false);
        mCurKeyboard = mSymbolsKeyboard1;
        if (mInputView != null) { mInputView.closing(); }
    }
    @Override public void onStartInputView(EditorInfo attribute, boolean restarting) {
        super.onStartInputView(attribute, restarting);
        setlAtinqibord(mCurKeyboard);
        mInputView.closing();
        final InputMethodSubtype subtype = mInputMethodManager.getCurrentInputMethodSubtype();
        mInputView.setSubtypeOnSpaceKey(subtype);
    }
    @Override
    public void onCurrentInputMethodSubtypeChanged(InputMethodSubtype subtype) {
        mInputView.setSubtypeOnSpaceKey(subtype);
    }
    @Override public void onUpdateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart, int candidatesEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd);
        if (mComposing.length() > 0 && (newSelStart != candidatesEnd || newSelEnd != candidatesEnd)) {
            mComposing.setLength(0);updateCandidates();InputConnection ic = getCurrentInputConnection();
            if (ic != null) ic.finishComposingText();
        }
    }
    @Override public void onDisplayCompletions(CompletionInfo[] completions) {
        if (mCompletionOn) {
            mCompletions = completions;
            if (completions == null) { setSuggestions(null, false, false); return; }
            List<String> stringList = new ArrayList<String>();
            for (int i = 0; i < completions.length; i++) {
                CompletionInfo ci = completions[i];
                if (ci != null) stringList.add(ci.getText().toString());
            }
            setSuggestions(stringList, true, true);
        }
    }
    private boolean translateKeyDown(int keyCode, KeyEvent event) {
        mMetaState = MetaKeyKeyListener.handleKeyDown(mMetaState, keyCode, event);
        int c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(mMetaState));
        mMetaState = MetaKeyKeyListener.adjustMetaAfterKeypress(mMetaState);
        InputConnection ic = getCurrentInputConnection();
        if (c == 0 || ic == null) { return false; }
        boolean dead = false;
        if ((c & KeyCharacterMap.COMBINING_ACCENT) != 0) {
           dead = true; c = c & KeyCharacterMap.COMBINING_ACCENT_MASK;
        }
        if (mComposing.length() > 0) {
            char accent = mComposing.charAt(mComposing.length() -1 );
            int composed = KeyEvent.getDeadChar(accent, c);
            if (composed != 0) { c = composed; mComposing.setLength(mComposing.length()-1); }
        }
        onKey(c, null);
        return true;
    }
    @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (event.getRepeatCount() == 0 && mInputView != null) {
                    if (mInputView.handleBack()) { return true; }
                }
                break;
            case KeyEvent.KEYCODE_DEL:
                if (mComposing.length() > 0) { onKey(Keyboard.KEYCODE_DELETE, null); return true; }
                break;
            case KeyEvent.KEYCODE_ENTER: return false;
            default:
                if (PROCESS_HARD_KEYS) {
                    if (keyCode == KeyEvent.KEYCODE_SPACE && (event.getMetaState()&KeyEvent.META_ALT_ON) != 0) {
                        InputConnection ic = getCurrentInputConnection();
                        if (ic != null) {
                            ic.clearMetaKeyStates(KeyEvent.META_ALT_ON);
                            keyDownUp(KeyEvent.KEYCODE_A); keyDownUp(KeyEvent.KEYCODE_N);
                            keyDownUp(KeyEvent.KEYCODE_D); keyDownUp(KeyEvent.KEYCODE_R);
                            keyDownUp(KeyEvent.KEYCODE_O); keyDownUp(KeyEvent.KEYCODE_I);
                            keyDownUp(KeyEvent.KEYCODE_D);
                            return true;
                        }
                    }
                    if (mPredictionOn && translateKeyDown(keyCode, event)) { return true; }
                }
        }

        return super.onKeyDown(keyCode, event);
    }
    @Override public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (PROCESS_HARD_KEYS) {
            if (mPredictionOn) {
                mMetaState = MetaKeyKeyListener.handleKeyUp(mMetaState, keyCode, event);
            }
        }
        return super.onKeyUp(keyCode, event);
    }
    private void commitTyped(InputConnection inputConnection) {
        if (mComposing.length() > 0) {
            inputConnection.commitText(mComposing, mComposing.length());
            mComposing.setLength(0);
            updateCandidates();
        }
    }
    private boolean isAlphabet(int code) {
        if (Character.isLetter(code)) { return true; } else { return false; }
    }
    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }
    private void sendKey(int keyCode) {
        switch (keyCode) {
            case '\n': keyDownUp(KeyEvent.KEYCODE_ENTER); break;
            default:
                if (keyCode >= '0' && keyCode <= '9') { keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0); }
                else { getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1); }
                break;
        }
    }
    public void onKey(int primaryCode, int[] keyCodes) {
        if (isWordSeparator(primaryCode)) {
            if (mComposing.length() > 0) { commitTyped(getCurrentInputConnection()); }
            sendKey(primaryCode);
        }
        else if (primaryCode == Keyboard.KEYCODE_DELETE) { handleBackspace(); }
        else if (primaryCode == Keyboard.KEYCODE_SHIFT) { handleShift(); }
        else if (primaryCode == Keyboard.KEYCODE_CANCEL) { handleClose(); return; }
        else if (primaryCode == lAtinqibordviyu.KEYCODE_LANGUAGE_SWITCH) { handleLanguageSwitch(); return; }
        else if (primaryCode == lAtinqibordviyu.KEYCODE_OPTIONS) {}
        else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE && mInputView != null) {
            Keyboard current = mInputView.getKeyboard();
            if (current == mSymbolsKeyboard1) setlAtinqibord(mSymbolsKeyboard2);
            else if (current == mSymbolsKeyboard2) setlAtinqibord(mSymbolsKeyboard3);
            else if (current == mSymbolsKeyboard3) setlAtinqibord(mSymbolsKeyboard1);
        } else handleCharacter(primaryCode, keyCodes);
    }
    public void onText(CharSequence text) {
        InputConnection ic = getCurrentInputConnection();
        if (ic == null) return;
        ic.beginBatchEdit();
        if (mComposing.length() > 0) { commitTyped(ic); }
        ic.commitText(text, 0);
        ic.endBatchEdit();
    }
    private void updateCandidates() {
        if (!mCompletionOn) {
            if (mComposing.length() > 0) {
                ArrayList<String> list = new ArrayList<String>();
                list.add(mComposing.toString());
                setSuggestions(list, true, true);
            } else { setSuggestions(null, false, false); }
        }
    }
    public void setSuggestions(List<String> suggestions, boolean completions, boolean typedWordValid) {
        if (suggestions != null && suggestions.size() > 0) { setCandidatesViewShown(true); }
        else if (isExtractViewShown()) { setCandidatesViewShown(true); }
        if (mCandidateView != null) {
            mCandidateView.setSuggestions(suggestions, completions, typedWordValid);
        }
    }
    private void handleBackspace() {
        final int length = mComposing.length();
        if (length > 1) {
            mComposing.delete(length - 1, length);
            getCurrentInputConnection().setComposingText(mComposing, 1);
            updateCandidates();
        } else if (length > 0) {
            mComposing.setLength(0);
            getCurrentInputConnection().commitText("", 0);
            updateCandidates();
        } else { keyDownUp(KeyEvent.KEYCODE_DEL); }
    }
    private void handleShift() {
        if (mInputView == null) return;
        Keyboard currentKeyboard = mInputView.getKeyboard();
        if (mSymbolsKeyboard3 == currentKeyboard) { checkToggleCapsLock();mInputView.setShifted(mCapsLock || !mInputView.isShifted());
        } else if (currentKeyboard == mSymbolsKeyboard3) {
            mSymbolsKeyboard3.setShifted(true);
            setlAtinqibord(mSymbolsKeyboard1);
            mSymbolsKeyboard1.setShifted(true);
        } else if (currentKeyboard == mSymbolsKeyboard1) {
            mSymbolsKeyboard1.setShifted(false);
            setlAtinqibord(mSymbolsKeyboard3);
            mSymbolsKeyboard3.setShifted(false);
        }
    }
    private void handleCharacter(int primaryCode, int[] keyCodes) {
        if (isInputViewShown()) {
            if (mInputView.isShifted()) { primaryCode = Character.toUpperCase(primaryCode); }
        }
        mComposing.append((char) primaryCode);
        getCurrentInputConnection().setComposingText(mComposing, 1);
        updateCandidates();
    }
    private void handleClose() {
        commitTyped(getCurrentInputConnection());
        requestHideSelf(0);
        mInputView.closing();
    }
    private IBinder getToken() {
        final Dialog dialog = getWindow(); if (dialog == null) { return null; }
        final Window window = dialog.getWindow(); if (window == null) { return null; }
        return window.getAttributes().token;
    }
    private void handleLanguageSwitch() {
        mInputMethodManager.switchToNextInputMethod(getToken(), false /* onlyCurrentIme */);
    }
    private void checkToggleCapsLock() { long now = System.currentTimeMillis();
        if (mLastShiftTime + 800 > now) { mCapsLock = !mCapsLock; mLastShiftTime = 0; }
        else { mLastShiftTime = now; }
    }
    private String getWordSeparators() { return mWordSeparators; }
    public boolean isWordSeparator(int code) { String separators = getWordSeparators();
        return separators.contains(String.valueOf((char)code));
    }
    public void pickDefaultCandidate() { pickSuggestionManually(0); }
    public void pickSuggestionManually(int index) {
        if (mCompletionOn && mCompletions != null && index >= 0 && index < mCompletions.length) {
            CompletionInfo ci = mCompletions[index];
            getCurrentInputConnection().commitCompletion(ci);
            if (mCandidateView != null) { mCandidateView.clear(); }
        } else if (mComposing.length() > 0) { commitTyped(getCurrentInputConnection()); }
    }
    public void swipeRight() { if (mCompletionOn) { pickDefaultCandidate(); } }
    public void swipeLeft() { handleBackspace(); }
    public void swipeDown() { handleClose(); }
    public void swipeUp() {}
    public void onPress(int primaryCode) {}
    public void onRelease(int primaryCode) {}
}
