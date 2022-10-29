package com.sneva.smoji.view.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.view.autofill.AutofillManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.sneva.smoji.view.SmojiManager;
import com.sneva.smoji.view.listener.PopupListener;
import com.sneva.smoji.view.search.EmojiSearchView;
import com.sneva.smoji.view.utils.EmojiResultReceiver;
import com.sneva.smoji.view.utils.Utils;


import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.LOLLIPOP;
import static android.os.Build.VERSION_CODES.O;

public final class EmojiPopup implements EmojiResultReceiver.Receiver, PopupInterface {
    static final int MIN_KEYBOARD_HEIGHT = 50;

    final View rootView;
    final Activity context;

    final PopupWindow popupWindow;
    final FrameLayout ap;
    EmojiBase content;
    EmojiSearchView searchView = null;

    final EditText editText;

    boolean isPendingOpen;
    boolean isKeyboardOpen;

    int keyboardHeight;
    int maxHeight = -1,minHeight = -1;
    int originalImeOptions = -1;

    final EmojiResultReceiver emojiResultReceiver = new EmojiResultReceiver(new Handler(Looper.getMainLooper()));

    final ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = this::updateKeyboardState;

    final View.OnAttachStateChangeListener onAttachStateChangeListener = new View.OnAttachStateChangeListener() {
        @Override
        public void onViewAttachedToWindow(final View v) {
            start();
        }

        @Override
        public void onViewDetachedFromWindow(final View v) {
            stop();

            popupWindow.setOnDismissListener(null);

            if (SDK_INT < LOLLIPOP) {
                rootView.getViewTreeObserver().removeGlobalOnLayoutListener(onGlobalLayoutListener);
            }

            rootView.removeOnAttachStateChangeListener(this);
        }
    };

    PopupListener listener = null;

    public void setPopupListener(PopupListener listener) {
        this.listener = listener;
    }

    public EmojiPopup(final EmojiBase content) {
        this.context = Utils.asActivity(content.getContext());
        this.rootView = content.getEditText().getRootView();
        this.editText = content.getEditText();
        this.content = content;
        this.content.setPopupInterface(this);
        this.keyboardHeight = Utils.getKeyboardHeight(context, 0);
        popupWindow = new PopupWindow(context);

        ap = new FrameLayout(context);
        ap.addView(content,new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
        ((FrameLayout.LayoutParams) content.getLayoutParams()).gravity = Gravity.BOTTOM;

        popupWindow.setContentView(ap);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null)); // To avoid borders and overdraw.
        popupWindow.setOnDismissListener(() -> {
            if (listener != null) listener.onDismiss();
        });

        if (content instanceof EmojiPager) {
            if (!((EmojiPager) content).isShowing) ((EmojiPager) content).show();
        }

        content.setBackgroundColor(SmojiManager.getEmojiViewTheme().getBackgroundColor());

        rootView.addOnAttachStateChangeListener(onAttachStateChangeListener);

        if (keyboardHeight >= MIN_KEYBOARD_HEIGHT) {
            popupWindow.setHeight(findHeightWithSearchView(keyboardHeight));
        }
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    void updateKeyboardState() {
        final int keyboardHeight = Utils.getInputMethodHeight(context, rootView);
        this.keyboardHeight = keyboardHeight;

        if (keyboardHeight > Utils.dpToPx(context, MIN_KEYBOARD_HEIGHT)) {
            updateKeyboardStateOpened(keyboardHeight);
        } else {
            updateKeyboardStateClosed();
        }
    }

    void start() {
        if (SDK_INT >= LOLLIPOP) {
            context.getWindow().getDecorView().setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                int previousOffset;

                @Override
                public WindowInsets onApplyWindowInsets(final View v, final WindowInsets insets) {
                    final int offset;

                    if (insets.getSystemWindowInsetBottom() < insets.getStableInsetBottom()) {
                        offset = insets.getSystemWindowInsetBottom();
                    } else {
                        offset = insets.getSystemWindowInsetBottom() - insets.getStableInsetBottom();
                    }

                    if (offset != previousOffset || offset == 0) {
                        previousOffset = offset;

                        if (offset > Utils.dpToPx(context, MIN_KEYBOARD_HEIGHT)) {
                            updateKeyboardStateOpened(offset);
                        } else {
                            updateKeyboardStateClosed();
                        }
                    }

                    return context.getWindow().getDecorView().onApplyWindowInsets(insets);
                }
            });
        } else {
            rootView.getViewTreeObserver().removeGlobalOnLayoutListener(onGlobalLayoutListener);
            rootView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
        }
    }

    void stop() {
        dismiss();

        if (SDK_INT >= LOLLIPOP) {
            context.getWindow().getDecorView().setOnApplyWindowInsetsListener(null);
        }
    }

    void updateKeyboardStateOpened(final int keyboardHeight) {
        if (popupWindowHeight <= 0) {
            popupWindowHeight = keyboardHeight;
        }

        if (popupWindow.getHeight() != popupWindowHeight) {
            popupWindow.setHeight(findHeightWithSearchView(popupWindowHeight));
        }

        final int properWidth = Utils.getProperWidth(context);

        if (popupWindow.getWidth() != properWidth) {
            popupWindow.setWidth(properWidth);
        }

        Utils.updateKeyboardHeight(context, keyboardHeight);
        if (!isKeyboardOpen) {
            isKeyboardOpen = true;
            if (listener != null) {
                listener.onKeyboardOpened(keyboardHeight);
            }
        }

        if (isPendingOpen) {
            showAtBottom();
        }
    }

    void updateKeyboardStateClosed() {
        isKeyboardOpen = false;

        if (listener != null) {
            listener.onKeyboardClosed();
        }

        if (isShowing()) {
            dismiss();
        }
    }

    int popupWindowHeight = 0;

    public void toggle() {
        SmojiManager.setUsingPopupWindow(true);
        if (!popupWindow.isShowing()) {
            // this is needed because something might have cleared the insets listener
            start();
            show();
        } else {
            dismiss();
        }
    }

    public void show() {
        hideSearchView(false);
        SmojiManager.setUsingPopupWindow(true);
        content.refresh();

        if (Utils.shouldOverrideRegularCondition(context, editText) && originalImeOptions == -1) {
            originalImeOptions = editText.getImeOptions();
        }

        editText.setFocusableInTouchMode(true);
        editText.requestFocus();

        showAtBottomPending();
    }


    private void showAtBottomPending() {
        isPendingOpen = true;

        final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (Utils.shouldOverrideRegularCondition(context, editText)) {
            editText.setImeOptions(editText.getImeOptions() | EditorInfo.IME_FLAG_NO_EXTRACT_UI);
            if (inputMethodManager != null) {
                inputMethodManager.restartInput(editText);
            }
        }

        if (inputMethodManager != null) {
            emojiResultReceiver.setReceiver(this);
            inputMethodManager.showSoftInput(editText, InputMethodManager.RESULT_UNCHANGED_SHOWN, emojiResultReceiver);
        }
    }

    public boolean isShowing() {
        return popupWindow.isShowing();
    }

    @Override
    public boolean onBackPressed() {
        if (isShowingSearchView()){
            show();
            return true;
        }
        if (isShowing()) {
            dismiss();
            return true;
        }
        return false;
    }

    @Override
    public void reload() {
        dismiss();
    }

    public void dismiss() {
        hideSearchView(false);
        popupWindow.dismiss();
        content.dismiss();


        emojiResultReceiver.setReceiver(null);

        if (originalImeOptions != -1) {
            editText.setImeOptions(originalImeOptions);
            final InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

            if (inputMethodManager != null) {
                inputMethodManager.restartInput(editText);
            }

            if (SDK_INT >= O) {
                final AutofillManager autofillManager = context.getSystemService(AutofillManager.class);
                if (autofillManager != null) {
                    autofillManager.cancel();
                }
            }
        }
    }

    void showAtBottom() {
        isPendingOpen = false;
        if (popupWindow.isShowing()) return;
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        if (listener != null) listener.onShow();
    }

    @Override
    public void onReceiveResult(final int resultCode, final Bundle data) {
        if (resultCode == 0 || resultCode == 1) {
            showAtBottom();
        }
    }

    private int findHeight(int keyboardHeight){
        if (searchView!=null && searchView.isShowing()) return keyboardHeight;
        int h = keyboardHeight;
        if (minHeight!=-1) h = Math.max(minHeight,h);
        if (maxHeight!=-1) h = Math.min(maxHeight,h);
        return h;
    }

    public void setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public void setMinHeight(int minHeight) {
        this.minHeight = minHeight;
    }

    public int getMinHeight() {
        return minHeight;
    }

    private int findHeightWithSearchView(int height) {
        int h;
        if (searchView!=null && searchView.isShowing()) {
            h = searchView.getSearchViewHeight();
            content.getLayoutParams().height = -1;
        }else {
            h = findHeight(height);
            content.getLayoutParams().height = h;
        }
        return h;
    }

    public EmojiSearchView getSearchView() {
        return searchView;
    }

    public void setSearchView(EmojiSearchView searchView) {
        hideSearchView(true);
        this.searchView = searchView;
    }

    public void hideSearchView(){
        hideSearchView(true);
    }

    private void hideSearchView(boolean l){
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        if (searchView == null || !searchView.isShowing()) return;
        try {
            if (searchView.getParent() != null)
                ap.removeView(searchView);
        }catch (Exception ignore){
        }
        searchView.hide();

        if (l && listener!=null) {
            if (content.getLayoutParams().height == 0) {
                listener.onKeyboardClosed();
            } else {
                content.getLayoutParams().height = findHeight(popupWindowHeight);
                listener.onKeyboardOpened(content.getLayoutParams().height);
            }
        }
        ap.requestLayout();
    }

    public void showSearchView(){
        if (searchView == null || searchView.isShowing() || searchView.getParent()!=null) return;
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-1,searchView.getSearchViewHeight());
        lp.gravity = Gravity.BOTTOM;
        content.getLayoutParams().height = -1;
        ap.addView(searchView,lp);
        searchView.show();
        popupWindow.dismiss();
        popupWindow.setHeight(lp.height);
        popupWindow.setFocusable(true);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_FROM_FOCUSABLE);
        popupWindow.update();
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        ap.requestLayout();

        if (listener!=null)
            listener.onKeyboardOpened(popupWindowHeight + lp.height);


        searchView.getSearchTextField().setFocusable(true);
        searchView.getSearchTextField().requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(searchView.getSearchTextField(), InputMethodManager.SHOW_FORCED);
        }
    }

    public boolean isShowingSearchView(){
        return searchView!=null && searchView.isShowing();
    }
}
