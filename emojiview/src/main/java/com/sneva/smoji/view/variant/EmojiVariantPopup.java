package com.sneva.smoji.view.variant;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.sneva.smoji.view.emoji.Emoji;
import com.sneva.smoji.view.listener.OnEmojiActions;
import com.sneva.smoji.view.view.EmojiImageView;

public abstract class EmojiVariantPopup {

    public EmojiVariantPopup(@NonNull final View rootView, @Nullable final OnEmojiActions listener) {}

    public abstract boolean isShowing();

    public abstract void show(@NonNull final EmojiImageView clickedImage, @NonNull final Emoji emoji, boolean fromRecent);

    public abstract void dismiss();

    public boolean onTouch(MotionEvent event, RecyclerView recyclerView) {
        return false;
    }
}
