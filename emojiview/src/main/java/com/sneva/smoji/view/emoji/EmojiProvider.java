package com.sneva.smoji.view.emoji;

import androidx.annotation.NonNull;

public interface EmojiProvider {
    @NonNull
    EmojiCategory[] getCategories();

    void destroy();

    EmojiData getEmojiData();
}
