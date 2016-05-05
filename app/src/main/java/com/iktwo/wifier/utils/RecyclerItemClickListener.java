package com.iktwo.wifier.utils;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    @SuppressWarnings("unused")
    private static final String TAG = RecyclerItemClickListener.class.getSimpleName();
    private GestureDetector mGestureDetector;
    private OnItemClickListener mListener;
    private View childView;
    private int position;

    public RecyclerItemClickListener(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        mListener = listener;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (e.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                    if (childView != null && mListener != null) {
                        mListener.onItemLongPress(childView, recyclerView.getChildAdapterPosition(childView));
                    }
                }
            }

            @Override
            public boolean onDown(MotionEvent event) {
                return true;
            }
        });
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        childView = view.findChildViewUnder(e.getX(), e.getY());
        position = view.getChildAdapterPosition(childView);

        // Log.d(TAG, "------- gesture: " + mGestureDetector.onTouchEvent(e) + " position: " + position);

        // if (childView != null)
        //    Log.d(TAG, "Initial childView: " + childView.toString().substring(0, 40));

        Object o = null;

        if (childView != null) {
            o = childView.getTag();
        }

        if (childView instanceof ViewGroup) {
            for (int i = ((ViewGroup) childView).getChildCount() - 1; i > 0; --i) {
                View child = ((ViewGroup) childView).getChildAt(i);

                Rect bounds = new Rect();
                child.getHitRect(bounds);

                if (bounds.contains((int) e.getX(), (int) e.getY())) {
                    childView = child;
                    break;
                }
            }
        }

        // if (childView != null)
        //    Log.d(TAG, "New childView: " + childView.toString().substring(0, 40));


        if (o != null) {
            childView.setTag(o);
        }

        if (childView != null && position >= 0 && !(childView instanceof RecyclerView) && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, position);
        }

        return true;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongPress(View view, int position);
    }
}