package com.sneva.smoji.view.variant;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.sneva.smoji.view.SmojiManager;
import com.sneva.smoji.view.R;
import com.sneva.smoji.view.emoji.Emoji;
import com.sneva.smoji.view.listener.OnEmojiActions;
import com.sneva.smoji.view.utils.Utils;
import com.sneva.smoji.view.view.EmojiImageView;


import java.util.ArrayList;
import java.util.List;

import static android.view.View.MeasureSpec.makeMeasureSpec;

public class SimpleEmojiVariantPopup extends EmojiVariantPopup {
    private static final int MARGIN = 1;

    @NonNull
    final View rootView;
    @Nullable
    private PopupWindow popupWindow;

    @Nullable
    final OnEmojiActions listener;
    @Nullable
    EmojiImageView rootImageView;

    public SimpleEmojiVariantPopup(@NonNull final View rootView, @Nullable final OnEmojiActions listener) {
        super(rootView, listener);
        this.rootView = rootView;
        this.listener = listener;
    }

    View content;
    boolean isShowing;

    public boolean isShowing() {
        return isShowing;
    }

    public void show(@NonNull final EmojiImageView clickedImage, @NonNull final Emoji emoji, final boolean fromRecent) {
        dismiss();
        isShowing = true;
        rootImageView = clickedImage;

        content = initView(clickedImage.getContext(), emoji, clickedImage.getWidth(), fromRecent);

        popupWindow = new PopupWindow(content, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setOnDismissListener(() -> isShowing = false);
        popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        popupWindow.setBackgroundDrawable(new BitmapDrawable(clickedImage.getContext().getResources(), (Bitmap) null));

        content.measure(makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        final Point location = Utils.locationOnScreen(clickedImage);
        final Point desiredLocation = new Point(
                location.x - content.getMeasuredWidth() / 2 + clickedImage.getWidth() / 2,
                location.y - content.getMeasuredHeight()
        );

        popupWindow.showAtLocation(rootView, Gravity.NO_GRAVITY, desiredLocation.x, desiredLocation.y);
        rootImageView.getParent().requestDisallowInterceptTouchEvent(true);
        Utils.fixPopupLocation(popupWindow, desiredLocation);
    }

    public void dismiss() {
        isShowing = false;
        rootImageView = null;

        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    FrameLayout imageContainer;

    private View initView(@NonNull final Context context, @NonNull final Emoji emoji, final int width, final boolean fromRecent) {
        final View result = View.inflate(context, R.layout.emoji_skin_popup, null);
        imageContainer = result.findViewById(R.id.container);
        CardView cardView = result.findViewById(R.id.cardview);
        cardView.setCardBackgroundColor(SmojiManager.getEmojiViewTheme().getVariantPopupBackgroundColor());

        final List<Emoji> variants = new ArrayList<>(emoji.getBase().getVariants());
        variants.add(0, emoji.getBase());

        final LayoutInflater inflater = LayoutInflater.from(context);

        int index = 0;
        boolean isCustom = variants.size() > 6;
        final int margin = Utils.dpToPx(context, MARGIN);
        for (final Emoji variant : variants) {
            int i = isCustom ? (index == 0 ? 0 : (index - 1) % 5) : index;

            final ImageView emojiImage = (ImageView) inflater.inflate(R.layout.emoji_item, imageContainer, false);
            final ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) emojiImage.getLayoutParams();

            // Use the same size for Emojis as in the picker.
            layoutParams.width = width;
            int row = (!isCustom || index == 0) ? 0 : ((index - 1) / 5 + 1);
            if (isCustom && index == 0)
                i = 2;

            layoutParams.setMargins((i + 1) * margin + i * width, row * width + (row + 1) * margin, margin, margin);
            emojiImage.setImageDrawable(variant.getDrawable(emojiImage));

            emojiImage.setOnClickListener(view -> {
                if (listener != null && rootImageView != null) {
                    rootImageView.updateEmoji(variant);
                    listener.onClick(rootImageView, variant, fromRecent, true);
                }
            });

            index++;
            imageContainer.addView(emojiImage);
        }

        return result;
    }
}
