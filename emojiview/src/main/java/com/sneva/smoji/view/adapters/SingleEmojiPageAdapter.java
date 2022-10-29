package com.sneva.smoji.view.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sneva.smoji.view.SmojiManager;
import com.sneva.smoji.view.emoji.Emoji;
import com.sneva.smoji.view.emoji.EmojiCategory;
import com.sneva.smoji.view.listener.OnEmojiActions;
import com.sneva.smoji.view.shared.RecentEmoji;
import com.sneva.smoji.view.shared.VariantEmoji;
import com.sneva.smoji.view.utils.Utils;
import com.sneva.smoji.view.view.EmojiImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SingleEmojiPageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    EmojiCategory[] categories;
    RecentEmoji recentEmoji;
    VariantEmoji variantEmoji;
    OnEmojiActions events;
    public List<Integer> titlesPosition = new ArrayList<>();
    List<Emoji> emojis = new ArrayList<>();

    public int getLastEmojiCategoryCount() {
        return categories[categories.length - 1].getEmojis().length;
    }

    public int getFirstTitlePosition() {
        return titlesPosition.get(0);
    }

    public SingleEmojiPageAdapter(EmojiCategory[] categories, OnEmojiActions events, RecentEmoji recentEmoji, VariantEmoji variantEmoji) {
        this.categories = categories;
        this.recentEmoji = recentEmoji;
        this.variantEmoji = variantEmoji;
        this.events = events;
        calItemsCount();
    }

    public void refresh() {
        titlesPosition.clear();
        emojis.clear();
        calItemsCount();
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == 1) {
            FrameLayout frameLayout = new FrameLayout(viewGroup.getContext());
            StaggeredGridLayoutManager.LayoutParams lm = new StaggeredGridLayoutManager.LayoutParams(-1, Utils.dpToPx(viewGroup.getContext(), 28));
            lm.setFullSpan(true);
            frameLayout.setLayoutParams(lm);

            TextView tv = new TextView(viewGroup.getContext());
            frameLayout.addView(tv, new FrameLayout.LayoutParams(-1, -1));
            tv.setTextColor(SmojiManager.getEmojiViewTheme().getTitleColor());
            tv.setTypeface(SmojiManager.getEmojiViewTheme().getTitleTypeface());
            tv.setTextSize(16);
            tv.setPadding(Utils.dpToPx(viewGroup.getContext(), 16), Utils.dpToPx(viewGroup.getContext(), 4),
                    Utils.dpToPx(viewGroup.getContext(), 16), Utils.dpToPx(viewGroup.getContext(), 4));

            return new TitleHolder(frameLayout, tv);
        } else if (i == 2) {
            FrameLayout frameLayout = new FrameLayout(viewGroup.getContext());
            StaggeredGridLayoutManager.LayoutParams lm = new StaggeredGridLayoutManager.LayoutParams(-1, Utils.dpToPx(viewGroup.getContext(), 38));
            lm.setFullSpan(true);
            frameLayout.setLayoutParams(lm);
            return new SpaceHolder(frameLayout);
        } else {
            FrameLayout frameLayout = new FrameLayout(viewGroup.getContext());
            EmojiImageView emojiView = new EmojiImageView(viewGroup.getContext());
            int cw = Utils.getColumnWidth(viewGroup.getContext());
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(cw, cw));
            frameLayout.addView(emojiView);

            int dp6 = Utils.dpToPx(viewGroup.getContext(), 6);
            emojiView.setPadding(dp6, dp6, dp6, dp6);

            return new EmojiHolder(frameLayout, emojiView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof TitleHolder) {
            EmojiCategory category = categories[titlesPosition.indexOf(i)];
            ((TextView) ((FrameLayout) viewHolder.itemView).getChildAt(0)).setText(category.getTitle());
        } else if (viewHolder instanceof EmojiHolder) {
            FrameLayout frameLayout = (FrameLayout) viewHolder.itemView;
            final EmojiImageView emojiView = (EmojiImageView) frameLayout.getChildAt(0);

            Emoji emoji = emojis.get(i);
            if (emoji == null) return;
            emojiView.setEmoji(variantEmoji.getVariant(emoji));
            //ImageLoadingTask currentTask = new ImageLoadingTask(emojiView);
            //currentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, emoji, null, null);

            boolean fromRecent = false;
            if (i < recentEmoji.getRecentEmojis().size()) fromRecent = true;
            emojiView.setOnEmojiActions(events, fromRecent);
        }
    }

    @Override
    public int getItemCount() {
        return emojis.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 2;
        if (titlesPosition.contains(position)) return 1;
        return 0;
    }

    void calItemsCount() {
        emojis.add(null);
        int number = 0;
        Emoji[] recents = new Emoji[recentEmoji.getRecentEmojis().size()];
        recents = recentEmoji.getRecentEmojis().toArray(recents);
        number = number + recents.length;
        emojis.addAll(Arrays.asList(recents));
        for (int i = 0; i < categories.length; i++) {
            number++;
            titlesPosition.add(number);
            emojis.add(null);
            List<Emoji> filtered = Utils.filterEmojis(Arrays.asList(categories[i].getEmojis()));
            number = number + filtered.size();
            emojis.addAll(filtered);
        }
    }

    public static class TitleHolder extends RecyclerView.ViewHolder {
        TextView tv;

        public TitleHolder(@NonNull View itemView, TextView tv) {
            super(itemView);
            this.tv = tv;
        }
    }

    public static class SpaceHolder extends RecyclerView.ViewHolder {
        public SpaceHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public static class EmojiHolder extends RecyclerView.ViewHolder {
        EmojiImageView imageView;

        public EmojiHolder(@NonNull View itemView, EmojiImageView imageView) {
            super(itemView);
            this.imageView = imageView;
        }
    }
}
