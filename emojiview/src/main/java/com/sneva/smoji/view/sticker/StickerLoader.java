package com.sneva.smoji.view.sticker;

import android.view.View;

public interface StickerLoader {
    void onLoadSticker(View view, Sticker<?> sticker);

    void onLoadStickerCategory(View icon, StickerCategory<?> stickerCategory, boolean selected);
}
