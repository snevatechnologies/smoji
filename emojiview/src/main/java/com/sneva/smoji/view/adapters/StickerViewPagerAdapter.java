package com.sneva.smoji.view.adapters;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;

import com.sneva.smoji.view.listener.OnStickerActions;
import com.sneva.smoji.view.sticker.RecentSticker;
import com.sneva.smoji.view.sticker.StickerProvider;
import com.sneva.smoji.view.view.StickerRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StickerViewPagerAdapter extends PagerAdapter {
    OnStickerActions events;
    RecyclerView.OnScrollListener scrollListener;
    public List<View> recyclerViews;
    public int add = 0;
    StickerProvider provider;
    RecentSticker recent;

    public StickerViewPagerAdapter(OnStickerActions events, RecyclerView.OnScrollListener scrollListener, StickerProvider provider, RecentSticker recentStickerManager) {
        this.events = events;
        this.scrollListener = scrollListener;
        recyclerViews = new ArrayList<>();
        this.provider = provider;
        this.recent = recentStickerManager;
    }

    public RecyclerView.ItemDecoration itemDecoration = null;

    @NonNull
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {
        if ((position == 0 && add == 1) || !provider.getCategories()[position - add].useCustomView()) {
            StickerRecyclerView recycler = new StickerRecyclerView(collection.getContext());
            collection.addView(recycler);
            if (position == 0 && add == 1) {
                recycler.setAdapter(new RecentStickerRecyclerAdapter(recent, events, provider.getLoader()));
            } else {
                recycler.setAdapter(new StickerRecyclerAdapter(provider.getCategories()[position - add]
                        , provider.getCategories()[position - add].getStickers(), events, provider.getLoader()
                        , provider.getCategories()[position - add].getEmptyView(collection)));
            }
            recyclerViews.add(recycler);
            if (itemDecoration != null) recycler.addItemDecoration(itemDecoration);
            if (scrollListener != null) recycler.addOnScrollListener(scrollListener);
            if (position != 0 || add == 0)
                provider.getCategories()[position - add].bindView(recycler);
            return recycler;
        } else {
            View view = provider.getCategories()[position - add].getView(collection);
            collection.addView(view);
            provider.getCategories()[position - add].bindView(view);
            recyclerViews.add(view);
            if (view instanceof RecyclerView) {
                if (itemDecoration != null) ((RecyclerView) view).addItemDecoration(itemDecoration);
                if (scrollListener != null)
                    ((RecyclerView) view).addOnScrollListener(scrollListener);
            }
            return view;
        }
    }

    @Override
    public int getCount() {
        if (!recent.isEmpty() && provider.isRecentEnabled()) {
            add = 1;
        } else {
            add = 0;
        }
        return provider.getCategories().length + add;
    }

    @Override
    public boolean isViewFromObject(@NonNull View arg0, @NonNull Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
        recyclerViews.remove(object);
    }
}
