package com.shiftscope.controllers;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Filter;

import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shiftscope.dto.FolderContentDTO;
import com.shiftscope.netservices.HTTPService;
import com.shiftscope.utils.adapters.LibraryAdapter;
import com.shiftscope.utils.comparators.ArtistComparator;
import com.shiftscope.utils.comparators.TitleComparator;
import com.shiftscope.utils.constants.ControllerEvent;
import com.shiftscope.utils.constants.SessionConstants;

import org.apache.http.Header;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


/**
 * Created by Carlos on 1/4/2015.
 */
public class FolderController {

    private static FolderContentDTO folderContentDTO;
    private static FolderCommunicator communicator;
    private static Context context;
    private static LayoutInflater inflater;
    private static LibraryAdapter adapter;
    private static HashMap<Integer, Parcelable> statesHashMap = new HashMap<>();
    private static boolean orderByArtist = true;
    private static boolean orderByTitle = false;

    public static void setCommunicator(Fragment fragment) {
        context = fragment.getActivity().getApplicationContext();
        inflater = fragment.getActivity().getLayoutInflater();
        communicator = (FolderCommunicator) fragment;
    }

    public static void search(String query) {
        new FolderWorker(ControllerEvent.ON_FILTER_QUERY, query).execute();
    }

    public static void orderByTitle() {
        new FolderWorker(ControllerEvent.ON_ORDER_BY_SONG_TITLE).execute();
    }

    public static void orderByArtist() {
        new FolderWorker(ControllerEvent.ON_ORDER_BY_ARTIST).execute();
    }

    public static void getFolderContentById(final int id, final Parcelable state){
        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if(statusCode == 200) {
                    new FolderWorker(ControllerEvent.ON_SUCCESSFUL_FOLDER_FETCH, response, id, state).execute();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.v("MIO", responseString);
            }
        };
        RequestParams params = new RequestParams();
        params.add("id", String.valueOf(id));
        params.add("library", String.valueOf(SessionConstants.LIBRARY_ID));
        HTTPService.get("folder/getFolderContentById", params, responseHandler);
    }


    private static class FolderWorker extends AsyncTask<Void, Void, Void> {
        private ControllerEvent event;
        private JSONObject response;
        private String query;
        private Filter filter;
        private Parcelable savedState;
        private Parcelable restoredState;
        private int parentFolder;
        private int folderId;

        public FolderWorker(ControllerEvent event) {
            super();
            this.event = event;
        }

        public FolderWorker(ControllerEvent event, JSONObject response) {
            super();
            this.event = event;
            this.response = response;
        }

        public FolderWorker(ControllerEvent event, JSONObject response, int folderId,  Parcelable savedState) {
            super();
            this.event = event;
            this.response = response;
            this.savedState = savedState;
            this.folderId = folderId;
        }

        public FolderWorker(ControllerEvent event, String query) {
            super();
            this.event = event;
            this.query = query;
            this.filter = adapter.getFilter();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<Object> folderContent;
            switch (event) {
                case ON_SUCCESSFUL_FOLDER_FETCH:

                    Gson JSONParser = new Gson();
                    folderContent = new ArrayList<>();
                    folderContentDTO = JSONParser.fromJson(response.toString(), FolderContentDTO.class);

                    parentFolder = folderContentDTO.getParentFolder();
                    if(orderByTitle) {
                        Collections.sort(folderContentDTO.getTracks(), new TitleComparator());
                    } else if (orderByArtist) {
                        Collections.sort(folderContentDTO.getTracks(), new ArtistComparator());
                    }

                    folderContent.addAll(folderContentDTO.getFolders());
                    folderContent.addAll(folderContentDTO.getTracks());
                    adapter = new LibraryAdapter(context, android.R.layout.simple_list_item_1, folderContent, inflater);
                    SessionConstants.PARENT_FOLDER = folderContentDTO.getParentFolder();
                    break;
                case ON_FILTER_QUERY:
                    filter.filter(query);
                    break;
                case ON_ORDER_BY_SONG_TITLE:
                    orderByTitle = true;
                    orderByArtist = false;
                    Collections.sort(folderContentDTO.getTracks(), new TitleComparator());
                    folderContent = new ArrayList<>();
                    folderContent.addAll(folderContentDTO.getFolders());
                    folderContent.addAll(folderContentDTO.getTracks());
                    adapter = new LibraryAdapter(context, android.R.layout.simple_list_item_1, folderContent, inflater);
                    break;

                case ON_ORDER_BY_ARTIST:
                    orderByTitle = false;
                    orderByArtist = true;
                    Collections.sort(folderContentDTO.getTracks(), new ArtistComparator());
                    folderContent = new ArrayList<>();
                    folderContent.addAll(folderContentDTO.getFolders());
                    folderContent.addAll(folderContentDTO.getTracks());
                    adapter = new LibraryAdapter(context, android.R.layout.simple_list_item_1, folderContent, inflater);
                    break;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            switch(event){
                case ON_SUCCESSFUL_FOLDER_FETCH:
                    restoredState = statesHashMap.get(parentFolder);
                    if(restoredState == null) {
                        statesHashMap.put(parentFolder, savedState);
                    } else{
                        restoredState = statesHashMap.get(folderId);
                        if(restoredState != null) {
                            statesHashMap.remove(folderId);
                        }
                    }
                    communicator.onSuccessfulFolderFetch(adapter, restoredState);
                    break;
                case ON_FILTER_QUERY:

                    break;

                case ON_ORDER_BY_SONG_TITLE:
                    communicator.onOrder(adapter);
                    break;
                case ON_ORDER_BY_ARTIST:
                    communicator.onOrder(adapter);
                    break;
            }
        }
    }

    public interface FolderCommunicator {
        public void onSuccessfulFolderFetch(LibraryAdapter adapter, Parcelable restoredState);
        public void onFailedFolderFetch();
        public void onOrder(LibraryAdapter adapter);
    }

}
