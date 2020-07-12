package com.example.myfiles.processor.strategy;

import com.example.myfiles.model.ImageDetail;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateClusterStrategy implements ClusterStrategy {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public int[] getClusters(List<ImageDetail> imageDetails) {
        List<String> dates = new ArrayList<>();
        int[] result = new int[imageDetails.size()];

        for (int i = 0; i < result.length; i++) {
            result[i] = searchClusters(imageDetails.get(i), dates);
        }

        return result;
    }

    private int searchClusters(ImageDetail imageDetail, List<String> dates) {
        Date date = new Date(imageDetail.getDateModified());
        String dateFormat = DATE_FORMAT.format(date);

        if (!dates.contains(dateFormat)) {
            dates.add(dateFormat);
        }

        return dates.indexOf(dateFormat);
    }
}
