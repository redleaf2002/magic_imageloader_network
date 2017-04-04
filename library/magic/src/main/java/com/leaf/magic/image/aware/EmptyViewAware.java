package com.leaf.magic.image.aware;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import java.lang.ref.Reference;

/**
 * Created by hong on 2017/3/24.
 */

public class EmptyViewAware implements ImageAware {
    private int width, height;

    public EmptyViewAware() {
    }

    public ImageView getWrappedView() {
        return null;
    }

    @Override
    public boolean isCollected() {
        return false;
    }

    @Override
    public boolean setImageBitmap(Bitmap btm) {
        return false;
    }

    @Override
    public boolean setImageResource(int resId) {
        return false;
    }


    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public void resetSize(Context mContext, int width, int height) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        if (width <= 0) {
            if (width <= 0) {
                width = displayMetrics.widthPixels;
            }
        }
        if (height <= 0) {
            if (height <= 0) {
                height = displayMetrics.heightPixels;
            }
        }
        this.width = width;
        this.height = height;
    }

}
