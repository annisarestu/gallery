package com.example.myfiles.model;

import android.graphics.Bitmap;
import android.net.Uri;

public class ImageDetail {

    private Bitmap bitmap;

    private Uri uri;

    private String dataPath;
    private String displayName;

    public ImageDetail(Bitmap bitmap) {
        this(bitmap, null, null, null);
    }

    public ImageDetail(Bitmap bitmap, Uri uri, String dataPath, String displayName) {
        this.bitmap = bitmap;
        this.uri = uri;
        this.dataPath = dataPath;
        this.displayName = displayName;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
