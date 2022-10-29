package com.sneva.smoji.view.listener;

import android.view.View;

import com.sneva.smoji.view.emoji.Emoji;

public interface OnEmojiActions {
    void onClick(View view, Emoji emoji, boolean fromRecent, boolean fromVariant);

    boolean onLongClick(View view, Emoji emoji, boolean fromRecent, boolean fromVariant);
}
