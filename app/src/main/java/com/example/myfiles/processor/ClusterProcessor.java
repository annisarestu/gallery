package com.example.myfiles.processor;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import ALI.ClusteringLib;
import ALI.ImageLib;

public class ClusterProcessor {

    public List<Bitmap> processCluster(Bitmap bitmap, List<Bitmap> bitmaps) {
        ClusteringLib clusteringLib = new ClusteringLib();
        ImageLib imageLib = new ImageLib();

        bitmaps.add(0, bitmap);

        double[][] cvq = new double[bitmaps.size()][];

        for (int i = 0; i < bitmaps.size(); i++) {
            cvq[i] = imageLib.ColorFeatureExtraction(getRgbColors(bitmaps.get(i)));
        }

        int[] clusters = clusteringLib.AutomaticClustering("centroid", cvq);
        List<List<Bitmap>> clusteredBitmaps = getClusteredBitmaps(clusters, bitmaps);
        return clusteredBitmaps.get(clusters[0]);
    }

    private int[][][] getRgbColors(Bitmap bitmap) {
        int[][][] rgbColors = new int[bitmap.getHeight()][bitmap.getWidth()][3];

        for (int i = 0; i < bitmap.getHeight(); i++) {
            for (int j = 0; j < bitmap.getWidth(); j++) {
                int pixel = bitmap.getPixel(i, j);
                rgbColors[i][j][0] = Color.red(pixel);
                rgbColors[i][j][1] = Color.green(pixel);
                rgbColors[i][j][2] = Color.blue(pixel);
            }
        }

        return rgbColors;
    }

    private List<List<Bitmap>> getClusteredBitmaps(int[] clusters, List<Bitmap> bitmaps) {
        List<List<Bitmap>> result = new ArrayList<>();

        for (int i = 1; i < clusters.length; i++) {
            List<Bitmap> bitmapsCluster = result.get(clusters[i]);
            Bitmap bitmap = bitmaps.get(i);

            if (bitmapsCluster != null) {
                bitmapsCluster.add(bitmap);
            } else {
                bitmapsCluster = new ArrayList<>();
                bitmapsCluster.add(bitmap);
                result.set(clusters[i], bitmapsCluster);
            }
        }

        return result;
    }
}
