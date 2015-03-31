package com.shudder.utils.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shudder.controllers.LibraryController;
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
import java.util.Objects;

import shiftscope.com.shiftscope.R;

/**
 * Created by Carlos on 1/4/2015.
 */
public class LibraryAdapter extends ArrayAdapter<Object> implements Filterable{

    private Context context;
    private ArrayList<Object> folderContent;
    private LibraryFilter filter;
    private FolderDTO folder;
    private HashMap<Integer, LinearLayout> contentLayouts;


    public LibraryAdapter(Context context, int resource, ArrayList<Object> objects) {
        super(context, resource, objects);
        this.context = context;
        this.folderContent = objects;
        Constants.MAX_X_POSITION = convertToPx(75);
        contentLayouts = new HashMap<>();
    }


    private class FolderViewHolder {
        public TextView folderTitle;

        private FolderViewHolder(View  v) {
            folderTitle = (TextView) v.findViewById(R.id.folderTitle);
        }
    }

    private class TrackViewHolder {
        public TextView trackTitle;
        public TextView artistName;

        private TrackViewHolder(View v) {
            trackTitle = (TextView) v.findViewById(R.id.trackTitle);
            artistName =  (TextView) v.findViewById(R.id.artistName);
        }
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
    public int getCount() {
        return folderContent.size();
    }

    @Override
    public Object getItem(int position) {
        return folderContent.get(position);
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
        int layoutType = getItemViewType(position);
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v;
        switch (layoutType) {
            case 0:
                folder = (FolderDTO)folderContent.get(position);
                v  = layoutInflater.inflate(R.layout.item_library_folder, parent, false);
                TextView folderTitle = (TextView) v.findViewById(R.id.folderTitle);
                folderTitle.setText(folder.getTitle().toUpperCase());
                return v;
            case 1:
                v  = layoutInflater.inflate(R.layout.item_library_track, parent, false);

                final TrackDTO track = (TrackDTO)folderContent.get(position);
                final LinearLayout contentLayout = (LinearLayout) v.findViewById(R.id.contentLayout);
                contentLayouts.put(track.getId(), contentLayout);
                final LinearLayout checkLayout = (LinearLayout) v.findViewById(R.id.checkLayout);
                if (PlaylistController.contains(track.getId())) {
                    contentLayout.setX(Constants.MAX_X_POSITION);
                }
                final TextView trackTitle = (TextView) v.findViewById(R.id.trackTitle);
                LinearLayout addBtn = (LinearLayout) v.findViewById(R.id.addToPlaylistLayout);
                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        animateToRight(contentLayout);
                        Operation operation = new Operation();
                        operation.setId(track.getId());
                        operation.setUserId(SessionConstants.USER_ID);
                        operation.setTo(SessionConstants.DEVICE_ID);
                        operation.setOperationType(RequestTypes.ENQUEUE);
                        TCPService.send(operation);
                    }
                });

                checkLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        animateToLeft(contentLayout);
                        Operation operation = new Operation();
                        operation.setId(track.getId());
                        operation.setUserId(SessionConstants.USER_ID);
                        operation.setTo(SessionConstants.DEVICE_ID);
                        operation.setOperationType(RequestTypes.REMOVE_FROM_PLAYLIST);
                        TCPService.send(operation);

                    }
                });
                trackTitle.setText(track.getTitle().toUpperCase());
                TextView artistName = (TextView) v.findViewById(R.id.artistName);
                artistName.setText(track.getArtist().toUpperCase());
                return v;
        }
        return null;
    }
    private View getAdapterViewById(int id) {
        for (int position = 0; position < getCount(); position++) {
            Object o = getItem(position);
            if (o instanceof TrackDTO) {
                TrackDTO track = (TrackDTO) o;
                if(track.getId() == id) {
                    return contentLayouts.get(track.getId());
                }
            }
        }
        return null;
    }

    public void animateFromQueueChange(TrackDTO addedTrack, TrackDTO deletedTrack) {
        if (addedTrack != null) {
            LinearLayout linearLayout = (LinearLayout)getAdapterViewById(addedTrack.getId());
            if (linearLayout != null) {
                animateToRight(linearLayout);
            }
        } else if (deletedTrack != null) {
            LinearLayout linearLayout = (LinearLayout)getAdapterViewById(deletedTrack.getId());
            if (linearLayout != null) {
                animateToLeft(linearLayout);
            }
        }
    }

    @Override
    public Filter getFilter() {
        if(filter == null) {
            filter = new LibraryFilter(this);
        }
        return filter;
    }

    public ArrayList<Object> getFolderContent() {
        return folderContent;
    }

    private int convertToPx(int dp) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private void animateToRight(View v) {
        v.animate().x(Constants.MAX_X_POSITION).setDuration(150).start();
    }

    private void animateToLeft(View v) {
        v.animate().x(0).setDuration(150).start();
    }
}
