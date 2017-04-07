package com.leaf.magic.image.cache;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DiskUtils {
    public static final String BACKUP_DIR = "image/cache";

    public static File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath = getDiskCacheDir(context);
        return new File(cachePath + File.separator + uniqueName);
    }

    public static File getDiskFixedCacheDir(Context context, String uniqueName) {
        String cachePath = getBackupDir();
        File file = new File(cachePath + File.separator + uniqueName);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static String getDiskCacheDir(Context context) {
        File cacheFile = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cacheFile = context.getExternalCacheDir();
        } else {
            cacheFile = context.getCacheDir();
        }
        if (cacheFile != null) {
            return cacheFile.getPath();
        } else {
            String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
            return Environment.getExternalStorageDirectory().getPath() + cacheDir;
        }
    }

    public static String getBackupDir() {
        return Environment.getExternalStorageDirectory().getPath() + File.separator + BACKUP_DIR;
    }

    public static int getAppVersion(Context context) {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    public static int getAppVersion(Context context, boolean isAlways) {
        if (isAlways) {
            return getAppVersion(context);
        }
        return 1;
    }

    public static File copyFile(File sourceLocation, File targetLocation) throws IOException {

        InputStream in = new FileInputStream(sourceLocation);
        return writeInputStreamFile(in, targetLocation);
    }

    public static File writeInputStreamFile(InputStream in, File targetLocation) throws IOException {

        OutputStream out = new FileOutputStream(targetLocation);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            try {
                out.write(buf, 0, len);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        in.close();
        out.close();
        return targetLocation;
    }

    /**
     * @Title: hashKeyForDisk
     * @Description: TODO(code the key to md5)
     * @param: @param key * @param: @return
     * @return: String * @throws
     */
    public static String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

}
