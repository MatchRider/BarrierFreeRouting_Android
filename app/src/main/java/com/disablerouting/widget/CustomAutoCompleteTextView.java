package com.disablerouting.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;

public class CustomAutoCompleteTextView extends android.support.v7.widget.AppCompatAutoCompleteTextView {

    private Context mContext;
    private boolean mAlwaysFilter = false;
    private boolean mFilterOnFocus = false;

    public CustomAutoCompleteTextView(Context context) {
        super(context);
        mContext=context;
    }

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
    }

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext=context;
    }
    
    public void setAlwaysFilter(boolean alwaysFilter) {
        mAlwaysFilter = alwaysFilter;
    }
    
    public void setFilterOnFocus(boolean filterOnFocus) {
        mFilterOnFocus = filterOnFocus;
    }
    
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && isPopupShowing()) {
            InputMethodManager inputManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            
            if(inputManager.hideSoftInputFromWindow(findFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS)){
                return true;
            }
        }
        
        return super.onKeyPreIme(keyCode, event);
    }
    
    @Override
    public boolean enoughToFilter() {
        return mAlwaysFilter || super.enoughToFilter();
    }
    
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if(focused && mFilterOnFocus && getAdapter() != null){
            if(getText().toString().length() == 0){
                setText("");
            }
        }
    }
}
