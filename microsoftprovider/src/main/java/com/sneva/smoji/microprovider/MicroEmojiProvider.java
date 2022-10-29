package com.sneva.smoji.microprovider;

import android.content.Context;

import com.sneva.smoji.view.R;
import com.sneva.smoji.view.emoji.Emoji;
import com.sneva.smoji.view.emoji.EmojiCategory;
import com.sneva.smoji.view.emoji.EmojiData;
import com.sneva.smoji.view.preset.PresetEmoji;
import com.sneva.smoji.view.preset.PresetEmojiCategory;
import com.sneva.smoji.view.preset.PresetEmojiProvider;

public final class MicroEmojiProvider extends PresetEmojiProvider {

    public MicroEmojiProvider(Context context) {
        super(context, new int[]{
                R.drawable.emoji_ios_category_people,
                R.drawable.emoji_ios_category_nature,
                R.drawable.emoji_ios_category_food,
                R.drawable.emoji_ios_category_activity,
                R.drawable.emoji_ios_category_travel,
                R.drawable.emoji_ios_category_objects,
                R.drawable.emoji_ios_category_symbols,
                R.drawable.emoji_ios_category_flags
        });
    }

    public MicroEmojiProvider(Context context, int[] icons) {
        super(context, icons);
    }

    @Override
    public EmojiData getEmojiData() {
        return MicroEmojiData.instance;
    }

    @Override
    protected EmojiCategory createCategory(int i, int icon) {
        return new MicroEmojiCategory(i, icon, getEmojiData());
    }

    public static class MicroEmojiCategory extends PresetEmojiCategory {
        public MicroEmojiCategory(int i, int icon, EmojiData emojiData) {
            super(i, icon, emojiData);
        }

        @Override
        protected Emoji createEmoji(String code, int category, int index, EmojiData emojiData){
            return new MicroEmoji(code, emojiData);
        }
    }

    public static class MicroEmoji extends PresetEmoji {
        public MicroEmoji(String code, EmojiData emojiData) {
            super(code, emojiData);
        }
    }
}

