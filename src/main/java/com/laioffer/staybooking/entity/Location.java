package com.laioffer.staybooking.entity;

import org.elasticsearch.common.geo.GeoPoint;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;

import java.io.Serializable;

//此class用于准备elastic search， 搜索出满足范围的所有stay

@Document(indexName = "loc") //elestic search里面对应的annotation是Document
                             // index --> database
                             // Type --> table (但被淘汰了)，变成一个database只对应一个table
public class Location implements Serializable {
    private static final long serialVersionUID = 1L;

    @Field(type = FieldType.Long)
    private Long id; //其实就是stay的id

    @GeoPointField
    private GeoPoint geoPoint; // lat lon --> GeoPoint class

    public Location(Long id, GeoPoint geoPoint) {
        this.id = id;
        this.geoPoint = geoPoint;
    }

    public Long getId() {
        return id;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }
}
