package com.example.myfiles.processor.strategy;

import com.example.myfiles.model.ImageDetail;

import java.util.List;

public interface ClusterStrategy {

    int[] getClusters(List<ImageDetail> imageDetails);
}
