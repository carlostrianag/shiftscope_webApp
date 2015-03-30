package com.shudder.utils.filters;

import android.widget.Filter;

import com.shudder.dto.TrackDTO;
import com.shudder.utils.adapters.LibraryAdapter;

import java.util.ArrayList;

/**
 * Created by Carlos on 1/11/2015.
 */
public class LibraryFilter extends Filter {

    private LibraryAdapter adapter;
    private ArrayList<Object> originalData;

    public LibraryFilter(LibraryAdapter adapter) {
        super();
        this.adapter = adapter;
        this.originalData = new ArrayList(adapter.getFolderContent());
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults result = new FilterResults();
        String query = constraint.toString().toLowerCase();
        ArrayList<TrackDTO> filteredList = new ArrayList<>();
        for (Object element : originalData) {
            if(element.getClass() == TrackDTO.class) {
                TrackDTO track = (TrackDTO) element;
                if(track.getTitle().toLowerCase().contains(query) || track.getArtist().toLowerCase().contains(query)) {
                    filteredList.add(track);
                }
            }
        }
        result.values = filteredList;
        result.count = filteredList.size();
        return result;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.clear();
        ArrayList<TrackDTO> tracks = (ArrayList<TrackDTO>)results.values;
        for (TrackDTO t : tracks) {
            adapter.add(t);
            adapter.notifyDataSetChanged();
        }
    }
}
