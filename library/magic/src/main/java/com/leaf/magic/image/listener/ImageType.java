package com.leaf.magic.image.listener;

/**
 * Created by suhong on 17/4/2.
 */
public interface ImageType {
    String DEFAULT = "default";
    String ASSETS = "assets";
    String VIDEO = "video";

    //not called  just for disk cache
    String DIST_CACHE = "diskcache";
}
