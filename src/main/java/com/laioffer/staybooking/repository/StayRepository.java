package com.laioffer.staybooking.repository;

import com.laioffer.staybooking.entity.Stay;
import com.laioffer.staybooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StayRepository extends JpaRepository<Stay, Long> {
    //我们可以用jpa里面naming convention 来
    List<Stay> findByHost(User user);
    // jap会自己帮你生成用这些方法的class
    //Ex:
    //List<Stay> findByAddress(String address);

    //List<Stay> findByDescription(String Descp);

    //这个方法不一定要实现，如果符合命名要求，jap会自己帮你实现
    List<Stay> findByIdInAndGuestNumberGreaterThanEqual(List<Long> ids, int guestNumber);
    //用in这个关键词区分是在一个list里面来find

    //Stay findByIdAndGuestNumberGreaterThanEqual(Long ids, int guestNumber);


}

//可以自己写
//public class StayRepositoryImpl implements StayRepository {//@override}

