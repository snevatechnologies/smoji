package com.sneva.smoji.view.adapters;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;

import com.sneva.smoji.view.SmojiManager;
import com.sneva.smoji.view.listener.FindVariantListener;
import com.sneva.smoji.view.listener.OnEmojiActions;
import com.sneva.smoji.view.shared.RecentEmoji;
import com.sneva.smoji.view.shared.VariantEmoji;
import com.sneva.smoji.view.view.EmojiRecyclerView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class EmojiViewPagerAdapter extends PagerAdapter {
    OnEmojiActions events;
    RecyclerView.OnScrollListener scrollListener;
    RecentEmoji recentEmoji;
    VariantEmoji variantEmoji;
    public List<EmojiRecyclerView> recyclerViews;
    public int add = 0;

    private final Queue<View> destroyedItems = new LinkedList<>();
    FindVariantListener findVariantListener;

    public EmojiViewPagerAdapter(OnEmojiActions events, RecyclerView.OnScrollListener scrollListener,
                                 RecentEmoji recentEmoji, VariantEmoji variantEmoji, FindVariantListener listener) {
        this.events = events;
        this.findVariantListener = listener;
        this.scrollListener = scrollListener;
        this.recentEmoji = recentEmoji;
        this.variantEmoji = variantEmoji;
        recyclerViews = new ArrayList<>();
    }

    public RecyclerView.ItemDecoration itemDecoration = null;

    @NonNull
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {
        EmojiRecyclerView recycler;
        try {
            recycler = (EmojiRecyclerView) destroyedItems.poll();
        } catch (Exception e) {
            recycler = null;
        }

        if (recycler == null)
            recycler = new EmojiRecyclerView(collection.getContext(), findVariantListener);
        collection.addView(recycler);

        if (position == 0 && add == 1) {
            recycler.setAdapter(new RecentEmojiRecyclerAdapter(recentEmoji, events, variantEmoji));
        } else {
            recycler.setAdapter(new EmojiRecyclerAdapter(SmojiManager.getInstance().getCategories()[position - add].getEmojis(),
                    events, variantEmoji));
        }

        recyclerViews.add(recycler);
        if (itemDecoration != null) {
            recycler.removeItemDecoration(itemDecoration);
            recycler.addItemDecoration(itemDecoration);
        }
        if (scrollListener != null) {
            recycler.removeOnScrollListener(scrollListener);
            recycler.addOnScrollListener(scrollListener);
        }
        return recycler;
    }

    @Override
    public int getCount() {
        if (!recentEmoji.isEmpty()) {
            add = 1;
        } else {
            add = 0;
        }
        return SmojiManager.getInstance().getCategories().length + add;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View arg0, @NonNull Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        recyclerViews.remove(object);
        destroyedItems.add((View) object);
    }
}
