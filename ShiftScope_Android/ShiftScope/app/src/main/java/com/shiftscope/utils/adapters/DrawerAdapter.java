package com.shiftscope.utils.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shiftscope.utils.MenuEntry;

import java.util.ArrayList;

import shiftscope.com.shiftscope.R;

/**
 * Created by Carlos on 1/3/2015.
 */
public class DrawerAdapter extends ArrayAdapter<MenuEntry> {

    private LayoutInflater inflater;
    private final ArrayList<MenuEntry> menuOptions;

    public DrawerAdapter(Context context, int resource, ArrayList<MenuEntry> objects, LayoutInflater inflater) {
        super(context, resource, objects);
        this.menuOptions = objects;
        this.inflater = inflater;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent){
        MenuEntry entry = menuOptions.get(position);
        View v = inflater.inflate(R.layout.menu_item_layout, parent, false);
        TextView entryText = (TextView)v.findViewById(R.id.menuEntryText);
        entryText.setText(entry.getEntryText());
        ImageView imageView = (ImageView) v.findViewById(R.id.entryImage);
        imageView.setImageResource(entry.getImageId());
        return v;
    }
}
