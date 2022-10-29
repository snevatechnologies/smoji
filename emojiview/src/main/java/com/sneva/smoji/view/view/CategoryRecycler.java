package com.sneva.smoji.view.view;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.sneva.smoji.view.SmojiManager;
import com.sneva.smoji.view.adapters.CategoryAdapter;

import com.sneva.smoji.view.sticker.RecentSticker;
import com.sneva.smoji.view.sticker.StickerProvider;
import com.sneva.smoji.view.utils.Utils;

@SuppressLint("ViewConstructor")
class CategoryRecycler extends EmojiLayout {

    RecentSticker recentStickerManager;

    public CategoryRecycler(Context context, EmojiLayout pager, StickerProvider provider, RecentSticker recentStickerManager) {
        super(context);
        this.recentStickerManager = recentStickerManager;
        this.pager = pager;
        init(provider);
    }

    EmojiLayout pager;
    RecyclerView icons;
    View Divider;

    void init(StickerProvider provider) {
        // int iconSize = Utils.dpToPx(getContext(),24);

        icons = new RecyclerView(getContext());
        this.addView(icons, new LayoutParams(0, 0, -1, -1));

        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);
        icons.setLayoutManager(lm);
        Utils.forceLTR(icons);

        icons.setItemAnimator(null);
        icons.setAdapter(new CategoryAdapter(pager, provider, recentStickerManager));
        icons.setOverScrollMode(View.OVER_SCROLL_NEVER);
        this.setBackgroundColor(SmojiManager.getStickerViewTheme().getCategoryColor());
        this.setOnClickListener(view -> {
        });


        Divider = new View(getContext());
        this.addView(Divider, new LayoutParams(
                0, Utils.dpToPx(getContext(), 38), getContext().getResources().getDisplayMetrics().widthPixels, Utils.dpToPx(getContext(), 1)));
        if (!SmojiManager.getStickerViewTheme().isAlwaysShowDividerEnabled())
            Divider.setVisibility(GONE);
        Divider.setBackgroundColor(SmojiManager.getStickerViewTheme().getDividerColor());
    }

    public void setPageIndex(int index) {
        ((CategoryAdapter) icons.getAdapter()).update();
    }

}
