package com.leaf.magic.image.aware;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

/**
 * Created by hong on 2017/3/24.
 */

public interface ImageAware {
    int getWidth();

    int getHeight();

    View getWrappedView();

    boolean isCollected();

    boolean setImageBitmap(Bitmap btm);

    boolean setImageResource(int resId);

    void resetSize(Context mContext, int width, int height);
}
