package com.laioffer.staybooking.repository;

import com.laioffer.staybooking.entity.Location;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends ElasticsearchRepository<Location, Long>, CustomLocationRepository{
    //这里extends了俩个，这样就绕过了父类继承爷爷类也要override的问题

}
