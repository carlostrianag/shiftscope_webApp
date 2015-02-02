package com.shiftscope.utils;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.shiftscope.dto.FolderDTO;

/**
 * Created by Carlos on 1/16/2015.
 */

public class SwipeDetector implements View.OnTouchListener {

    public static enum Action {
        MOVE,
        LR, // Left to Right
        RL, // Right to Left
        TB, // Top to bottom
        BT, // Bottom to Top
        None // when no action was detected
    }

    private ListView mListView;


    private static final String logTag = "SwipeDetector";
    private static final int MIN_DISTANCE = 100;
    private float downX, downY, upX, upY;
    int absDownX = 0;
    int absDownY = 0;
    int absMoveX = 0;
    int absMoveY = 0;
    float deltaWidth = 0;
    private int[] listViewCoords = new int[2];
    private View selectedView = null;
    private Action mSwipeDetected = Action.None;

    public SwipeDetector(ListView mListView) {
        this.mListView = mListView;

    }

    public boolean swipeDetected() {
        return mSwipeDetected != Action.None;
    }

    public Action getAction() {
        return mSwipeDetected;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getActionMasked()) {

            case MotionEvent.ACTION_DOWN:
                selectedView = null;
                downX = event.getX();
                downY = event.getY();
                mSwipeDetected = Action.None;
                Rect rect = new Rect();
                int childCount = mListView.getChildCount();
                mListView.getLocationOnScreen(listViewCoords);
                absDownX = (int) event.getRawX() - listViewCoords[0];
                absDownY = (int) event.getRawY() - listViewCoords[1];
                View child;
                ArrayAdapter arrayAdapter = (ArrayAdapter) mListView.getAdapter();
                int firstVisibleChild = mListView.getFirstVisiblePosition();
                for (int i = 0; i < childCount; i++) {
                    child = mListView.getChildAt(i);
                    child.getHitRect(rect);
                    if (rect.contains(absDownX, absDownY)) {
                        Log.v(logTag, "SELECCIONADO: " + i);
                        Object item  = arrayAdapter.getItem(i+firstVisibleChild);
                        if(item.getClass() == FolderDTO.class) {
                            selectedView = null;
                        } else {
                            selectedView = child;
                            deltaWidth = absDownX - selectedView.getX();
                        }
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                mSwipeDetected = Action.None;
                upX = event.getX();
                upY = event.getY();
                absMoveX = (int) event.getRawX() - listViewCoords[0];
                absMoveY = (int) event.getRawY() - listViewCoords[1];
                float deltaX = downX - upX;
                float deltaY = downY - upY;

                float absDeltaY = absDownY - absMoveY;
                float absDeltaX = absDownX - absMoveX;
                float newX = absMoveX - deltaWidth;
                Log.v("PRUEBA", " . " + Math.abs(absDeltaY) + " . " + Math.abs(absDeltaX));
                if(Math.abs(absDeltaY) < 70) {
                    if ( selectedView != null ) {

                        if(Math.abs(absDeltaX) > 40) {
                            selectedView.setX(newX);
                        }

                        if (deltaX < 0) {
                            //Log.i(logTag, "Swipe Left to Right");
                            mSwipeDetected = Action.LR;
                        }

                        if (deltaX > 0) {
                            //Log.i(logTag, "Swipe Right to Left");
                            mSwipeDetected = Action.RL;
                        }

                        if(mSwipeDetected == Action.LR && newX > 180) {
                            selectedView.setX(180);
                        }

                        if(mSwipeDetected == Action.RL && newX < 0) {
                            selectedView.setX(0);
                        }
                    }
                } else {
                    mSwipeDetected = Action.None;
                    selectedView = null;
                    return false;
                }
                return true;

            case MotionEvent.ACTION_UP:
                if (selectedView != null) {
                    if (mSwipeDetected == Action.LR && selectedView.getX() == 0) {
                        selectedView.animate().x(150).setDuration(80).start();
                    } else if (mSwipeDetected == Action.RL && selectedView.getX() != 0) {
                        selectedView.animate().x(0).setDuration(80).start();
                    }
                }
                return false;
        }
        return false;
    }
}