package com.shudder.utils.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shudder.controllers.PlaylistController;
import com.shudder.dto.FolderDTO;
import com.shudder.dto.TrackDTO;
import com.shudder.netservices.TCPService;
import com.shudder.utils.Operation;
import com.shudder.utils.constants.Constants;
import com.shudder.utils.constants.RequestTypes;
import com.shudder.utils.constants.SessionConstants;
import com.shudder.utils.filters.LibraryFilter;

import java.util.ArrayList;
import java.util.HashMap;

import shiftscope.com.shiftscope.R;

/**
 * Created by Carlos on 1/4/2015.
 */
public class PlaylistAdapter extends ArrayAdapter<TrackDTO> implements Filterable{

    private Context context;
    private ArrayList<TrackDTO> tracks;
    private LibraryFilter filter;
    private HashMap<Integer, LinearLayout> contentLayouts;


    public PlaylistAdapter(Context context, int resource, ArrayList<TrackDTO> tracks) {
        super(context, resource, tracks);
        this.context = context;
        this.tracks = tracks;
    }


    @Override
    public int getCount() {
        return tracks.size();
    }

    @Override
    public TrackDTO getItem(int position) {
        return tracks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    private View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v  = layoutInflater.inflate(R.layout.item_playlist_track, parent, false);
        final TrackDTO track = tracks.get(position);
        LinearLayout trashLayout = (LinearLayout) v.findViewById(R.id.removeFromPlaylistLayout);
        TextView trackTitle = (TextView) v.findViewById(R.id.trackTitle);

        trackTitle.setText(track.getTitle().toUpperCase());
        TextView artistName = (TextView) v.findViewById(R.id.artistName);
        artistName.setText(track.getArtist().toUpperCase());

        trashLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Operation operation = new Operation();
                operation.setId(track.getId());
                operation.setUserId(SessionConstants.USER_ID);
                operation.setOperationType(RequestTypes.REMOVE_FROM_PLAYLIST);
                operation.setTo(SessionConstants.DEVICE_ID);
                TCPService.send(operation);
            }
        });
        return v;
    }

//    @Override
//    public Filter getFilter() {
//        if(filter == null) {
//            filter = new LibraryFilter(this);
//        }
//        return filter;
//    }

    private void animateToRight(View v) {
        v.animate().x(Constants.MAX_X_POSITION).setDuration(150).start();
    }

    private void animateToLeft(View v) {
        v.animate().x(0).setDuration(150).start();
    }
}
