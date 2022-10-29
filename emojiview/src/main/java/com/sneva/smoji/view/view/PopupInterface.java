package com.sneva.smoji.view.view;

public interface PopupInterface {
    void toggle();

    void show();

    void dismiss();

    boolean isShowing();

    boolean onBackPressed();

    void reload();
}
