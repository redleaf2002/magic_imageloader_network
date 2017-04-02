package com.leaf.magic.image.dowload;

import com.leaf.magic.image.aware.ImageAware;
import com.leaf.magic.image.listener.LoadListener;
import com.leaf.magic.image.listener.LoadedFrom;

/**
 * Created by hong on 2017/3/29.
 * cacheKey is just for memeory cache
 * for diskcache to use url
 */

public class ImageDownloadInfo {

    private String downloadType;
    private int width;
    private int height;
    private LoadListener loadListener;
    private ImageAware imageAware;
    private LoadedFrom loadedFrom;
    private int extraInfo;
    private String memoryCacheKey;
    private int loadingImageRes;
    private String imageUrl;

    public ImageDownloadInfo(String imageUrl, ImageAware imageAware, String downloadType, LoadListener loadListener, int loadingImageRes, int extraInfo) {
        this.imageUrl = imageUrl;
        this.width = imageAware.getWidth();
        this.height = imageAware.getHeight();
        this.downloadType = downloadType;
        this.loadingImageRes = loadingImageRes;
        this.loadListener = loadListener;
        this.imageAware = imageAware;
        this.extraInfo = extraInfo;
        this.memoryCacheKey = generateKey(imageUrl, width, height);
    }

    private final String generateKey(String imageUri, int width, int height) {
        return imageUri + "_" + width + "x" + height + "_" + extraInfo;
    }

    public int getSampleSize(int bmtWidth, int bmtHeight) {
        if (bmtWidth < width || bmtHeight < height) {
            return 1;
        }
        float rateWidth = (float) bmtWidth / width + 0.5f;
        float rateHeight = (float) bmtHeight / height + 0.5f;
        return Math.min((int) rateWidth, (int) rateHeight);
    }


    public String getMemoryCacheKey() {
        return memoryCacheKey;
    }

    public void setMemoryCacheKey(String memoryCacheKey) {
        this.memoryCacheKey = memoryCacheKey;
    }

    public int getLoadingImageRes() {
        return loadingImageRes;
    }

    public void setLoadingImageRes(int loadingImageRes) {
        this.loadingImageRes = loadingImageRes;
    }


    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDownloadType() {
        return downloadType;
    }

    public void setDownloadType(String downloadType) {
        this.downloadType = downloadType;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public LoadListener getLoadListener() {
        return loadListener;
    }

    public void setLoadListener(LoadListener loadListener) {
        this.loadListener = loadListener;
    }

    public ImageAware getImageAware() {
        return imageAware;
    }

    public void setImageAware(ImageAware imageAware) {
        this.imageAware = imageAware;
    }

    public LoadedFrom getLoadedFrom() {
        return loadedFrom;
    }

    public void setLoadedFrom(LoadedFrom loadedFrom) {
        this.loadedFrom = loadedFrom;
    }

    public int getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(int extraInfo) {
        this.extraInfo = extraInfo;
    }

}
