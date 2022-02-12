package com.laioffer.staybooking.service;

import com.laioffer.staybooking.entity.*;
import com.laioffer.staybooking.exception.StayDeleteException;
import com.laioffer.staybooking.repository.LocationRepository;
import com.laioffer.staybooking.repository.ReservationRepository;
import com.laioffer.staybooking.repository.StayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StayService {
    //一定会用到repository
    private StayRepository stayRepository;

    private ImageStorageService imageStorageService;

    private LocationRepository locationRepository;
    private GeoEncodingService geoEncodingService;
    private ReservationRepository reservationRepository;

    @Autowired
    public StayService(StayRepository stayRepository, LocationRepository locationRepository, ImageStorageService imageStorageService, GeoEncodingService geoEncodingService, ReservationRepository reservationRepository) {
        this.stayRepository = stayRepository;
        this.locationRepository = locationRepository;
        this.imageStorageService = imageStorageService;
        this.geoEncodingService = geoEncodingService;
        this.reservationRepository = reservationRepository;
    }


    public Stay findByID(Long stayId){
        //直接调用jpa
        return stayRepository.findById(stayId).orElse(null);
    }

    //public void deleteByID(Long stayId){
        //stayRepository.deleteById(stayId);
        //delete的时候 因为cascade all所以连着availability也delete了
    //}

    public void deleteByID(Long stayId) throws StayDeleteException {
        List<Reservation> reservations = reservationRepository.findByStayAndCheckoutDateAfter(new Stay.Builder().setId(stayId).build(), LocalDate.now());
        if (reservations != null && reservations.size() > 0) {
            throw new StayDeleteException("Cannot delete stay with active reservation");
        }
        stayRepository.deleteById(stayId);
    }


    //SQL 语句这么写：select * from stay where user_id = username
    public List<Stay> findByHost(String username){
        return stayRepository.findByHost(new User.Builder().setUsername(username).build());
    }

    public void add(Stay stay, MultipartFile[] images){ //传惨变了，要去改controller了
        LocalDate date = LocalDate.now().plusDays(1);

        List<StayAvailability> availabilities = new ArrayList<>();
        //set available for 30 days
        for(int i = 0; i < 30; i++){
            availabilities.add(
                    new StayAvailability.Builder()
                            .setId(new StayAvailabilityKey(stay.getId(), date))
                            .setStay(stay)
                            .setState(StayAvailabilityState.AVAILABLE)
                            .build());

            date = date.plusDays(1);
        }

        stay.setAvailabilities(availabilities);

        //image
        //并行上传
        List<String> mediaLinks = Arrays.stream(images) //把array里面每个元素拿出来
                .parallel() //并行操作
                .map(image -> imageStorageService.save(image)).collect(Collectors.toList());
                //上面这句，具体怎么操作这些数据.把所有url全部添加到一个list

        List<StayImage> stayImages = new ArrayList<>();

        for (String mediaLink : mediaLinks) {
            stayImages.add(new StayImage(mediaLink, stay));
        }
        stay.setImages(stayImages);

        //把所有stay相关的数据加到数据库（stay信息 + 图片上传到GCS后返还的url）
        stayRepository.save(stay);
        //Stay存入了，那stay availability怎么办?
        // Stay 中availabilities 定义了cascadeType.All
        // 所以 存入stay的时候 availabilities 也就更新了
        // 只需要setAvailabilities

        //save location to elasticsearch
        Location location = geoEncodingService.getLatLng(stay.getId(), stay.getAddress());
        locationRepository.save(location);

    }
}
