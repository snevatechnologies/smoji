package com.sneva.smoji.view.adapters;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.ViewGroup;

import com.sneva.smoji.view.SmojiManager;
import com.sneva.smoji.view.R;
import com.sneva.smoji.view.sticker.RecentSticker;
import com.sneva.smoji.view.sticker.StickerProvider;
import com.sneva.smoji.view.utils.Utils;
import com.sneva.smoji.view.view.EmojiLayout;

public class CategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    EmojiLayout pager;
    StickerProvider provider;
    RecentSticker recentSticker;
    boolean recent;

    public CategoryAdapter(EmojiLayout pager, StickerProvider provider, RecentSticker RecentStickerManager) {
        recent = !RecentStickerManager.isEmpty() && provider.isRecentEnabled();
        this.recentSticker = RecentStickerManager;
        this.pager = pager;
        this.provider = provider;
    }

    public void update() {
        recent = !recentSticker.isEmpty() && provider.isRecentEnabled();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        int iconSize = Utils.dpToPx(viewGroup.getContext(), 24);
        EmojiLayout layout = new EmojiLayout(viewGroup.getContext());
        View icon;
        if (i == 10) {
            icon = new AppCompatImageView(viewGroup.getContext());
        } else {
            icon = SmojiManager.getInstance().getStickerViewCreatorListener().onCreateCategoryView(viewGroup.getContext());
        }
        layout.addView(icon, new EmojiLayout.LayoutParams(Utils.dpToPx(viewGroup.getContext(), 7), Utils.dpToPx(viewGroup.getContext(), 7), iconSize, iconSize));
        layout.setLayoutParams(new ViewGroup.LayoutParams(Utils.dpToPx(viewGroup.getContext(), 38), Utils.dpToPx(viewGroup.getContext(), 38)));

        View selection = new View(viewGroup.getContext());
        layout.addView(selection, new EmojiLayout.LayoutParams(
                0, Utils.dpToPx(viewGroup.getContext(), 36), Utils.dpToPx(viewGroup.getContext(), 38), Utils.dpToPx(viewGroup.getContext(), 2)));
        selection.setBackgroundColor(SmojiManager.getStickerViewTheme().getSelectionColor());
        selection.setVisibility(View.GONE);
        return new RecyclerView.ViewHolder(layout) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        boolean selected = pager.getPageIndex() == i;
        EmojiLayout layout = (EmojiLayout) viewHolder.itemView;
        View icon = layout.getChildAt(0);
        View selection = layout.getChildAt(1);

        if (selected) selection.setVisibility(View.VISIBLE);
        else selection.setVisibility(View.GONE);

        if (recent && i == 0) {
            Drawable dr0 = AppCompatResources.getDrawable(layout.getContext(), R.drawable.emoji_recent);
            Drawable dr = dr0.getConstantState().newDrawable();
            if (selected) {
                DrawableCompat.setTint(DrawableCompat.wrap(dr), SmojiManager.getStickerViewTheme().getSelectedColor());
            } else {
                DrawableCompat.setTint(DrawableCompat.wrap(dr), SmojiManager.getStickerViewTheme().getDefaultColor());
            }
            ((AppCompatImageView) icon).setImageDrawable(dr);
        } else {
            int i2 = i;
            if (recent) i2--;
            provider.getLoader().onLoadStickerCategory(icon, provider.getCategories()[i2], selected);
        }

        Utils.setClickEffect(icon, true);

        View.OnClickListener listener = view -> pager.setPageIndex(i);
        icon.setOnClickListener(listener);
        layout.setOnClickListener(listener);
    }

    @Override
    public int getItemCount() {
        if (recent) return provider.getCategories().length + 1;
        return provider.getCategories().length;
    }

    @Override
    public int getItemViewType(int position) {
        if (recent && position == 0) return 10;
        return -1;
    }
}
