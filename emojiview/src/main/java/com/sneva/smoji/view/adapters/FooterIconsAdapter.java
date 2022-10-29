package com.sneva.smoji.view.adapters;

import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ViewGroup;

import com.sneva.smoji.view.SmojiManager;
import com.sneva.smoji.view.utils.Utils;
import com.sneva.smoji.view.view.EmojiLayout;
import com.sneva.smoji.view.view.EmojiPager;

public class FooterIconsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    EmojiPager pager;

    public FooterIconsAdapter(EmojiPager pager) {
        this.pager = pager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        int iconSize = Utils.dpToPx(viewGroup.getContext(), 24);
        EmojiLayout layout = new EmojiLayout(viewGroup.getContext());
        AppCompatImageView icon = new AppCompatImageView(viewGroup.getContext());
        layout.addView(icon, new EmojiLayout.LayoutParams(Utils.dpToPx(viewGroup.getContext(), 8), Utils.dpToPx(viewGroup.getContext(), 10), iconSize, iconSize));
        layout.setLayoutParams(new ViewGroup.LayoutParams(Utils.dpToPx(viewGroup.getContext(), 40), Utils.dpToPx(viewGroup.getContext(), 44)));
        return new RecyclerView.ViewHolder(layout) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        boolean selected = pager.getViewPager().getCurrentItem() == i;
        EmojiLayout layout = (EmojiLayout) viewHolder.itemView;
        AppCompatImageView icon = (AppCompatImageView) layout.getChildAt(0);

        if (pager.getPageBinder(i) != null) {
            pager.getPageBinder(i).onBindFooterItem(icon, i, selected);
        } else {
            Drawable dr = ContextCompat.getDrawable(icon.getContext().getApplicationContext(), pager.getPageIcon(i));
            if (selected) {
                DrawableCompat.setTint(DrawableCompat.wrap(dr), SmojiManager.getEmojiViewTheme().getFooterSelectedItemColor());
            } else {
                DrawableCompat.setTint(DrawableCompat.wrap(dr), SmojiManager.getEmojiViewTheme().getFooterItemColor());
            }
            icon.setImageDrawable(dr);
        }
        Utils.setClickEffect(icon, true);

        icon.setOnClickListener(view -> {
            if (pager.getViewPager().getCurrentItem() != i) {
                pager.setPageIndex(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pager.getPagesCount();
    }
}
