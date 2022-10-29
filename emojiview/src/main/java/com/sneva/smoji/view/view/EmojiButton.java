package com.sneva.smoji.view.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;

import androidx.annotation.CallSuper;
import androidx.annotation.DimenRes;
import androidx.annotation.Px;
import androidx.appcompat.widget.AppCompatButton;

import android.text.SpannableStringBuilder;
import android.util.AttributeSet;

import com.sneva.smoji.view.SmojiManager;
import com.sneva.smoji.view.R;
import com.sneva.smoji.view.utils.Utils;

public class EmojiButton extends AppCompatButton {
    private float emojiSize;

    public EmojiButton(final Context context) {
        this(context, null);
    }

    public EmojiButton(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        final Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
        final float defaultEmojiSize = fontMetrics.descent - fontMetrics.ascent;

        if (attrs == null) {
            emojiSize = defaultEmojiSize;
        } else {
            final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AXEmojiButton);
            try {
                emojiSize = a.getDimension(R.styleable.AXEmojiButton_emojiSize, defaultEmojiSize);
            } finally {
                a.recycle();
            }
        }

        setText(getText());
    }

    public float getEmojiSize() {
        return emojiSize;
    }

    @Override
    @CallSuper
    public void setText(final CharSequence rawText, final BufferType type) {
        if (SmojiManager.isInstalled()) {
            final CharSequence text = rawText == null ? "" : rawText;
            final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
            final Paint.FontMetrics fontMetrics = getPaint().getFontMetrics();
            //final float defaultEmojiSize = fontMetrics.descent - fontMetrics.ascent;
            SmojiManager.getInstance().replaceWithImages(getContext(), this, spannableStringBuilder,
                    emojiSize>0 ? emojiSize : Utils.getDefaultEmojiSize(fontMetrics), fontMetrics);
            super.setText(spannableStringBuilder, type);
        }else{
            super.setText(rawText,type);
        }
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
