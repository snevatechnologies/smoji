package com.sneva.smoji.view.emoji;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

public interface EmojiCategory {

    @NonNull
    Emoji[] getEmojis();

    @DrawableRes
    int getIcon();

    CharSequence getTitle();
}
