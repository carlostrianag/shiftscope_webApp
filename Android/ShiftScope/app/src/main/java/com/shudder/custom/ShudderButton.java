package com.shudder.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Carlos on 29/03/2015.
 */
public class ShudderButton extends Button{

    public ShudderButton(Context context) {
        super(context);
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "font/FuturaLTBook.ttf");
        this.setTypeface(typeFace);
    }

    public ShudderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "font/FuturaLTBook.ttf");
        this.setTypeface(typeFace);
    }

    public ShudderButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Typeface typeFace = Typeface.createFromAsset(context.getAssets(), "font/FuturaLTBook.ttf");
        this.setTypeface(typeFace);
    }

}
