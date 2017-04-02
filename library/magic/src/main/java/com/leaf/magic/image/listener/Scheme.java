package com.leaf.magic.image.listener;

import java.util.Locale;

/**
 * Created by Administrator on 2017/3/20.
 */

public enum Scheme {
    HTTP("http"),
    HTTPS("https"),
    FILE("file"),
    CONTENT("content"),
    ASSETS("assets"),
    DRAWABLE("drawable"),
    UNKNOWN(""),
    APK("apk"),
    VIDEO("video"),
    PKG("pkg") ;

    private String scheme;
    private String uriPrefix;

    private Scheme(String scheme) {
        this.scheme = scheme;
        this.uriPrefix = scheme + "://";
    }

    public static Scheme getUrl(String uri) {
        if (uri != null) {
            Scheme[] mSchemes;
            int size = (mSchemes = values()).length;
            for (int i = 0; i < size; i++) {
                Scheme s = mSchemes[i];
                if (s.belongsTo(uri)) {
                    return s;
                }
            }
        }
        return UNKNOWN;
    }

    private boolean belongsTo(String uri) {
        return uri.toLowerCase(Locale.US).startsWith(this.uriPrefix);
    }

    public String crop(String uri) {
        if (!this.belongsTo(uri)) {
            throw new IllegalArgumentException(String.format("URI [%1$s] doesn\'t have expected scheme [%2$s]", new Object[]{uri, this.scheme}));
        } else {
            return uri.substring(this.uriPrefix.length());
        }
    }

}
