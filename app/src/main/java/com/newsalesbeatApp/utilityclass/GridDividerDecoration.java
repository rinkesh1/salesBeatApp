package com.newsalesbeatApp.utilityclass;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.newsalesbeatApp.R;

/*
 * Created by MTC on 11-05-2017.
 */

public class GridDividerDecoration extends RecyclerView.ItemDecoration {

    //private static final int[] ATTRS = { android.R.attr.listDivider };
    private Drawable mDivider;
    private int mInsets = 10;

    public GridDividerDecoration(Context context) {
        // TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = context.getResources().getDrawable(R.drawable.grid_divider_color);
//        a.recycle();
//        mDivider.setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_OVER);
//        mInsets = insets;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawVertical(c, parent);
        drawHorizontal(c, parent);
    }

    /**
     * Draw dividers at each expected grid interval
     */
    public void drawVertical(Canvas c, RecyclerView parent) {
        if (parent.getChildCount() == 0) return;

        final int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params =
                    (RecyclerView.LayoutParams) child.getLayoutParams();

            final int left = child.getLeft() - params.leftMargin - mInsets;
            final int right = child.getRight() + params.rightMargin + mInsets;
            final int top = child.getBottom() + params.bottomMargin + mInsets;
            final int bottom = top + mDivider.getIntrinsicHeight();

            //Log.e("Bounds","===="+left+"=="+top+"=="+right+"==="+bottom);

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
//        int left = parent.getPaddingLeft();
//        int right = parent.getWidth() - parent.getPaddingRight();
//
//        int childCount = parent.getChildCount();
//        for (int i = 0; i < childCount; i++) {
//            View child = parent.getChildAt(i);
//
//            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
//
//            int top = child.getBottom() + params.bottomMargin;
//            int bottom = top + mDivider.getIntrinsicHeight();
//
//            Log.e("Bounds","===="+left+"=="+top+"=="+right+"==="+bottom);
//
//            mDivider.setBounds(left, top, right, bottom);
//            mDivider.draw(c);
//        }
    }

    /**
     * Draw dividers to the right of each child view
     */
    public void drawHorizontal(Canvas c, RecyclerView parent) {
        final int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params =
                    (RecyclerView.LayoutParams) child.getLayoutParams();

            final int left = child.getRight() + params.rightMargin + mInsets;
            final int right = left + mDivider.getIntrinsicWidth();
            final int top = child.getTop() - params.topMargin - mInsets;
            final int bottom = child.getBottom() + params.bottomMargin + mInsets;
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //We can supply forced insets for each item view here in the Rect
        outRect.set(mInsets, mInsets, mInsets, mInsets);
    }
}