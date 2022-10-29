package com.sneva.smoji.view.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;

import com.sneva.smoji.view.SmojiManager;
import com.sneva.smoji.view.SmojiUtils;
import com.sneva.smoji.view.R;
import com.sneva.smoji.view.adapters.FooterIconsAdapter;
import com.sneva.smoji.view.utils.Utils;

class FooterView extends EmojiLayout {
    public FooterView(Context context, EmojiPager pager, int Left) {
        super(context);

        this.Left = Left;
        this.pager = pager;
        init();
    }

    int Left;
    EmojiPager pager;
    RecyclerView icons;
    AppCompatImageView backSpace;
    AppCompatImageView leftIcon;


    private boolean backspacePressed;
    private boolean backspaceOnce;
    boolean backspaceEnabled = true;

    private void init() {
        int iconSize = Utils.dpToPx(getContext(), 24);
        backSpace = new AppCompatImageView(getContext()) {
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                if (!backspaceEnabled) return super.onTouchEvent(event);

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    backspacePressed = true;
                    backspaceOnce = false;
                    postBackspaceRunnable(350);
                } else if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) {
                    backspacePressed = false;
                    if (!backspaceOnce) {
                        SmojiUtils.backspace(editText);
                        backSpace.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

                    }
                }
                super.onTouchEvent(event);
                return true;
            }
        };

        this.addView(backSpace, new LayoutParams(
                getContext().getResources().getDisplayMetrics().widthPixels - Utils.dpToPx(getContext(), 38), Utils.dpToPx(getContext(), 10), iconSize, iconSize));

        Drawable back = ContextCompat.getDrawable(getContext(), R.drawable.emoji_backspace);
        DrawableCompat.setTint(DrawableCompat.wrap(back), SmojiManager.getEmojiViewTheme().getFooterItemColor());
        backSpace.setImageDrawable(back);
        Utils.setClickEffect(backSpace, true);

        backSpace.setOnClickListener(view -> {
            //if (pager.getEditText()!=null) AXEmojiUtils.backspace(pager.getEditText());
            if (pager.listener != null) pager.listener.onClick(view, false);
        });

        if (Left != -1) {
            leftIcon = new AppCompatImageView(getContext());
            this.addView(leftIcon, new LayoutParams(Utils.dpToPx(getContext(), 8), Utils.dpToPx(getContext(), 10), iconSize, iconSize));

            Drawable leftIconDr = AppCompatResources.getDrawable(getContext(), Left);
            DrawableCompat.setTint(DrawableCompat.wrap(leftIconDr), SmojiManager.getEmojiViewTheme().getFooterItemColor());
            leftIcon.setImageDrawable(leftIconDr);
            Utils.setClickEffect(leftIcon, true);

            leftIcon.setOnClickListener(view -> {
                if (pager.listener != null) pager.listener.onClick(view, true);
            });
        }

        icons = new RecyclerView(getContext());
        this.addView(icons, new EmojiLayout.LayoutParams(
                Utils.dpToPx(getContext(), 44), 0, getContext().getResources().getDisplayMetrics().widthPixels - Utils.dpToPx(getContext(), 88), -1));

        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);
        icons.setLayoutManager(lm);
        Utils.forceLTR(icons);

        icons.setItemAnimator(null);

        icons.setAdapter(new FooterIconsAdapter(pager));

        icons.setOverScrollMode(View.OVER_SCROLL_NEVER);
        if (icons.getLayoutParams().width > pager.getPagesCount() * Utils.dpToPx(getContext(), 40)) {
            icons.getLayoutParams().width = pager.getPagesCount() * Utils.dpToPx(getContext(), 40);
            ((LayoutParams) icons.getLayoutParams()).leftMargin = 0;
            ((LayoutParams) icons.getLayoutParams()).gravity = Gravity.CENTER_HORIZONTAL;
            this.requestLayout();
        }

        this.setBackgroundColor(SmojiManager.getEmojiViewTheme().getFooterBackgroundColor());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.setElevation(Utils.dpToPx(getContext(), 2));
        }

        this.setOnClickListener(view -> {});
    }

    public void setPageIndex(int index) {
        icons.getAdapter().notifyDataSetChanged();
    }

    private void postBackspaceRunnable(final int time) {
        backSpace.postDelayed(() -> {
            if (!backspaceEnabled || !backspacePressed) return;

            SmojiUtils.backspace(editText);
            backSpace.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP);

            backspaceOnce = true;
            postBackspaceRunnable(Math.max(50, time - 100));
        }, time);
    }
}
