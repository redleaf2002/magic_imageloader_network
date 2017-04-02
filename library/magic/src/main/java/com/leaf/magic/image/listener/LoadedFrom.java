package com.leaf.magic.image.listener;

/**
 * Created by Administrator on 2017/3/20.
 */

public enum LoadedFrom {
    NETWORK,
    MEMORY_CACHE,
    FILE_LOCAL,
    DISC_CACHE;

    private LoadedFrom() {
    }
}
