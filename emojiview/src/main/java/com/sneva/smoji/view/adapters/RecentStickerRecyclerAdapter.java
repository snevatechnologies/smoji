package com.sneva.smoji.view.adapters;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.sneva.smoji.view.SmojiManager;
import com.sneva.smoji.view.listener.OnStickerActions;
import com.sneva.smoji.view.sticker.RecentSticker;
import com.sneva.smoji.view.sticker.Sticker;
import com.sneva.smoji.view.sticker.StickerLoader;
import com.sneva.smoji.view.utils.Utils;

public class RecentStickerRecyclerAdapter extends RecyclerView.Adapter<RecentStickerRecyclerAdapter.ViewHolder> {
    RecentSticker recent;
    OnStickerActions events;
    StickerLoader loader;

    public RecentStickerRecyclerAdapter(RecentSticker recent, OnStickerActions events, StickerLoader loader){
        this.recent = recent;
        this.events = events;
        this.loader = loader;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        FrameLayout frameLayout = new FrameLayout(viewGroup.getContext());
        View emojiView = SmojiManager.getInstance().getStickerViewCreatorListener().onCreateStickerView(viewGroup.getContext(),null,true);
        int cw = Utils.getStickerColumnWidth(viewGroup.getContext());
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(cw,cw));
        frameLayout.addView(emojiView);

        int dp6=Utils.dpToPx(viewGroup.getContext(),6);
        emojiView.setPadding(dp6,dp6,dp6,dp6);

        View ripple = new View(viewGroup.getContext());
        frameLayout.addView(ripple,new ViewGroup.MarginLayoutParams(cw,cw));

        return new ViewHolder(frameLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        FrameLayout frameLayout = (FrameLayout) viewHolder.itemView;
        final AppCompatImageView stickerView = (AppCompatImageView) frameLayout.getChildAt(0);
        View ripple = frameLayout.getChildAt(1);

        @SuppressWarnings("rawtypes")
		final Sticker sticker = (Sticker) recent.getRecentStickers().toArray()[i];
        loader.onLoadSticker(stickerView,sticker);

        Utils.setClickEffect(ripple,false);

        ripple.setOnClickListener(view -> {
            if (events !=null) events.onClick(stickerView,sticker,true);
        });
            ripple.setOnLongClickListener(view -> {
                if (events!=null) return events.onLongClick(stickerView,sticker,true);
                return false;
            });
    }

    @Override
    public int getItemCount() {
        return recent.getRecentStickers().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
