package com.leaf.magic.image;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.leaf.magic.Magic;
import com.leaf.magic.image.aware.EmptyViewAware;
import com.leaf.magic.image.aware.ImageAware;
import com.leaf.magic.image.aware.ImageViewAware;
import com.leaf.magic.image.dowload.ImageDownloadInfo;
import com.leaf.magic.image.listener.ImageType;
import com.leaf.magic.image.listener.OnLoadListener;
import com.leaf.magic.utils.FileUtils;


/**
 * Created by hong on 2017/3/30.
 */

public class ImageRequest {

    private Magic magic;
    private OnLoadListener loadListener;
    private int loadingImageRes;
    private String imageUrl, imageType;
    private int width, height;
    private int extra;
    private Context mContext;
    private boolean enableCache = true;


    public ImageRequest(Context mContext, Magic magic, String imageUrl, String imageType) {
        this.mContext = mContext;
        this.magic = magic;
        this.imageType = imageType;
        this.imageUrl = imageUrl;
    }

    public ImageRequest addExtra(int extra) {
        this.extra = extra;
        return this;
    }

    public ImageRequest addListener(OnLoadListener loadListener) {
        this.loadListener = loadListener;
        return this;
    }

    //unit:pixels
    public ImageRequest resetSize(int width, int height) {
        if (width < 0) {
            throw new IllegalArgumentException("Width must be positive >=0.");
        }
        if (height < 0) {
            throw new IllegalArgumentException("Height must be positive >= 0.");
        }
        if (width == 0 && height == 0) {
            throw new IllegalArgumentException("At least one dimension has to be positive number.");
        }
        this.width = width;
        this.height = height;
        return this;
    }

    public ImageRequest withImageOnLoading(int resourceId) {
        this.loadingImageRes = resourceId;
        return this;
    }

    public ImageRequest withDiscCache(boolean enableCache) {
        this.enableCache = enableCache;
        return this;
    }

    public void into() {
        into(null);
    }

    public void into(ImageView imageView) {
        if (TextUtils.isEmpty(imageUrl)) {
            throw new IllegalArgumentException("ImageUrl must not be tempty.");
        }
        if (loadListener != null) {
            loadListener.onLoadStarted(imageUrl);
        }
        if (imageView == null) {
            ImageAware imageAware = new EmptyViewAware();
            magic.magicEngine.tryAsyncBitmap(createImageInfo(imageAware));
        } else {
            ImageAware imageAware = new ImageViewAware(imageView);
            magic.magicEngine.tryDisplayImage(createImageInfo(imageAware));
        }

    }

    private ImageDownloadInfo createImageInfo(ImageAware imageAware) {
        imageAware.resetSize(mContext, width, height);
        imageUrl = checkImageUrl(imageUrl);
        return new ImageDownloadInfo(imageUrl, imageAware, imageType, loadListener, loadingImageRes, enableCache, extra);
    }

    private String checkImageUrl(String imageUrl) {
        if (ImageType.FILE.equals(imageType)) {
            imageUrl = FileUtils.getPath(mContext, imageUrl);
        }
        return imageUrl;
    }
}
