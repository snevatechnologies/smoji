package com.sneva.smoji.view.preset;

import androidx.annotation.NonNull;

import com.sneva.smoji.view.emoji.Emoji;
import com.sneva.smoji.view.emoji.EmojiCategory;
import com.sneva.smoji.view.emoji.EmojiData;

public class PresetEmojiCategory implements EmojiCategory {
    public Emoji[] emojiData;
    public String title;
    int icon;

    public PresetEmojiCategory(int i, int icon, EmojiData emojiData) {
        String[][] dataColored = emojiData.getDataColored();
        this.emojiData = new Emoji[dataColored[i].length];
        for (int j = 0; j < dataColored[i].length; j++)
            this.emojiData[j] = createEmoji(dataColored[i][j], i, j, emojiData);
        title = emojiData.getTitle(i);
        this.icon = icon;
    }

    protected Emoji createEmoji(String code, int category, int index, EmojiData emojiData){
        return new PresetEmoji(code, emojiData);
    }

    @NonNull
    @Override
    public Emoji[] getEmojis() {
        return emojiData;
    }

    @Override
    public int getIcon() {
        return icon;
    }

    @Override
    public CharSequence getTitle() {
        return title;
    }
}