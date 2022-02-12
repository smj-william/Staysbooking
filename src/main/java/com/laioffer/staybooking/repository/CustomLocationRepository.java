package com.laioffer.staybooking.repository;

import java.util.List;


public interface CustomLocationRepository {
    //要自己实现一个方法，和其他repository里面spring实现的捡现成的不一样了，ES没有支持我们想要的这个方法
    List<Long> searchByDistance(double lat, double lon, String distance);
}
