package com.shudder.views.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.shudder.R;
import com.shudder.controllers.FolderController;
import com.shudder.controllers.LibraryController;
import com.shudder.dto.FolderDTO;
import com.shudder.dto.TrackDTO;
import com.shudder.listeners.FolderListener;
import com.shudder.listeners.LibraryListener;
import com.shudder.netservices.TCPService;
import com.shudder.utils.Operation;
import com.shudder.utils.adapters.LibraryAdapter;
import com.shudder.utils.constants.RequestTypes;
import com.shudder.utils.constants.SessionConstants;


/**
 * Created by Carlos on 1/3/2015.
 */

public class LibraryFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView libraryListView;
    private LibraryListener libraryListener;
    private FolderListener folderListener;
    private RelativeLayout progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ShudderSharedPreferences", Context.MODE_PRIVATE);
        libraryListView = (ListView)getView().findViewById(R.id.libraryListView);
        libraryListView.setOnItemClickListener(this);
        progressBar = (RelativeLayout) getView().findViewById(R.id.progressBarLayout);
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    getFolderContent(SessionConstants.PARENT_FOLDER, libraryListView.onSaveInstanceState());
                    return true;
                }
                return false;
            }
        });

        libraryListener = new LibraryListener() {
            @Override
            public void OnSuccessfulLibraryFetch() {
                getFolderContent(-1, libraryListView.onSaveInstanceState());
            }

            @Override
            public void OnQueueChanged(TrackDTO addedTrack, TrackDTO deletedTrack) {
                ((LibraryAdapter)(libraryListView.getAdapter())).animateFromQueueChange(addedTrack, deletedTrack);
            }

            @Override
            public void OnError() {

            }
        };

        folderListener = new FolderListener() {
            @Override
            public void OnSuccessfulFolderFetch(LibraryAdapter adapter, Parcelable restoredState) {
                super.OnSuccessfulFolderFetch(adapter, restoredState);
                libraryListView.setAdapter(adapter);

                if(restoredState != null) {
                    libraryListView.onRestoreInstanceState(restoredState);
                }
            }

            @Override
            public void OnLoading() {
                progressBar.setVisibility(View.VISIBLE);
                libraryListView.setVisibility(View.GONE);
            }

            @Override
            public void OnLoaded() {
                progressBar.setVisibility(View.GONE);
                libraryListView.setVisibility(View.VISIBLE);
            }

            @Override
            public void OnFailedFolderFetch() {

            }

            @Override
            public void OnOrder(LibraryAdapter adapter) {
                libraryListView.setAdapter(adapter);
            }
        };

        FolderController.addListener(folderListener, this.getActivity());
        LibraryController.addListener(libraryListener);

        LibraryController.getLibraryByDeviceId();
        Log.v("FRAGMENT", "START");
    }

    private void getFolderContent(int id, Parcelable state) {
        FolderController.getFolderContentById(id, state);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object object = parent.getAdapter().getItem(position);
            if(object.getClass() == TrackDTO.class) {
                TrackDTO track = (TrackDTO) object;
                Operation operation = new Operation();
                operation.setId(track.getId());
                operation.setUserId(SessionConstants.USER_ID);
                operation.setTo(SessionConstants.DEVICE_ID);
                operation.setOperationType(RequestTypes.PLAY);
                TCPService.send(operation);
            } else if(object.getClass() == FolderDTO.class) {
                FolderDTO selectedFolder = (FolderDTO) object;
                getFolderContent(selectedFolder.getId(), libraryListView.onSaveInstanceState());
            }
        }
}
