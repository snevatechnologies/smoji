package com.sneva.smoji.view.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.AppCompatImageView;

import android.view.View;

import com.sneva.smoji.view.SmojiManager;
import com.sneva.smoji.view.SmojiUtils;
import com.sneva.smoji.view.R;
import com.sneva.smoji.view.emoji.Emoji;
import com.sneva.smoji.view.emoji.EmojiCategory;
import com.sneva.smoji.view.shared.RecentEmoji;
import com.sneva.smoji.view.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressLint("ViewConstructor")
class CategoryViews extends EmojiLayout {
    public CategoryViews(Context context, EmojiBase view, RecentEmoji recentEmoji) {
        super(context);
        this.emojiView = view;
        this.recentEmoji = recentEmoji;
        init();
    }

    RecentEmoji recentEmoji;
    EmojiBase emojiView;
    View selection;
    View Divider;
    boolean recent;
    List<AppCompatImageView> icons;
    int index = 0;

    void init() {
        this.setOnClickListener(view -> {
        });

        icons = new ArrayList<>();

        int left = 0;
        List<EmojiCategory> categories = new ArrayList<>(Arrays.asList(SmojiManager.getInstance().getCategories()));
        recent = recentEmoji.isEmpty();
        if (!recent) {
            categories.add(0, new EmojiCategory() {

                @NonNull
                @Override
                public Emoji[] getEmojis() {
                    return new Emoji[0];
                }

                @Override
                public int getIcon() {
                    return R.drawable.emoji_recent;
                }

                @Override
                public CharSequence getTitle() {
                    return "";
                }
            });
        }

        boolean backspace = false;
        if (!SmojiManager.getEmojiViewTheme().isFooterEnabled() && SmojiManager.isBackspaceCategoryEnabled()) {
            backspace = true;
            categories.add(new EmojiCategory() {
                @NonNull
                @Override
                public Emoji[] getEmojis() {
                    return new Emoji[0];
                }

                @Override
                public int getIcon() {
                    return R.drawable.emoji_backspace;
                }

                @Override
                public CharSequence getTitle() {
                    return "";
                }
            });
        }

        int size = getContext().getResources().getDisplayMetrics().widthPixels / categories.size();
        int iconSize = Utils.dpToPx(getContext(), 22);

        for (int i = 0; i < categories.size(); i++) {
            EmojiLayout layout = new EmojiLayout(getContext());
            this.addView(layout, new LayoutParams(left, 0, size, -1));
            AppCompatImageView icon = new AppCompatImageView(getContext());
            layout.addView(icon, new LayoutParams
                    ((size / 2) - (iconSize / 2), Utils.dpToPx(getContext(), 9), iconSize, iconSize));

            Drawable dr = AppCompatResources.getDrawable(getContext(), categories.get(i).getIcon());
            icon.setTag(dr);

            setIconImage(icon, i == 0);
            if (backspace && i == categories.size() - 1) {
                icon.setOnClickListener(view -> {
                    if (emojiView.getEditText() != null)
                        SmojiUtils.backspace(emojiView.getEditText());
                });
            } else {
                addClick(icon, i);
                addClick(layout, i);
            }
            Utils.setClickEffect(icon, true);
            left = left + size;
            icons.add(icon);
        }

        selection = new View(getContext());
        this.addView(selection, new LayoutParams(
                0, Utils.dpToPx(getContext(), 36), size, Utils.dpToPx(getContext(), 2)));
        selection.setBackgroundColor(SmojiManager.getEmojiViewTheme().getSelectionColor());

        Divider = new View(getContext());
        this.addView(Divider, new LayoutParams(
                0, Utils.dpToPx(getContext(), 38), getContext().getResources().getDisplayMetrics().widthPixels, Utils.dpToPx(getContext(), 1)));
        if (!SmojiManager.getEmojiViewTheme().isAlwaysShowDividerEnabled())
            Divider.setVisibility(GONE);
        Divider.setBackgroundColor(SmojiManager.getEmojiViewTheme().getDividerColor());

        this.setBackgroundColor(SmojiManager.getEmojiViewTheme().getCategoryColor());
    }


    public void setPageIndex(int index) {
        if (this.index == index) return;
        this.index = index;
        for (int i = 0; i < icons.size(); i++) {
            AppCompatImageView icon = icons.get(i);
            if (i == index) {
                setIconImage(icon, true);
                setSelectionPage((EmojiLayout) icon.getParent());
            } else {
                setIconImage(icon, false);
            }
        }
    }

    private void setIconImage(AppCompatImageView icon, boolean selected) {
        Drawable dr = ((Drawable) icon.getTag()).getConstantState().newDrawable();
        if (selected) {
            DrawableCompat.setTint(DrawableCompat.wrap(dr), SmojiManager.getEmojiViewTheme().getSelectedColor());
        } else {
            DrawableCompat.setTint(DrawableCompat.wrap(dr), SmojiManager.getEmojiViewTheme().getDefaultColor());
        }
        icon.setImageDrawable(dr);
    }

    private void setSelectionPage(EmojiLayout icon) {
        ((LayoutParams) selection.getLayoutParams()).leftMargin = ((LayoutParams) icon.getLayoutParams()).leftMargin;
        this.requestLayout();
    }

    private void addClick(final View icon, final int i) {
        icon.setOnClickListener(view -> {
            if (index == i) return;
            emojiView.setPageIndex(i);
        });
    }
}
