package com.example.myfiles.processor;

import com.example.myfiles.model.ImageDetail;
import com.example.myfiles.processor.strategy.ClusterStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ClusterProcessor {

    public List<ImageDetail> processCluster(List<ImageDetail> imageDetails,
                                            ClusterStrategy clusterStrategy) {
        int[] clusters = clusterStrategy.getClusters(imageDetails);
        List<List<ImageDetail>> clusteredBitmaps = getClusteredBitmaps(clusters, imageDetails);
        return clusteredBitmaps.get(clusters[0]);
    }

    private List<List<ImageDetail>> getClusteredBitmaps(int[] clusters, List<ImageDetail> imageDetails) {
        Map<Integer, List<ImageDetail>> result = new HashMap<>();

        for (int i = 0; i < clusters.length; i++) {
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
