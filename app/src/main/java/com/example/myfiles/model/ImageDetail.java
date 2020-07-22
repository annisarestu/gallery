package com.example.myfiles.model;

import android.graphics.Bitmap;
import android.net.Uri;

public class ImageDetail {

    private Bitmap bitmap;

    private Uri uri;

    private String dataPath;

    private String displayName;

    private String mimeType;

    private Long dateModified;

    private Long size;
    
    public ImageDetail(Bitmap bitmap) {
        this(bitmap, null, null, null, null, null, null);
    }

    public ImageDetail(Bitmap bitmap, Uri uri, String dataPath, String displayName, String mimeType,
                       Long dateModified, Long size) {
        this.bitmap = bitmap;
        this.uri = uri;
        this.dataPath = dataPath;
        this.displayName = displayName;
        this.mimeType = mimeType;
        this.dateModified = dateModified;
        this.size = size;
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

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getDateModified() {
        return dateModified;
    }

    public void setDateModified(Long dateModified) {
        this.dateModified = dateModified;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
