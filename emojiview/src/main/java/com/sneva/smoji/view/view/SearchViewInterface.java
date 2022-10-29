package com.sneva.smoji.view.view;

import android.view.View;
import android.widget.EditText;

public interface SearchViewInterface {
    int getSearchViewHeight();
    void show();
    void hide();
    boolean isShowing();
    View getView();
    EditText getSearchTextField();
}
