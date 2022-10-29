package com.sneva.smoji.view.listener;

import android.view.View;

import com.sneva.smoji.view.sticker.Sticker;

public interface OnStickerActions {
    void onClick(View view, Sticker<?> sticker, boolean fromRecent);

    boolean onLongClick(View view, Sticker<?> sticker, boolean fromRecent);
}
