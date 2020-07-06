package com.example.myfiles.model;

import android.net.Uri;

public class ImageDetail {

    private Uri uri;

    private String dataPath;
    private String displayName;

    public ImageDetail(Uri uri, String dataPath, String displayName) {
        this.uri = uri;
        this.dataPath = dataPath;
        this.displayName = displayName;
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
