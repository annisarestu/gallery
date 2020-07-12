package com.example.myfiles.processor.strategy;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.myfiles.model.ImageDetail;

import java.util.List;

import ALI.ClusteringLib;
import ALI.ImageLib;

public class ColorClusterStrategy implements ClusterStrategy {

    @Override
    public int[] getClusters(List<ImageDetail> imageDetails) {
        ClusteringLib clusteringLib = new ClusteringLib();
        ImageLib imageLib = new ImageLib();

        double[][] cvq = new double[imageDetails.size()][];

        for (int i = 0; i < imageDetails.size(); i++) {
            cvq[i] = imageLib.ColorFeatureExtraction(getRgbColors(imageDetails.get(i).getBitmap()));
        }

        return clusteringLib.AutomaticClustering("centroid", cvq);
    }

    private int[][][] getRgbColors(Bitmap bitmap) {
        int[][][] rgbColors = new int[bitmap.getHeight()][bitmap.getWidth()][3];

        for (int i = 0; i < bitmap.getHeight(); i++) {
            for (int j = 0; j < bitmap.getWidth(); j++) {
                int pixel = bitmap.getPixel(j, i);
                rgbColors[i][j][0] = Color.red(pixel);
                rgbColors[i][j][1] = Color.green(pixel);
                rgbColors[i][j][2] = Color.blue(pixel);
            }
        }

        return rgbColors;
    }
}
