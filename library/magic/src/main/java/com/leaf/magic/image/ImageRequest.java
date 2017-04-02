package com.leaf.magic.image;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.leaf.magic.Magic;
import com.leaf.magic.image.aware.ImageAware;
import com.leaf.magic.image.aware.ImageViewAware;
import com.leaf.magic.image.dowload.ImageDownloadInfo;
import com.leaf.magic.image.listener.ImageType;
import com.leaf.magic.image.listener.LoadListener;
import com.leaf.magic.utils.FileUtils;


/**
 * Created by hong on 2017/3/30.
 */

public class ImageRequest {

    private Magic magic;
    private String loadType;
    private int extra;
    private LoadListener loadListener;
    private int loadingImageRes;
    private String imageUrl;
    private int width, height;
    private Context mContext;


    public ImageRequest(Context mContext, Magic imageLoader, String imageUrl, int resId) {
        this.mContext = mContext;
        this.magic = imageLoader;
        if (resId != 0) {
            imageUrl = "drawable://" + String.valueOf(resId);
        }
        this.imageUrl = imageUrl;
    }

    //for extra custom imageloader
    public ImageRequest withType(String loadType) {
        return this.withType(loadType, 0);
    }

    public ImageRequest withType(String loadType, int extra) {
        this.loadType = loadType;
        this.extra = extra;
        return this;
    }

    public ImageRequest addListener(LoadListener loadListener) {
        this.loadListener = loadListener;
        return this;
    }

    public ImageRequest reSize(int width, int height) {
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

    public void into(ImageView imageView) {
        ImageAware imageAware = new ImageViewAware(imageView);
        imageAware.resetSize(mContext, width, height);
        if (TextUtils.isEmpty(loadType)) {
            loadType = ImageType.DEFAULT;
        }
        imageUrl = checkImageUrl(imageUrl, loadType);
        ImageDownloadInfo imageDownloadInfo = new ImageDownloadInfo(imageUrl, imageAware, loadType, loadListener, loadingImageRes, extra);
        magic.magicEngine.tryDisplayImage(imageDownloadInfo);
    }

    private String checkImageUrl(String imageUrl, String loadType) {
        if (loadType.equals(ImageType.ASSETS)) {
            return "assets://" + imageUrl;
        } else if (loadType.equals(ImageType.VIDEO)) {
            return "video://" + imageUrl;
        } else if (loadType.equals(ImageType.DEFAULT)) {
            imageUrl = FileUtils.getPath(mContext, imageUrl);
            if (TextUtils.isEmpty(imageUrl)) {
                return null;
            }
            if (!imageUrl.startsWith("drawable://") && !imageUrl.startsWith("http://") && !imageUrl.startsWith("https://") && !imageUrl.startsWith("file://")) {
                return "file://" + imageUrl;
            }
            return imageUrl;
        }

        return imageUrl;
    }
}
