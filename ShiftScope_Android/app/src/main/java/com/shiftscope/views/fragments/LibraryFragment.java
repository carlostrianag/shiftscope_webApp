package com.shiftscope.views.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shiftscope.controllers.FolderController;
import com.shiftscope.controllers.LibraryController;
import com.shiftscope.dto.FolderContentDTO;
import com.shiftscope.dto.FolderDTO;
import com.shiftscope.dto.LibraryDTO;
import com.shiftscope.dto.TrackDTO;
import com.shiftscope.utils.LibraryAdapter;
import com.shiftscope.utils.SessionConstants;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;

import shiftscope.com.shiftscope.R;

/**
 * Created by Carlos on 1/3/2015.
 */
public class LibraryFragment extends Fragment implements AdapterView.OnItemClickListener{

    private JsonHttpResponseHandler folderFetchResponseHandler;
    private JsonHttpResponseHandler libraryFetchResponseHandler;
    private ListView libraryListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        folderFetchResponseHandler = new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Gson JSONParser = new Gson();
                ArrayList<Object> folderContent = new ArrayList<>();
                FolderContentDTO folderContentDTO = JSONParser.fromJson(response.toString(), FolderContentDTO.class);
                folderContent.addAll(folderContentDTO.getFolders());
                folderContent.addAll(folderContentDTO.getTracks());
                LibraryAdapter adapter = new LibraryAdapter(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, folderContent, getActivity().getLayoutInflater());
                libraryListView.setAdapter(adapter);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.v("MIO", responseString);
            }
        };

        libraryFetchResponseHandler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.v("MIO", "libreria:" + response.toString());
                Gson JSONParser = new Gson();
                LibraryDTO library = JSONParser.fromJson(response.toString(), LibraryDTO.class);
                SessionConstants.LIBRARY_ID = library.getId();
                getFolderContent(-1);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.v("MIO", "errror libreria:" + responseString);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        libraryListView = (ListView)getView().findViewById(R.id.libraryListView);
        libraryListView.setOnItemClickListener(this);
        new LibraryStarter().execute();
    }

    private void getFolderContent(int id) {
        FolderController.getFolderContentById(id, folderFetchResponseHandler);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object object = parent.getItemAtPosition(position);
        if(object.getClass() == FolderDTO.class) {
            FolderDTO selectedFolder = (FolderDTO) object;
            Log.v("MIO", "ESTOY PIDIENDOO"+selectedFolder.getId());
            getFolderContent(selectedFolder.getId());
        } else {
            TrackDTO track = (TrackDTO) object;
            Log.v("MIO", track.getTitle()+ " - " + track.getArtist());
        }
    }


    public class LibraryStarter extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            LibraryController.getLibraryByDeviceId(libraryFetchResponseHandler);
            return null;
        }
    }
}
