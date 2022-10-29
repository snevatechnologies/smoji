package com.sneva.smoji.view.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.sneva.smoji.view.listener.FindVariantListener;
import com.sneva.smoji.view.utils.Utils;

@SuppressLint("ViewConstructor")
public class EmojiSingleRecyclerView extends RecyclerView {
    FindVariantListener variantListener;

    public EmojiSingleRecyclerView(@NonNull Context context, FindVariantListener variantListener) {
        super(context);
        this.variantListener = variantListener;
        StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(Utils.getGridCount(context), StaggeredGridLayoutManager.VERTICAL);
        this.setLayoutManager(lm);
        Utils.forceLTR(this);
        setOverScrollMode(OVER_SCROLL_NEVER);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (variantListener.findVariant() != null && variantListener.findVariant().onTouch(event, this))
            return true;
        return super.dispatchTouchEvent(event);
    }
}
