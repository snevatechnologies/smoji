package com.sneva.smoji.view.preset;

import android.content.Context;
import android.graphics.Paint;
import android.text.Spannable;
import android.view.View;

import androidx.annotation.NonNull;

import com.sneva.smoji.view.SmojiManager;
import com.sneva.smoji.view.utils.EmojiRange;
import com.sneva.smoji.view.utils.EmojiReplacer;
import com.sneva.smoji.view.view.EmojiEditText;

import java.util.ArrayList;
import java.util.List;


public abstract class PresetEmojiReplacer implements EmojiReplacer {
    @Override
    public void replaceWithImages(final Context context, final View view, Spannable text, float emojiSize, Paint.FontMetrics fontMetrics) {
        //AXEmojiLoader.replaceEmoji(text,fontMetrics,(int) emojiSize,false);
        if (text.length() == 0) return;

        final SmojiManager emojiManager = SmojiManager.getInstance();
        final PresetEmojiLoader.EmojiSpan[] existingSpans = text.getSpans(0, text.length(), PresetEmojiLoader.EmojiSpan.class);
        final List<Integer> existingSpanPositions = new ArrayList<>(existingSpans.length);
        final int size = existingSpans.length;
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < size; i++) {
            existingSpanPositions.add(text.getSpanStart(existingSpans[i]));
        }

        List<EmojiRange> findAllEmojis = null;

        if (existingSpanPositions.size() == 0) {
            PresetEmojiLoader.replaceEmoji(text, fontMetrics, (int) emojiSize, false);
        } else {
            findAllEmojis = emojiManager.findAllEmojis(text);

            for (int i = 0; i < findAllEmojis.size(); i++) {
                final EmojiRange location = findAllEmojis.get(i);

                if (!existingSpanPositions.contains(location.start)) {
                    List<PresetEmojiLoader.SpanLocation> list = new ArrayList<>();
                    PresetEmojiLoader.replaceEmoji(location.emoji.getUnicode(), fontMetrics, (int) emojiSize, false, list);
                    for (PresetEmojiLoader.SpanLocation l : list) {
                        l.start = location.start + l.start;
                        l.end = location.start + l.end;
                        if (!existingSpanPositions.contains(l.start)) {
                            if (l.start == l.end)
                                continue;
                            text.setSpan(l.span, l.start, l.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }
                }
            }
        }

        if (view != null) {
            if (findAllEmojis == null) findAllEmojis = emojiManager.findAllEmojis(text);
            postInvalidate(view, findAllEmojis);
        }
    }

    private void postInvalidate(@NonNull final View view, @NonNull final List<EmojiRange> emojis) {
        if (!checkEmojisState(emojis)) {
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!checkEmojisState(emojis)) {
                        view.postDelayed(this, 20);
                    } else {
                        if (view instanceof EmojiEditText) {
                            try {
                                int start = ((EmojiEditText) view).getSelectionStart();
                                int end = ((EmojiEditText) view).getSelectionEnd();
                                ((EmojiEditText) view).setText(((EmojiEditText) view).getText());
                                ((EmojiEditText) view).setSelection(start, end);
                            } catch (Exception ignore) {
                                view.invalidate();
                            }
                        } else {
                            view.postInvalidate();
                        }
                    }
                }
            }, 20);
        }
    }

    private boolean checkEmojisState(@NonNull final List<EmojiRange> emojis) {
        for (int i = 0; i < emojis.size(); i++) {
            final EmojiRange location = emojis.get(i);
            if (location.emoji.isLoading()) return false;
        }
        return true;
    }
}
