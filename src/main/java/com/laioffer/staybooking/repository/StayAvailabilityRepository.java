package com.laioffer.staybooking.repository;

import com.laioffer.staybooking.entity.StayAvailability;
import com.laioffer.staybooking.entity.StayAvailabilityKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
@Repository
public interface StayAvailabilityRepository extends JpaRepository<StayAvailability, StayAvailabilityKey> {

    //这里用的是关系型数据库，我们用一个sql query语句
    @Query(value = "SELECT sa.id.stay_id FROM StayAvailability sa WHERE sa.id.stay_id IN ?1 AND sa.state = 0 AND sa.id.date BETWEEN ?2 AND ?3 GROUP BY sa.id.stay_id HAVING COUNT(sa.id.date) = ?4")
    List<Long> findByDateBetweenAndStateIsAvailable(List<Long> stayIds, LocalDate startDate, LocalDate endDate, long duration); //用duration过滤重复

    @Query(value = "SELECT sa.id.date FROM StayAvailability sa WHERE sa.id.stay_id = ?1 AND sa.state = 0 AND sa.id.date BETWEEN ?2 AND ?3")
    //和上面这个不一样 判断换到了java里面的reservation里面了
    List<LocalDate> countByDateBetweenAndId(Long stayId, LocalDate startDate, LocalDate endDate);

    @Modifying //
    @Query(value = "UPDATE StayAvailability sa SET sa.state = 1 WHERE sa.id.stay_id = ?1 AND sa.id.date BETWEEN ?2 AND ?3")
    void reserveByDateBetweenAndId(Long stayId, LocalDate startDate, LocalDate endDate);
    // ava --> reserve

    @Modifying
    @Query(value = "UPDATE StayAvailability sa SET sa.state = 0 WHERE sa.id.stay_id = ?1 AND sa.id.date BETWEEN ?2 AND ?3")
    void cancelByDateBetweenAndId(Long stayId, LocalDate startDate, LocalDate endDate);
    // reserve --> ava
}

//sql语句： 搜索就是select

//SELECT stay_id FROM StayAvailability WHERE stay_id in stayIds AND state=0 AND id.date BETWEEN startDate AND endDate

//这么写的话返回的类似 【1，1，1，1，2，2，2，2，3，3，3，3，4，4，4，4】因为返回来满足各个条件的id，在table里面有很多行重复
// 可以 -> map<stay_id, count> 对应count是否和duration一样
//在sql里面就是用group by 然后count
