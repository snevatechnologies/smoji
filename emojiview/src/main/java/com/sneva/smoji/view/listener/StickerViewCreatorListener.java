package com.sneva.smoji.view.listener;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sneva.smoji.view.sticker.StickerCategory;

public interface StickerViewCreatorListener {
    View onCreateStickerView(@NonNull final Context context, @Nullable final StickerCategory<?> category, final boolean isRecent);

    View onCreateCategoryView(@NonNull final Context context);
}