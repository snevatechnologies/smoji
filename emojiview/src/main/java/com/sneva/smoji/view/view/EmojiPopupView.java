package com.sneva.smoji.view.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.autofill.AutofillManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.sneva.smoji.view.SmojiManager;
import com.sneva.smoji.view.listener.PopupListener;
import com.sneva.smoji.view.search.EmojiSearchView;
import com.sneva.smoji.view.utils.Utils;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.O;

@SuppressLint("ViewConstructor")
public class EmojiPopupView extends FrameLayout implements PopupInterface {
    static final int MIN_KEYBOARD_HEIGHT = 50;

    final View rootView;
    final Activity context;

    EmojiBase content;
    final EditText editText;

    boolean isPendingOpen;
    boolean isShowing = false;
    boolean isKeyboardOpen;

    int keyboardHeight = 0;
    int originalImeOptions = -1;
    EmojiSearchView searchView = null;

    public boolean isKeyboardOpen() {
        return isKeyboardOpen;
    }

    PopupListener listener = null;

    public void setPopupListener(PopupListener listener) {
        this.listener = listener;
    }

    ViewGroup ap;
    LayoutParams lp;

    public EmojiPopupView(ViewGroup view, final EmojiBase content) {
        super(content.getContext());
        Utils.forceLTR(view);
        popupWindowHeight = Utils.getKeyboardHeight(content.getContext(), 0);
        lp = new LayoutParams(-1, popupWindowHeight);
        lp.gravity = Gravity.BOTTOM;

        this.ap = view;
        this.context = Utils.asActivity(content.getContext());
        this.rootView = content.getEditText().getRootView();
        this.editText = content.getEditText();
        this.content = content;

        this.addView(content, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        if (content instanceof EmojiPager) {
            if (!((EmojiPager) content).isShowing) ((EmojiPager) content).show();
        }

        content.setBackgroundColor(SmojiManager.getEmojiViewTheme().getBackgroundColor());
        this.invalidate();
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
            inputMethodManager.showSoftInput(editText, InputMethodManager.RESULT_UNCHANGED_SHOWN, new ResultReceiver(new Handler(Looper.getMainLooper())) {
                @Override
                public void onReceiveResult(final int resultCode, final Bundle data) {
                }
            });
        }
    }

    void updateKeyboardStateOpened(final int keyboardHeight) {
        isKeyboardOpen = true;
        if (this.keyboardHeight <= 0 || needToOpen) {
            this.keyboardHeight = keyboardHeight;
            popupWindowHeight = keyboardHeight;
            Utils.updateKeyboardHeight(context, keyboardHeight);
        }
        lp.height = findHeight(popupWindowHeight);
        if (listener != null) listener.onKeyboardOpened(findHeightWithSearchView(popupWindowHeight));

        if (needToOpen) {
            this.setLayoutParams(lp);
            needToOpen = false;
            Utils.hideKeyboard(ap);

            /*if (isKeyboardOpen){
             //disable anim
             show();
             }else{
             show();
             }*/
            show();

        //} else {
            //disable anim
            //dismiss();
        }
    }

    void updateKeyboardState(int keyboardHeight){
        if (keyboardHeight > Utils.dpToPx(context, MIN_KEYBOARD_HEIGHT)) {
            updateKeyboardStateOpened(keyboardHeight);
        } else {
            updateKeyboardStateClosed();
        }
    }

    void updateKeyboardStateClosed() {
        isKeyboardOpen = false;
        isShowing = getParent()!=null && lp.height > MIN_KEYBOARD_HEIGHT;

        if (listener != null) {
            listener.onKeyboardClosed();
        }

        /*if (isShowing()) {
         dismiss();
         }*/
    }

    int popupWindowHeight;
    boolean needToOpen = false;

    private int animFrom;

    public void toggle() {
        SmojiManager.setUsingPopupWindow(false);
        hideSearchView(false);

        //if (isAnimRunning) return;
        if (!isShowing) {
            if (this.getParent() != null) {
                ap.removeView(this);
            }
            // this is needed because something might have cleared the insets listener
            if (popupWindowHeight > 0) {
                needToOpen = false;
                lp.height = findHeight(popupWindowHeight);
                ap.addView(this, lp);
                show();
            } else {
                needToOpen = true;
                ap.addView(this, lp);

                if (Utils.shouldOverrideRegularCondition(context, editText) && originalImeOptions == -1) {
                    originalImeOptions = editText.getImeOptions();
                }

                editText.setFocusableInTouchMode(true);
                editText.requestFocus();

                showAtBottomPending();
            }
        } else {
            dismiss();
        }
    }

    public void show() {
        SmojiManager.setUsingPopupWindow(false);
        hideSearchView(false);

        animFrom = lp.height;

        //if (isAnimRunning) return;
        if (popupWindowHeight == 0) {
            needToOpen = true;
            if (this.getParent() != null) {
                ap.removeView(this);
            }
            ap.addView(this, lp);

            if (Utils.shouldOverrideRegularCondition(context, editText) && originalImeOptions == -1) {
                originalImeOptions = editText.getImeOptions();
            }

            editText.setFocusableInTouchMode(true);
            editText.requestFocus();

            showAtBottomPending();
            return;
        }
        if (getParent() == null) {
            needToOpen = false;
            lp.height = findHeight(popupWindowHeight);
            ap.addView(this, lp);
            content.refresh();
        }
        isShowing = true;

        Utils.hideKeyboard(ap);
        showAtBottom();
    }


    public boolean isShowing() {
        return isShowing;
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
        hideSearchView(true);
    }

    public void dismiss() {
        hideSearchView(false);
        animFrom = lp.height;
        //if (isAnimRunning) return;
        anim(false);

        isShowing = false;
        content.dismiss();
        if (listener != null) listener.onDismiss();

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

    boolean ignoreCall = false;

    public void onlyHide() {
        ignoreCall = true;
        animFrom = lp.height;
        anim(false);

        isShowing = false;
        content.dismiss();
        hideSearchView(false);
        if (listener != null) listener.onDismiss();
    }

    void showAtBottom() {
        isPendingOpen = false;

        anim(true);
        if (listener != null) listener.onShow();
    }


    void anim(final boolean e) {
        if (e) {
            lp.height = findHeight(popupWindowHeight);
            this.setLayoutParams(lp);
            loadAnimation(animFrom - lp.height,0,false);
        } else {
            remove();
            loadAnimation(0,-lp.height,true);
        }
    }


    public int getPopupHeight() {
        return lp.height;
    }

    public void remove() {
        content.dismiss();
        //this.isAnimRunning = false;
        this.isShowing = false;

        //lp.height = 0;
        //if (this.getParent() != null) ap.removeView(this);

        if (listener != null) listener.onDismiss();
    }

    int maxHeight = -1,minHeight = -1;

    private int findHeight(int keyboardHeight){
        if (searchView!=null && searchView.isShowing()) return keyboardHeight;
        int h = keyboardHeight;
        if (minHeight!=-1) h = Math.max(minHeight,h);
        if (maxHeight!=-1) h = Math.min(maxHeight,h);
        //if (searchView!=null && searchView.isShowing()) h += searchView.getSearchViewHeight();
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
            h = height + searchView.getSearchViewHeight();
        }else {
            h = findHeight(height);
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

    public void hideSearchView(boolean l){
        if (searchView == null || !searchView.isShowing() || (searchViewValueAnimator!=null && searchViewValueAnimator.isRunning())) return;
        loadSearchViewAnimation(0,searchView.getSearchViewHeight(),true,l, (LayoutParams) searchView.getView().getLayoutParams());
    }

    private void removeSearchView(boolean l){
        try {
            if (searchView.getParent() != null)
                ap.removeView(searchView);
        }catch (Exception ignore){
        }
        searchView.hide();
        this.lp.topMargin = 0;
        if (lp.height!=0) lp.height = findHeight(popupWindowHeight);
        isShowing = lp.height>0;

        if (l && listener!=null) {
            if (isKeyboardOpen) {
                listener.onKeyboardOpened(popupWindowHeight);
            }else {
                if (!isShowing) {
                    listener.onDismiss();
                } else {
                    listener.onShow();
                }
            }
        }
        ap.requestLayout();
    }

    public void showSearchView(){
        if (searchView == null || searchView.isShowing() || searchView.getParent()!=null) return;
        LayoutParams lp = new LayoutParams(-1,searchView.getSearchViewHeight());
        lp.gravity = Gravity.TOP;
        this.lp.height = popupWindowHeight;
        this.lp.topMargin = lp.height;
        ap.addView(searchView,ap.indexOfChild(this),lp);
        loadSearchViewAnimation(searchView.getSearchViewHeight(),0,false,false, (LayoutParams) searchView.getView().getLayoutParams());
        searchView.show();
        ap.requestLayout();

        //if (listener!=null)
        //    listener.onViewHeightChanged(this.lp.height + lp.height);

        searchView.getSearchTextField().setFocusable(true);
        searchView.getSearchTextField().requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchView.getSearchTextField(), InputMethodManager.SHOW_IMPLICIT);
    }

    public boolean isShowingSearchView(){
        return searchView!=null && searchView.isShowing();
    }


    ValueAnimator valueAnimator = null;
    boolean animationEnabled = true;
    long animationDuration = 250;

    void loadAnimation(int from, int to, final boolean remove){
        try {
            if (valueAnimator != null && valueAnimator.isRunning()) {
                valueAnimator.end();
            }
        }catch (Exception ignore){}

        if (!animationEnabled || isKeyboardOpen) {
            lp.bottomMargin = to;
            if (remove) {
                lp.height = 0;
                ap.removeView(EmojiPopupView.this);
            }
            ap.requestLayout();
            return;
        }

        valueAnimator = ValueAnimator.ofInt(from,to);
        valueAnimator.addUpdateListener(valueAnimator -> {
            if (valueAnimator!=null && valueAnimator.getAnimatedValue()!=null) {
                lp.bottomMargin = (int) valueAnimator.getAnimatedValue();
                ap.requestLayout();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (remove) {
                    try {
                        lp.height = 0;
                        ap.removeView(EmojiPopupView.this);
                    }catch (Exception ignore){}
                }
            }
        });
        valueAnimator.setDuration(animationDuration);
        valueAnimator.start();
    }

    ValueAnimator searchViewValueAnimator = null;
    boolean searchViewAnimationEnabled = true;
    long searchViewAnimationDuration = animationDuration;

    void loadSearchViewAnimation(int from, int to, final boolean remove,final boolean l,final LayoutParams lp){
        try {
            if (valueAnimator != null && valueAnimator.isRunning()) {
                valueAnimator.end();
            }
            if (searchViewValueAnimator != null && searchViewValueAnimator.isRunning()) {
                searchViewValueAnimator.end();
            }
        }catch (Exception ignore){}

        if (!searchViewAnimationEnabled) {
            lp.topMargin = to;
            EmojiPopupView.this.lp.topMargin = lp.height - lp.topMargin;
            if (remove) {
                removeSearchView(l);
            }
            ap.requestLayout();
            return;
        }

        searchViewValueAnimator = ValueAnimator.ofInt(from,to);
        searchViewValueAnimator.addUpdateListener(valueAnimator -> {
            if (valueAnimator!=null && valueAnimator.getAnimatedValue()!=null) {
                lp.topMargin = (int) valueAnimator.getAnimatedValue();
                EmojiPopupView.this.lp.topMargin = lp.height - lp.topMargin;
                ap.requestLayout();
            }
        });
        searchViewValueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (remove) {
                    try {
                       removeSearchView(l);
                    }catch (Exception ignore){}
                }
            }
        });
        searchViewValueAnimator.setDuration(searchViewAnimationDuration);
        searchViewValueAnimator.start();
    }
}
