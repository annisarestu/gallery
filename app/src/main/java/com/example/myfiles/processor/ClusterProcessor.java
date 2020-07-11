package com.example.myfiles.processor;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.example.myfiles.model.ImageDetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import ALI.ClusteringLib;
import ALI.ImageLib;

public class ClusterProcessor {

    public List<ImageDetail> processCluster(Bitmap bitmap, List<ImageDetail> imageDetails) {
        ClusteringLib clusteringLib = new ClusteringLib();
        ImageLib imageLib = new ImageLib();

        imageDetails.add(0, new ImageDetail(bitmap));

        double[][] cvq = new double[imageDetails.size()][];

        for (int i = 0; i < imageDetails.size(); i++) {
            cvq[i] = imageLib.ColorFeatureExtraction(getRgbColors(imageDetails.get(i).getBitmap()));
        }

        int[] clusters = clusteringLib.AutomaticClustering("centroid", cvq);
        List<List<ImageDetail>> clusteredBitmaps = getClusteredBitmaps(clusters, imageDetails);
        return clusteredBitmaps.get(clusters[0]);
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

    private List<List<ImageDetail>> getClusteredBitmaps(int[] clusters, List<ImageDetail> imageDetails) {
        Map<Integer, List<ImageDetail>> result = new HashMap<>();

        for (int i = 1; i < clusters.length; i++) {
            List<ImageDetail> bitmapsCluster = result.get(clusters[i]);
            ImageDetail imageDetail = imageDetails.get(i);

            if (bitmapsCluster != null) {
                bitmapsCluster.add(imageDetail);
            } else {
                bitmapsCluster = new ArrayList<>();
                bitmapsCluster.add(imageDetail);
                result.put(clusters[i], bitmapsCluster);
            }
        }

        return convertToListResult(result);
    }

    private List<List<ImageDetail>> convertToListResult(Map<Integer, List<ImageDetail>> result) {
        List<List<ImageDetail>> listResult = new ArrayList<>();
        Set<Integer> indexes = new TreeSet<>(result.keySet());

        for (Integer index : indexes) {
            listResult.add(result.get(index));
        }

        return listResult;
    }
}
