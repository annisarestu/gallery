package com.example.myfiles.model;

import java.util.List;

public class StaticImages {

    public static List<ImageDetail> imageDetails;

    public static List<ImageDetail> getImageDetails() {
        return imageDetails;
    }

    public static void setImageDetails(List<ImageDetail> imageDetails) {
        StaticImages.imageDetails = imageDetails;
    }
}
