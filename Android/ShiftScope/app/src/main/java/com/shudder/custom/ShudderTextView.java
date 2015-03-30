package com.shudder.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Carlos on 29/03/2015.
 */
public class ShudderTextView extends TextView {
    public ShudderTextView(Context context) {
        super(context);
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "font/FuturaLTBook.ttf");
        this.setTypeface(typeFace);
    }

    public ShudderTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "font/FuturaLTBook.ttf");
        this.setTypeface(typeFace);
    }

    public ShudderTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "font/FuturaLTBook.ttf");
        this.setTypeface(typeFace);
    }
}
