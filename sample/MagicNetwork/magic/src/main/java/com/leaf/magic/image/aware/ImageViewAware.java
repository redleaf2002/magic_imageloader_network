package com.leaf.magic.image.aware;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

/**
 * Created by hong on 2017/3/24.
 */

public class ImageViewAware implements ImageAware {
    private Reference<ImageView> mViewRef;
    protected boolean checkActualViewSize;
    private int width, height;

    public ImageViewAware(ImageView imageView) {
        this(imageView, true);
    }

    public ImageViewAware(ImageView imageView, boolean checkActualViewSize) {
        this.mViewRef = new WeakReference(imageView);
        this.checkActualViewSize = checkActualViewSize;
    }

    public ImageView getWrappedView() {
        return this.mViewRef.get();
    }

    @Override
    public boolean isCollected() {
        return this.mViewRef.get() == null;
    }

    public int getDefaultWidth() {
        ImageView imageView = this.mViewRef.get();
        if (imageView != null) {
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            int width = 0;
            if (this.checkActualViewSize && params != null && params.width != -2) {
                width = imageView.getWidth();
            }
            if (width <= 0 && params != null) {
                width = params.width;
            }
            if (width <= 0) {
                width = getImageViewFieldValue(imageView, "mMaxWidth");
            }

            return width;
        } else {
            return 0;
        }
    }

    public int getDefaultHeight() {
        ImageView imageView = this.mViewRef.get();
        if (imageView != null) {
            ViewGroup.LayoutParams params = imageView.getLayoutParams();
            int height = 0;
            if (this.checkActualViewSize && params != null && params.height != -2) {
                height = imageView.getHeight();
            }

            if (height <= 0 && params != null) {
                height = params.height;
            }

            if (height <= 0) {
                height = getImageViewFieldValue(imageView, "mMaxHeight");
            }
            return height;
        } else {
            return 0;
        }
    }


    private int getImageViewFieldValue(Object object, String fieldName) {
        int value = 0;

        try {
            Field e = ImageView.class.getDeclaredField(fieldName);
            e.setAccessible(true);
            int fieldValue = ((Integer) e.get(object)).intValue();
            if (fieldValue > 0 && fieldValue < 2147483647) {
                value = fieldValue;
            }
        } catch (Exception var5) {
        }

        return value;
    }

    public boolean setImageBitmap(Bitmap bitmap) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            ImageView imageView = this.mViewRef.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean setImageResource(int resId) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            ImageView imageView = this.mViewRef.get();
            if (imageView != null) {
                imageView.setImageResource(resId);
                return true;
            }
        } else {
        }
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
            width = getDefaultWidth();
            if (width <= 0) {
                width = displayMetrics.widthPixels;
            }
        }
        if (height <= 0) {
            height = getDefaultHeight();
            if (height <= 0) {
                height = displayMetrics.heightPixels;
            }
        }
        this.width = width;
        this.height = height;
    }

}
