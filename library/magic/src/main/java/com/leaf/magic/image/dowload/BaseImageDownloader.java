package com.leaf.magic.image.dowload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;


import com.leaf.magic.image.dowload.ImageDownloadInfo;
import com.leaf.magic.image.dowload.ImageDownloader;
import com.leaf.magic.image.listener.LoadedFrom;
import com.leaf.magic.image.listener.Scheme;
import com.leaf.magic.utils.IoUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hong on 2017/3/20.
 */

public class BaseImageDownloader implements ImageDownloader {

    protected final Context context;
    protected final int connectTimeout;
    protected final int readTimeout;

    public BaseImageDownloader(Context context) {

        this.context = context;
        this.connectTimeout = 5000;
        this.readTimeout = 20000;
    }

    public BaseImageDownloader(Context context, int connectTimeout, int readTimeout) {
        this.context = context;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    @Override
    public InputStream getStream(ImageDownloadInfo imageDownloadInfo) {
        String imageUrl = imageDownloadInfo.getImageUrl();
        try {
            switch (Scheme.getUrl(imageUrl)) {
            case HTTP:
            case HTTPS:
                imageDownloadInfo.setLoadedFrom(LoadedFrom.NETWORK);
                return getStreamFromNetwork(imageUrl, null);
            case FILE:
                imageDownloadInfo.setLoadedFrom(LoadedFrom.FILE_LOCAL);
                return getStreamFromFile(imageUrl);
            case DRAWABLE:
                imageDownloadInfo.setLoadedFrom(LoadedFrom.FILE_LOCAL);
                return getStreamFromDrawable(imageUrl);
            }
        } catch (IOException e) {
        }
        return null;
    }

    protected InputStream getStreamFromFile(String pathUrl) {
        String filePath = Scheme.FILE.crop(pathUrl);
        try {
            return new FileInputStream(new File(filePath));
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    //for network
    protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
        String encodedUrl = Uri.encode(url, "@#&=*+-_.,:!?()/~\'%");
        HttpURLConnection conn = (HttpURLConnection) (new URL(encodedUrl)).openConnection();
        conn.setConnectTimeout(this.connectTimeout);
        conn.setReadTimeout(this.readTimeout);
        return conn;
    }

    protected InputStream getStreamFromNetwork(String imageUri, Object extra) throws IOException {
        HttpURLConnection conn = this.createConnection(imageUri, extra);
        InputStream imageStream;
        try {
            imageStream = conn.getInputStream();
        } catch (IOException e) {
            IoUtils.readAndCloseStream(conn.getErrorStream());
            throw e;
        }
        return imageStream;
    }

    protected InputStream getStreamFromDrawable(String imageUri) {
        String drawableIdString = Scheme.DRAWABLE.crop(imageUri);
        try {
            int drawableId = Integer.parseInt(drawableIdString);
            BitmapDrawable drawable = (BitmapDrawable) this.context.getResources().getDrawable(drawableId);
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (Exception e) {
            Log.e("e", e.toString());
        }
        return null;
    }


}
