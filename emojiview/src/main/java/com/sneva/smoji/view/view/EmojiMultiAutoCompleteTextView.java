package com.sneva.smoji.view.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;

import androidx.annotation.CallSuper;
import androidx.annotation.DimenRes;
import androidx.annotation.Px;
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;

import android.util.AttributeSet;

import com.sneva.smoji.view.SmojiManager;
import com.sneva.smoji.view.SmojiUtils;
import com.sneva.smoji.view.R;
import com.sneva.smoji.view.emoji.Emoji;

/**
 * Reference implementation for an EmojiAutoCompleteTextView with emoji support.
 */
public class EmojiMultiAutoCompleteTextView extends AppCompatMultiAutoCompleteTextView {
    private float emojiSize;

    public EmojiMultiAutoCompleteTextView(final Context context) {
        this(context, null);
    }

    public EmojiMultiAutoCompleteTextView(final Context context, final AttributeSet attrs) {
        super(context, attrs);


        final Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
        final float defaultEmojiSize = fontMetrics.descent - fontMetrics.ascent;

        if (attrs == null) {
            emojiSize = defaultEmojiSize;
        } else {
            final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AXEmojiMultiAutoCompleteTextView);

            try {
                emojiSize = a.getDimension(R.styleable.AXEmojiMultiAutoCompleteTextView_emojiSize, defaultEmojiSize);
            } finally {
                a.recycle();
            }
        }

        setText(getText());
    }

    @Override
    @CallSuper
    protected void onTextChanged(final CharSequence text, final int start, final int lengthBefore, final int lengthAfter) {
        if (!SmojiManager.isInstalled()) return;
        final Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
        //final float defaultEmojiSize = fontMetrics.descent - fontMetrics.ascent;
        SmojiManager.getInstance().replaceWithImages(getContext(), this, getText(), emojiSize, fontMetrics);
    }

    @CallSuper
    public void backspace() {
        SmojiUtils.backspace(this);
    }

    @CallSuper
    public void input(final Emoji emoji) {
        SmojiUtils.input(this, emoji);
    }

    /**
     * returns the emoji size
     */
    public final float getEmojiSize() {
        return emojiSize;
    }

    /**
     * sets the emoji size in pixels and automatically invalidates the text and renders it with the new size
     */
    public final void setEmojiSize(@Px final int pixels) {
        setEmojiSize(pixels, true);
    }

    /**
     * sets the emoji size in pixels and automatically invalidates the text and renders it with the new size when {@code shouldInvalidate} is true
     */
    public final void setEmojiSize(@Px final int pixels, final boolean shouldInvalidate) {
        emojiSize = pixels;

        if (shouldInvalidate) {
            if (getText()!=null)
                setText(getText().toString());
        }
    }

    /**
     * sets the emoji size in pixels with the provided resource and automatically invalidates the text and renders it with the new size
     */
    public final void setEmojiSizeRes(@DimenRes final int res) {
        setEmojiSizeRes(res, true);
    }

    /**
     * sets the emoji size in pixels with the provided resource and invalidates the text and renders it with the new size when {@code shouldInvalidate} is true
     */
    public final void setEmojiSizeRes(@DimenRes final int res, final boolean shouldInvalidate) {
        setEmojiSize(getResources().getDimensionPixelSize(res), shouldInvalidate);
    }
}
