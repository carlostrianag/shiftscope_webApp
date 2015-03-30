package com.shudder.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by Carlos on 30/03/2015.
 */
public class ShudderEditText extends EditText {
    public ShudderEditText(Context context) {
        super(context);
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "font/FuturaLTBook.ttf");
        this.setTypeface(typeFace);
    }

    public ShudderEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "font/FuturaLTBook.ttf");
        this.setTypeface(typeFace);
    }

    public ShudderEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "font/FuturaLTBook.ttf");
        this.setTypeface(typeFace);
    }
}
