package com.sneva.smoji.view.view;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Rect;
import android.view.View;

import com.sneva.smoji.view.utils.Utils;

/**
 * @hide
 */
public class StickerRecyclerView extends RecyclerView {

    public StickerRecyclerView(@NonNull Context context) {
        super(context);
        final int spanCount = Utils.getStickerGridCount(context);
        GridLayoutManager lm = new GridLayoutManager(context, spanCount);
        this.setLayoutManager(lm);

        final int spacing;
        spacing = (context.getResources().getDisplayMetrics().widthPixels - (Utils.getStickerColumnWidth(context) * spanCount)) / (spanCount + 2);
        if (spacing > 0) {
            this.addItemDecoration(new ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull State state) {
                    outRect.left = spacing;
                }
            });
        }

        setOverScrollMode(OVER_SCROLL_NEVER);
    }

}
