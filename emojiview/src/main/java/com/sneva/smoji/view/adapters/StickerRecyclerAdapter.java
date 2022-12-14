package com.sneva.smoji.view.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sneva.smoji.view.SmojiManager;
import com.sneva.smoji.view.listener.OnStickerActions;
import com.sneva.smoji.view.sticker.Sticker;
import com.sneva.smoji.view.sticker.StickerCategory;
import com.sneva.smoji.view.sticker.StickerLoader;
import com.sneva.smoji.view.utils.Utils;

@SuppressWarnings("rawtypes")
public class StickerRecyclerAdapter extends RecyclerView.Adapter<StickerRecyclerAdapter.ViewHolder> {
    Sticker[] stickers;
    int count;
    OnStickerActions events;
    StickerLoader loader;
    boolean isEmptyLoading;
    View empty;
    StickerCategory category;

    public StickerRecyclerAdapter(StickerCategory category, Sticker[] stickers, OnStickerActions events, StickerLoader loader, View empty) {
        this.stickers = stickers;
        this.category = category;
        this.count = stickers.length;
        this.events = events;
        this.loader = loader;
        this.empty = empty;
        isEmptyLoading = empty != null;
    }

    RecyclerView rv = null;

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView r) {
        super.onAttachedToRecyclerView(r);
        rv = r;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == 1) {
            return new ViewHolder(empty);
        } else {
            FrameLayout frameLayout = new FrameLayout(viewGroup.getContext());
            View emojiView = SmojiManager.getInstance().getStickerViewCreatorListener().onCreateStickerView(viewGroup.getContext(), category, false);
            int cw = Utils.getStickerColumnWidth(viewGroup.getContext());
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(cw, cw));
            frameLayout.addView(emojiView);

            int dp6 = Utils.dpToPx(viewGroup.getContext(), 6);
            emojiView.setPadding(dp6, dp6, dp6, dp6);

            View ripple = new View(viewGroup.getContext());
            frameLayout.addView(ripple, new ViewGroup.MarginLayoutParams(cw, cw));

            return new ViewHolder(frameLayout);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (viewHolder.getItemViewType() == 1) {
            if (rv != null && rv.getLayoutParams() != null) {
                if (viewHolder.itemView.getLayoutParams() == null) {
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(rv.getMeasuredWidth(), rv.getLayoutParams().height);
                    lp.gravity = Gravity.CENTER;
                    viewHolder.itemView.setLayoutParams(lp);
                } else {
                    viewHolder.itemView.getLayoutParams().width = rv.getMeasuredWidth();
                    viewHolder.itemView.getLayoutParams().height = rv.getLayoutParams().height;
                }
            }
        } else {
            FrameLayout frameLayout = (FrameLayout) viewHolder.itemView;
            final View stickerView = frameLayout.getChildAt(0);
            View ripple = frameLayout.getChildAt(1);

            final Sticker sticker = stickers[i];
            loader.onLoadSticker(stickerView, sticker);

            Utils.setClickEffect(ripple, false);

            ripple.setOnClickListener(view -> {
                if (events != null) events.onClick(stickerView, sticker, false);
            });
            ripple.setOnLongClickListener(view -> {
                if (events != null) return events.onLongClick(stickerView, sticker, false);
                return false;
            });
        }
    }

    @Override
    public int getItemCount() {
        if (count == 0 && isEmptyLoading) return 1;
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && stickers.length == 0 && isEmptyLoading) return 1;
        return super.getItemViewType(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
