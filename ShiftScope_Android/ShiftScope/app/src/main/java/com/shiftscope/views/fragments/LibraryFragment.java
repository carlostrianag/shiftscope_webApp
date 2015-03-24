package com.shiftscope.views.fragments;

import android.app.ProgressDialog;
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

import com.daimajia.swipe.implments.SwipeItemMangerImpl;
import com.shiftscope.controllers.FolderController;
import com.shiftscope.controllers.LibraryController;
import com.shiftscope.dto.FolderDTO;
import com.shiftscope.dto.TrackDTO;
import com.shiftscope.netservices.TCPService;
import com.shiftscope.utils.Operation;
import com.shiftscope.utils.SwipeDetector;
import com.shiftscope.utils.adapters.LibraryAdapter;
import com.shiftscope.utils.constants.RequestTypes;
import com.shiftscope.utils.constants.SessionConstants;

import java.util.ArrayList;

import shiftscope.com.shiftscope.R;

/**
 * Created by Carlos on 1/3/2015.
 */

public class LibraryFragment extends Fragment implements AdapterView.OnItemClickListener, LibraryController.LibraryCommunicator, FolderController.FolderCommunicator{

    private ListView libraryListView;
    private ProgressDialog progressDialog;
    private SwipeDetector swipeDetector;
    private LibraryAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LibraryController.setCommunicator(this);
        FolderController.setCommunicator(this);

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        libraryListView = (ListView)getView().findViewById(R.id.libraryListView);
        swipeDetector = new SwipeDetector(libraryListView);
        libraryListView.setOnTouchListener(swipeDetector);
        libraryListView.setOnItemClickListener(this);
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
        LibraryController.getLibraryByDeviceId();
    }

    private void getFolderContent(int id, Parcelable state) {
        showProgressDialog();
        FolderController.getFolderContentById(id, state);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Object object = parent.getAdapter().getItem(position);
        if(swipeDetector.swipeDetected()) {
            if(object.getClass() == TrackDTO.class) {
                TrackDTO track = (TrackDTO) object;
                Operation operation = new Operation();
                operation.setId(track.getId());
                operation.setUserId(SessionConstants.USER_ID);
                operation.setTo(SessionConstants.DEVICE_ID);
                if(swipeDetector.getAction() == SwipeDetector.Action.LR) {
                    operation.setOperationType(RequestTypes.ENQUEUE);
                    LibraryController.addId(track.getId());
                } else if (swipeDetector.getAction() == SwipeDetector.Action.RL) {
                    operation.setOperationType(RequestTypes.REMOVE_FROM_PLAYLIST);
                    LibraryController.removeId(track.getId());
                }
                TCPService.send(operation);
            }
        } else {
            if(object.getClass() == FolderDTO.class) {
                FolderDTO selectedFolder = (FolderDTO) object;
                getFolderContent(selectedFolder.getId(), libraryListView.onSaveInstanceState());
            } else {
                TrackDTO track = (TrackDTO) object;
                Operation operation = new Operation();
                operation.setId(track.getId());
                operation.setUserId(SessionConstants.USER_ID);
                operation.setOperationType(RequestTypes.PLAY);
                operation.setTo(SessionConstants.DEVICE_ID);
                TCPService.send(operation);
            }
        }
    }

    @Override
    public void onSuccessfulLibraryFetch() {
        getFolderContent(-1, libraryListView.onSaveInstanceState());
    }

    @Override
    public void onQueueChanged(TrackDTO addedTrack, TrackDTO deletedTrack) {
        Log.v("HAY ENTREEE ", "DE UNA");
        if (addedTrack != null) {
            Log.v("HAY ENTREEE ", "DE DOS");
            View trackView = adapter.getAdapterViewById(addedTrack.getId());
            if (trackView != null) {
                swipeDetector.moveView(trackView);
            }
        }
    }

    @Override
    public void onFailedLibraryFetch() {

    }

    @Override
    public void onSuccessfulFolderFetch(LibraryAdapter adapter, Parcelable restoredState) {
        this.adapter = adapter;
        libraryListView.setAdapter(adapter);

        if(restoredState != null) {
            libraryListView.onRestoreInstanceState(restoredState);
        }
        dismissProgressDialog();
    }

    @Override
    public void onFailedFolderFetch() {

    }

    @Override
    public void onOrder(LibraryAdapter adapter) {
        libraryListView.setAdapter(adapter);
        Log.v("DEBUG", adapter.toString() + " set");
        dismissProgressDialog();
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading, please wait ...");
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
