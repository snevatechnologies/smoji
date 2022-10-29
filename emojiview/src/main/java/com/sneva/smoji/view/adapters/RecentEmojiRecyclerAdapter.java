package com.sneva.smoji.view.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sneva.smoji.view.SmojiManager;
import com.sneva.smoji.view.emoji.Emoji;
import com.sneva.smoji.view.listener.OnEmojiActions;
import com.sneva.smoji.view.shared.RecentEmoji;
import com.sneva.smoji.view.shared.VariantEmoji;
import com.sneva.smoji.view.utils.Utils;
import com.sneva.smoji.view.view.EmojiImageView;

public class RecentEmojiRecyclerAdapter extends RecyclerView.Adapter<RecentEmojiRecyclerAdapter.ViewHolder> {
    RecentEmoji recentEmoji;
    OnEmojiActions events;
    VariantEmoji variantEmoji;

    public RecentEmojiRecyclerAdapter(RecentEmoji recentEmoji, OnEmojiActions events, VariantEmoji variantEmoji) {
        this.recentEmoji = recentEmoji;
        this.events = events;
        this.variantEmoji = variantEmoji;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        FrameLayout frameLayout = new FrameLayout(viewGroup.getContext());
        EmojiImageView emojiView = new EmojiImageView(viewGroup.getContext());
        int cw = Utils.getColumnWidth(viewGroup.getContext());
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(cw, cw));
        frameLayout.addView(emojiView);

        int dp6 = Utils.dpToPx(viewGroup.getContext(), 6);
        emojiView.setPadding(dp6, dp6, dp6, dp6);

        return new ViewHolder(frameLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        FrameLayout frameLayout = (FrameLayout) viewHolder.itemView;
        final EmojiImageView emojiView = (EmojiImageView) frameLayout.getChildAt(0);

        Emoji emoji = variantEmoji.getVariant((Emoji) recentEmoji.getRecentEmojis().toArray()[i]);
        emojiView.setEmoji(emoji);
        emojiView.setOnEmojiActions(events, true);


        if (!SmojiManager.isRecentVariantEnabled()) {
            emojiView.setShowVariants(false);
        } else {
            emojiView.setShowVariants(SmojiManager.getEmojiViewTheme().isVariantDividerEnabled());
        }
    }

    @Override
    public int getItemCount() {
        return recentEmoji.getRecentEmojis().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
