package com.shiftscope.views.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shiftscope.dto.TrackDTO;
import com.shiftscope.utils.adapters.LibraryAdapter;

import java.util.ArrayList;
import java.util.Objects;

import shiftscope.com.shiftscope.R;

/**
 * Created by Carlos on 2/4/2015.
 */
public class PlayListFragment extends Fragment {


    private SharedPreferences sharedPreferences;
    private ListView playListListView;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_playlist, container, false);
        Gson JSONParser = new Gson();
        sharedPreferences = getActivity().getSharedPreferences("ShudderSharedPreferences", Context.MODE_PRIVATE);
        ArrayList<Object> playList = JSONParser.fromJson(sharedPreferences.getString("currentPlaylist", ""), new TypeToken<ArrayList<TrackDTO>>(){}.getType());
        LibraryAdapter adapter = new LibraryAdapter(getActivity(), android.R.layout.simple_list_item_1, playList);
        playListListView = (ListView) v.findViewById(R.id.playListListView);
        playListListView.setAdapter(adapter);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
