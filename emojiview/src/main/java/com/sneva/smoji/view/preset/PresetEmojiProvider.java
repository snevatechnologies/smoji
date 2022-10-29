package com.sneva.smoji.view.preset;

import android.content.Context;

import androidx.annotation.NonNull;

import com.sneva.smoji.view.emoji.EmojiCategory;
import com.sneva.smoji.view.emoji.EmojiProvider;

public abstract class PresetEmojiProvider extends PresetEmojiReplacer implements EmojiProvider {

    public static EmojiCategory[] emojiCategories = null;

    public PresetEmojiProvider(Context context, int[] icons) {
        PresetEmojiLoader.init(context, getEmojiData());

        if (emojiCategories == null) {
            int len = getEmojiData().getCategoriesLength();
            emojiCategories = new EmojiCategory[len];
            for (int c = 0; c < len; c++) {
                emojiCategories[c] = createCategory(c, icons[c]);
            }
        }
    }

    @Override
    @NonNull
    public EmojiCategory[] getCategories() {
        return emojiCategories;
    }

    @Override
    public void destroy() {

    }

    protected EmojiCategory createCategory(int i, int icon) {
        return new PresetEmojiCategory(i, icon, getEmojiData());
    }

}
