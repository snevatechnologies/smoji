package com.sneva.smoji.view.view;

import android.content.Context;

import androidx.viewpager.widget.ViewPager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.sneva.smoji.view.SmojiManager;
import com.sneva.smoji.view.adapters.StickerViewPagerAdapter;
import com.sneva.smoji.view.listener.OnStickerActions;
import com.sneva.smoji.view.sticker.RecentSticker;
import com.sneva.smoji.view.sticker.RecentStickerManager;
import com.sneva.smoji.view.sticker.Sticker;
import com.sneva.smoji.view.sticker.StickerProvider;
import com.sneva.smoji.view.utils.Utils;

public class StickerView extends EmojiLayout {
    StickerProvider stickerProvider;
    RecentSticker recent;

    final String type;

    public StickerView(Context context, String type, StickerProvider stickerProvider) {
        super(context);
        this.type = type;
        this.stickerProvider = stickerProvider;

        init();
    }

    public final String getType() {
        return type;
    }


    CategoryRecycler categoryViews;
    ViewPager vp;


    @SuppressWarnings("rawtypes")
    OnStickerActions events = new OnStickerActions() {

        @Override
        public void onClick(View view, Sticker sticker, boolean fromRecent) {
            if (recent != null) recent.addSticker(sticker);
            if (stickerActions != null) stickerActions.onClick(view, sticker, fromRecent);
        }

        @Override
        public boolean onLongClick(View view, Sticker sticker, boolean fromRecent) {
            if (stickerActions != null) return stickerActions.onLongClick(view, sticker, fromRecent);
            return false;
        }
    };

    OnStickerActions stickerActions = null;

    /**
     * add sticker click and longClick listener
     */
    public void setOnStickerActionsListener(OnStickerActions listener) {
        stickerActions = listener;
    }


    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {

        private boolean isShowing = true;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (recyclerView == null) {
                if (!SmojiManager.getStickerViewTheme().isAlwaysShowDividerEnabled()) {
                    if (!isShowing) {
                        isShowing = true;
                        if (categoryViews != null) categoryViews.Divider.setVisibility(GONE);
                    }
                }
                return;
            }
            if (dy == 0) return;
            if (dy == 1) dy = 0;
            super.onScrolled(recyclerView, dx, dy);
            if (scrollListener2 != null) scrollListener2.onScrolled(recyclerView, dx, dy);

            if (!SmojiManager.getStickerViewTheme().isAlwaysShowDividerEnabled()) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager == null) return;
                int firstVisibleItemPosition = 1;
                if (layoutManager instanceof GridLayoutManager) {
                    firstVisibleItemPosition = ((GridLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
                } else if (layoutManager instanceof LinearLayoutManager) {
                    firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
                }

                int visibleItemCount = layoutManager.getChildCount();
                if ((visibleItemCount > 0 && (firstVisibleItemPosition) == 0)) {
                    if (!isShowing) {
                        isShowing = true;
                        if (categoryViews != null) categoryViews.Divider.setVisibility(GONE);
                    }
                } else {
                    if (isShowing) {
                        isShowing = false;
                        if (categoryViews != null) categoryViews.Divider.setVisibility(VISIBLE);
                    }
                }
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (scrollListener2 != null)
                scrollListener2.onScrollStateChanged(recyclerView, newState);
        }
    };

    RecyclerView.OnScrollListener scrollListener2 = null;

    private void init() {
        if (SmojiManager.getRecentSticker() != null) {
            recent = SmojiManager.getRecentSticker();
        } else {
            recent = new RecentStickerManager(getContext(), type);
        }

        int top = 0;
        if (SmojiManager.getStickerViewTheme().isCategoryEnabled())
            top = Utils.dpToPx(getContext(), 39);

        vp = new ViewPager(getContext());
        this.addView(vp, new LayoutParams(0, top, -1, -1));
        vp.setAdapter(new StickerViewPagerAdapter(events, scrollListener, stickerProvider, recent));
        //vp.setPadding(0, 0, 0, top);

        if (SmojiManager.getStickerViewTheme().isCategoryEnabled()) {
            categoryViews = new CategoryRecycler(getContext(), this, stickerProvider, recent);
            this.addView(categoryViews, new LayoutParams(0, 0, -1, top));
        } else {
            categoryViews = null;
        }

        this.setBackgroundColor(SmojiManager.getStickerViewTheme().getBackgroundColor());
        if (categoryViews != null)
            categoryViews.setBackgroundColor(SmojiManager.getStickerViewTheme().getBackgroundColor());

        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                if (pagerListener2 != null) pagerListener2.onPageScrolled(i, v, i1);
            }

            @Override
            public void onPageSelected(int i) {
                vp.setCurrentItem(i, true);
                if (!SmojiManager.getStickerViewTheme().isAlwaysShowDividerEnabled()) {
                    if (((StickerViewPagerAdapter) vp.getAdapter()).recyclerViews.size() > i) {
                        View view = ((StickerViewPagerAdapter) vp.getAdapter()).recyclerViews.get(i);
                        if (view instanceof RecyclerView)
                            scrollListener.onScrolled((RecyclerView) view, 0, 1);
                    } else {
                        scrollListener.onScrolled(null, 0, 1);
                    }
                }
                if (categoryViews != null) categoryViews.setPageIndex(i);
                if (pagerListener2 != null) pagerListener2.onPageSelected(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
                if (pagerListener2 != null) pagerListener2.onPageScrollStateChanged(i);
            }
        });

    }

    ViewPager.OnPageChangeListener pagerListener2 = null;

    @Override
    public void setPageIndex(int index) {
        vp.setCurrentItem(index, true);
        if (!SmojiManager.getStickerViewTheme().isAlwaysShowDividerEnabled()) {
            if (((StickerViewPagerAdapter) vp.getAdapter()).recyclerViews.size() > index) {
                View view = ((StickerViewPagerAdapter) vp.getAdapter()).recyclerViews.get(index);
                if (view instanceof RecyclerView)
                    scrollListener.onScrolled((RecyclerView) view, 0, 1);
            } else {
                scrollListener.onScrolled(null, 0, 1);
            }
        }
        if (categoryViews != null) categoryViews.setPageIndex(index);
    }

    @Override
    public void dismiss() {
        recent.persist();
    }

    @Override
    protected void setScrollListener(RecyclerView.OnScrollListener listener) {
        super.setScrollListener(listener);
        scrollListener2 = listener;
    }

    @Override
    protected void setPageChanged(ViewPager.OnPageChangeListener listener) {
        super.setPageChanged(listener);
        pagerListener2 = listener;
    }

    @Override
    public int getPageIndex() {
        return vp.getCurrentItem();
    }

    @Override
    protected void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        ((StickerViewPagerAdapter) vp.getAdapter()).itemDecoration = itemDecoration;
        for (int i = 0; i < ((StickerViewPagerAdapter) vp.getAdapter()).recyclerViews.size(); i++) {
            View view = ((StickerViewPagerAdapter) vp.getAdapter()).recyclerViews.get(i);
            if (view instanceof RecyclerView)
                ((RecyclerView) view).addItemDecoration(itemDecoration);
        }
    }

    @Override
    protected void refresh() {
        super.refresh();
        try {
            if (categoryViews != null) {
                categoryViews.removeAllViews();
                categoryViews.init(stickerProvider);
            }
            vp.getAdapter().notifyDataSetChanged();
            vp.setCurrentItem(0, false);
            for (int i = 0; i < ((StickerViewPagerAdapter) vp.getAdapter()).recyclerViews.size(); i++) {
                View view = ((StickerViewPagerAdapter) vp.getAdapter()).recyclerViews.get(i);
                if (view instanceof RecyclerView)
                    ((RecyclerView) view).getAdapter().notifyDataSetChanged();
            }
            if (!SmojiManager.getStickerViewTheme().isAlwaysShowDividerEnabled())
                scrollListener.onScrolled(null, 0, 1);
            if (categoryViews != null) categoryViews.setPageIndex(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshNow(int scrollToPosition) {
        super.refresh();
        try {
            for (int i = 0; i < ((StickerViewPagerAdapter) vp.getAdapter()).recyclerViews.size(); i++) {
                View view = ((StickerViewPagerAdapter) vp.getAdapter()).recyclerViews.get(i);
                if (view instanceof RecyclerView)
                    ((RecyclerView) view).getAdapter().notifyDataSetChanged();
            }
            vp.getAdapter().notifyDataSetChanged();
            int stp = scrollToPosition + ((StickerViewPagerAdapter) vp.getAdapter()).add;
            if (categoryViews != null) {
                categoryViews.removeAllViews();
                categoryViews.init(stickerProvider);
            }
            vp.setCurrentItem(stp, false);
            if (!SmojiManager.getStickerViewTheme().isAlwaysShowDividerEnabled())
                scrollListener.onScrolled(null, 0, 1);
            if (categoryViews != null) categoryViews.setPageIndex(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ViewPager getViewPager() {
        return vp;
    }

    View l = null;

    public View getLoadingView() {
        return l;
    }

    public void setLoadingView(View view) {
        if (l != null) this.removeView(l);
        l = view;
        if (l == null) return;
        if (l.getLayoutParams() == null) {
            l.setLayoutParams(new LayoutParams(0, 0, -1, -1));
        }
        this.addView(l, l.getLayoutParams());
        l.setVisibility(GONE);
    }

    public void setLoadingMode(boolean enabled) {
        if (l != null) {
            if (enabled) {
                l.setVisibility(VISIBLE);
                if (categoryViews != null) categoryViews.setVisibility(GONE);
                vp.setVisibility(GONE);
            } else {
                l.setVisibility(GONE);
                if (categoryViews != null) categoryViews.setVisibility(VISIBLE);
                vp.setVisibility(VISIBLE);
            }
        }
    }

    public void refreshNow() {
        refresh();
    }
}
