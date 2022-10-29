package com.sneva.smoji.view.preset;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.sneva.smoji.view.emoji.Emoji;
import com.sneva.smoji.view.emoji.EmojiData;

@SuppressWarnings("serial")
public class PresetEmoji extends Emoji {

    public PresetEmoji(final String code, final EmojiData emojiData) {
        super(code, -1);

        boolean isVariants = emojiData.isColoredEmoji(code);
        if (isVariants) {
            PresetEmojiLoader.globalQueue.postRunnable(() -> {
                String[] vars = emojiData.getEmojiVariants(code);
                PresetEmoji[] variants = new PresetEmoji[vars.length];
                for (int i = 0; i < vars.length; i++) {
                    variants[i] = new PresetEmoji(vars[i], -1, 0);
                }
                setVariants(variants);
            });
        }
    }

    private PresetEmoji(String code, int resource, int count) {
        super(code, resource, new Emoji[count]);
    }

    @Override
    public Drawable getDrawable(final View view) {
        return PresetEmojiLoader.getEmojiBigDrawable(getUnicode());
    }

    @Override
    public Drawable getDrawable(final Context context) {
        return PresetEmojiLoader.getEmojiBigDrawable(getUnicode());
    }

    public Drawable getDrawable() {
        return PresetEmojiLoader.getEmojiBigDrawable(getUnicode());
    }

    public Drawable getDrawable(int size, boolean fullSize) {
        return PresetEmojiLoader.getEmojiDrawable(getUnicode(), size, fullSize);
    }

    @Override
    public Bitmap getEmojiBitmap() {
        return PresetEmojiLoader.getEmojiBitmap(getUnicode());
    }

    @Override
    public boolean isLoading() {
        return getEmojiBitmap() == null;
    }
}
