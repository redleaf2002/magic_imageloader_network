package com.leaf.magic.image.dowload;


import java.io.InputStream;

/**
 * Created by Administrator on 2017/3/20.
 */

public interface ImageDownloader {
    InputStream getStream(ImageDownloadInfo imageDownloadInfo);
}
