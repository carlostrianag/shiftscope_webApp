package com.shiftscope.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.shiftscope.dto.FolderDTO;
import com.shiftscope.dto.TrackDTO;

import java.util.ArrayList;

import shiftscope.com.shiftscope.R;

/**
 * Created by Carlos on 1/4/2015.
 */
public class LibraryAdapter extends ArrayAdapter<Object>{

    private LayoutInflater inflater;
    private ArrayList<Object> folderContent;

    public LibraryAdapter(Context context, int resource, ArrayList<Object> objects, LayoutInflater inflater) {
        super(context, resource, objects);
        this.inflater = inflater;
        this.folderContent = objects;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return (folderContent.get(position).getClass() == FolderDTO.class)?0:1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        int layoutType = getItemViewType(position);
        if (layoutType == 0 ){
            FolderDTO folder = (FolderDTO)folderContent.get(position);
            View v = inflater.inflate(R.layout.item_library_folder, parent, false);
            TextView folderTitle = (TextView) v.findViewById(R.id.folderTitle);
            folderTitle.setText(folder.getTitle());
            return v;
        } else {
            TrackDTO track = (TrackDTO)folderContent.get(position);
            View v = inflater.inflate(R.layout.item_library_track, parent, false);
            TextView trackTitle = (TextView) v.findViewById(R.id.trackTitle);
            trackTitle.setText(track.getTitle());
            return v;
        }
    }
}
