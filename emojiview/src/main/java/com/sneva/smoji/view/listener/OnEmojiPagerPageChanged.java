package com.sneva.smoji.view.listener;

import com.sneva.smoji.view.view.EmojiBase;
import com.sneva.smoji.view.view.EmojiPager;

public interface OnEmojiPagerPageChanged {
    void onPageChanged(EmojiPager emojiPager, EmojiBase base, int position);
}
