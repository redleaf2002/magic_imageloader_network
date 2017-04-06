package com.leaf.magic.image.listener;

/**
 * Created by hong
 * 4 ways to get a imageStream
 * the network mode has a disk cache and local mode hasn't a disk cache by default.
 * In order to prevent repetitive storage
 * ex:local file for drawable asset
 */

public enum LoadForm {

    MEMORY_CACHE,
    DISC_CACHE,
    NETWORK,
    LOCAL_FILE;

}
