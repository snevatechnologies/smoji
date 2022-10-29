package com.sneva.smoji.view.listener;

public interface PopupListener {
    void onDismiss();

    void onShow();

    void onKeyboardOpened(int height);

    void onKeyboardClosed();

    void onViewHeightChanged (int height);
}
