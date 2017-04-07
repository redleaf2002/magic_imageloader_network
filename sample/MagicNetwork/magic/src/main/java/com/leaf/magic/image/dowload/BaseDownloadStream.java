package com.leaf.magic.image.dowload;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;


import com.leaf.magic.image.cache.DiskManager;
import com.leaf.magic.image.listener.ImageType;
import com.leaf.magic.image.listener.LoadForm;
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

public class BaseDownloadStream implements DownloadStream {

    protected final Context context;
    protected final int connectTimeout;
    protected final int readTimeout;

    public BaseDownloadStream(Context context) {

        this.context = context;
        this.connectTimeout = 5000;
        this.readTimeout = 20000;
    }

    public BaseDownloadStream(Context context, int connectTimeout, int readTimeout) {
        this.context = context;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    @Override
    public InputStream getStream(ImageDownloadInfo imageDownloadInfo) {
        String imageUrl = imageDownloadInfo.getImageUrl();
        String loadType = imageDownloadInfo.getImageType();
        try {
            if (ImageType.HTTP.equals(loadType)) {
                imageDownloadInfo.setLoadForm(LoadForm.NETWORK);
                return getStreamFromNetwork(imageUrl, null);
            } else if (ImageType.DRAWABLE.equals(loadType)) {
                imageDownloadInfo.setLoadForm(LoadForm.LOCAL_FILE);
                return getStreamFromDrawable(imageUrl);
            } else if (ImageType.FILE.equals(loadType)) {
                imageDownloadInfo.setLoadForm(LoadForm.LOCAL_FILE);
                return getStreamFromFile(imageUrl);
            } else if (ImageType.DISC_CACHE.equals(loadType)) {
                imageDownloadInfo.setLoadForm(LoadForm.LOCAL_FILE);
                return getStreamFromDisc(imageUrl);
            } else if (ImageType.ASSETS.equals(loadType)) {
                imageDownloadInfo.setLoadForm(LoadForm.LOCAL_FILE);
                return getStreamFromAssets(imageUrl);
            } else if (ImageType.VIDEO.equals(loadType)) {
                imageDownloadInfo.setLoadForm(LoadForm.NETWORK);
                return getStreamFromVideo(imageDownloadInfo);
            }


        } catch (IOException e) {
        }
        return null;

    }

    protected InputStream getStreamFromFile(String pathUrl) {
        try {
            return new FileInputStream(new File(pathUrl));
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
        try {
            int drawableId = Integer.parseInt(imageUri);
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


    private InputStream getStreamFromDisc(String imageUrl) {
        return DiskManager.with(context).getStream(imageUrl);
    }

    private InputStream getStreamFromAssets(String imageUri) throws IOException {
        return this.context.getAssets().open(imageUri);
    }

    private InputStream getStreamFromVideo(ImageDownloadInfo imageDownloadInfo) throws IOException {
        try {
            if (imageDownloadInfo.getExtraInfo() < MediaStore.Images.Thumbnails.MINI_KIND || imageDownloadInfo.getExtraInfo() > MediaStore.Video.Thumbnails.MICRO_KIND) {
                imageDownloadInfo.setExtraInfo(MediaStore.Images.Thumbnails.MINI_KIND);
            }
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(imageDownloadInfo.getImageUrl(), imageDownloadInfo.getExtraInfo());
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, os);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (Exception e) {
            Log.e("error", "e: " + e.toString());
        }
        return null;
    }

}
