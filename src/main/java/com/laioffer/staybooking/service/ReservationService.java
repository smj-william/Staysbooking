package com.laioffer.staybooking.service;

import com.laioffer.staybooking.entity.Reservation;
import com.laioffer.staybooking.entity.Stay;
import com.laioffer.staybooking.entity.User;
import com.laioffer.staybooking.exception.ReservationCollisionException;
import com.laioffer.staybooking.exception.ReservationNotFoundException;
import org.springframework.stereotype.Service;
import com.laioffer.staybooking.repository.ReservationRepository;
import com.laioffer.staybooking.repository.StayAvailabilityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {
    private ReservationRepository reservationRepository; //inject 进来
    private StayAvailabilityRepository stayAvailabilityRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository, StayAvailabilityRepository stayAvailabilityRepository) {
        this.reservationRepository = reservationRepository;
        this.stayAvailabilityRepository = stayAvailabilityRepository;
    }

    public List<Reservation> listByGuest(String username) {
        return reservationRepository.findByGuest(new User.Builder().setUsername(username).build());
        //直接调用 repo里面.findByGuest
    }

    public List<Reservation> listByStay(Long stayId) {
        return reservationRepository.findByStay(new Stay.Builder().setId(stayId).build());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE) // isolation保证了一个一个执行，如果不成功就回滚
    public void add(Reservation reservation) throws ReservationCollisionException {//添加的时候要知道是否available
        //check availability of the given stay
        // if success, continue save reservation, else return collision 用throw e的方式
        List<LocalDate> dates = stayAvailabilityRepository.countByDateBetweenAndId(reservation.getStay().getId(), reservation.getCheckinDate(), reservation.getCheckoutDate().minusDays(1));

        int duration = (int) Duration.between(reservation.getCheckinDate().atStartOfDay(), reservation.getCheckoutDate().atStartOfDay()).toDays();

        if (duration != dates.size()) {
            throw new ReservationCollisionException("Duplicate reservation");
        }

        //update stay ava from ava to reserved （这个和下面那个要一起成功或者一起失败，所以要用上面@Transactional）
        stayAvailabilityRepository.reserveByDateBetweenAndId(reservation.getStay().getId(), reservation.getCheckinDate(), reservation.getCheckoutDate().minusDays(1));

        reservationRepository.save(reservation); //修改reservation
                                                //这个和上面那个要一起成功或者一起失败，所以要用上面@Transactional）

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void delete(Long reservationId) throws ReservationNotFoundException {

        //先读出是哪个reservation
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(() -> new ReservationNotFoundException("Reservation is not available"));

        stayAvailabilityRepository.cancelByDateBetweenAndId(reservation.getStay().getId(), reservation.getCheckinDate(), reservation.getCheckoutDate().minusDays(1));

        reservationRepository.deleteById(reservationId);
    }

}



