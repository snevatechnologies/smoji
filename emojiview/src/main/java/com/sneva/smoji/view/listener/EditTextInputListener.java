package com.sneva.smoji.view.listener;

import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sneva.smoji.view.emoji.Emoji;

public interface EditTextInputListener {
    void input(@NonNull final EditText editText, @Nullable final Emoji emoji);
}
