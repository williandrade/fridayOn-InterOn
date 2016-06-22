package me.williandrade.util;

import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class Helper {

    public void setEditTextFocus(EditText searchEditText, boolean isFocused, Context context) {
        searchEditText.setCursorVisible(isFocused);
        searchEditText.setFocusable(isFocused);
        searchEditText.setFocusableInTouchMode(isFocused);
        if (isFocused) {
            searchEditText.requestFocus();
        } else {
            InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(searchEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
