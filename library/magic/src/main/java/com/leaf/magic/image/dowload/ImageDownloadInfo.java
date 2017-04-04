package com.leaf.magic.image.dowload;

import com.leaf.magic.image.aware.ImageAware;
import com.leaf.magic.image.listener.ImageType;
import com.leaf.magic.image.listener.OnLoadListener;
import com.leaf.magic.image.listener.LoadForm;

/**
 * Created by hong on 2017/3/29.
 * cacheKey is just for memeory cache
 * for diskcache to use imageUrl
 */

public class ImageDownloadInfo {

    private int width;
    private int height;
    private OnLoadListener loadListener;
    private ImageAware imageAware;
    private boolean enableCache = true;
    private LoadForm loadForm;
    private int extraInfo;
    private String memoryCacheKey;
    private int loadingImageRes;
    private String imageUrl, imageType;

    public ImageDownloadInfo(String imageUrl, ImageAware imageAware, String imageType, OnLoadListener loadListener, int loadingImageRes, boolean enableCache, int extraInfo) {
        this.imageUrl = imageUrl;
        this.width = imageAware.getWidth();
        this.height = imageAware.getHeight();
        this.imageType = imageType;
        this.loadingImageRes = loadingImageRes;
        this.loadListener = loadListener;
        this.imageAware = imageAware;
        this.extraInfo = extraInfo;
        this.memoryCacheKey = generateKey(imageUrl, width, height);
        this.enableCache = enableCache;
    }

    private final String generateKey(String imageUri, int width, int height) {
        if (ImageType.VIDEO.equals(imageType)) {
            return imageUri + "_" + width + "x" + height + "_" + extraInfo;
        }
        return imageUri + "_" + width + "x" + height;
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

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
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

    public OnLoadListener getLoadListener() {
        return loadListener;
    }

    public void setLoadListener(OnLoadListener loadListener) {
        this.loadListener = loadListener;
    }

    public ImageAware getImageAware() {
        return imageAware;
    }

    public void setImageAware(ImageAware imageAware) {
        this.imageAware = imageAware;
    }

    public int getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(int extraInfo) {
        this.extraInfo = extraInfo;
    }

    public boolean isEnableCache() {
        return enableCache;
    }

    public void setEnableCache(boolean enableCache) {
        this.enableCache = enableCache;
    }

    public LoadForm getLoadForm() {
        return loadForm;
    }

    public void setLoadForm(LoadForm loadForm) {
        this.loadForm = loadForm;
    }

}
