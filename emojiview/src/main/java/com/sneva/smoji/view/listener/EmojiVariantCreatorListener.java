package com.sneva.smoji.view.listener;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sneva.smoji.view.variant.EmojiVariantPopup;

public interface EmojiVariantCreatorListener {
    EmojiVariantPopup create(@NonNull final View rootView, @Nullable final OnEmojiActions listener);
}
