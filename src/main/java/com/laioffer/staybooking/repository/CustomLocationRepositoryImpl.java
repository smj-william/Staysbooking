package com.laioffer.staybooking.repository;

import com.laioffer.staybooking.entity.Location;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class CustomLocationRepositoryImpl implements CustomLocationRepository{

    private final String DEFAULT_DISTANCE = "50";

    private ElasticsearchOperations elasticsearchOperations;
    //从ElasticsearchConfig 返回的, elasticsearchOperations是RestHighLevelClient 的父类，不用细究

    @Autowired
    public CustomLocationRepositoryImpl(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    public List<Long> searchByDistance(double lat, double lon, String distance) {
        if (distance == null || distance.isEmpty()) {
            distance = DEFAULT_DISTANCE;
        }

        //下面这些写法，是ElasticSearch 里面的写法
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //把要query对象挑出来
        queryBuilder.withFilter(new GeoDistanceQueryBuilder("geoPoint").point(lat, lon).distance(distance, DistanceUnit.KILOMETERS));

        SearchHits<Location> searchResult = elasticsearchOperations.search(queryBuilder.build(), Location.class);
        List<Long> locationIDs = new ArrayList<>();

        for (SearchHit<Location> hit : searchResult.getSearchHits()) {
            locationIDs.add(hit.getContent().getId());
        }
        return locationIDs;
    }
}


