package com.laioffer.staybooking.controller;

import com.laioffer.staybooking.entity.Reservation;
import com.laioffer.staybooking.entity.Stay;
import com.laioffer.staybooking.service.ReservationService;
import com.laioffer.staybooking.service.StayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.laioffer.staybooking.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
public class StayController {
    private StayService stayService;
    private ReservationService reservationService;

    @Autowired
    public StayController(StayService stayService, ReservationService reservationService) {

        this.stayService = stayService;
        this.reservationService = reservationService;
    }

    @GetMapping(value = "/stays")
    //public List<Stay> listStays(@RequestParam(name = "host") String hostName) {
    public List<Stay> listStays(Principal principal) {
        //@RequestParam 都是针对post请求，前端参数是类似/stays?host=mj
        //用？这种方式对应返回值是一堆 比如 List，类似search到一大堆
        //return stayService.findByHost(hostName); 通过principle 方式，改为下面
        return stayService.findByHost(principal.getName());
    }

    @GetMapping(value = "/stays/{stayId}")
    public Stay getStay(@PathVariable Long stayId) {
        //@PathVariable 都是针对post请求，前端参数是类似/stays/{123}
        // 用id这种模式返回值是一个，类似get
        return stayService.findByID(stayId);
    }

//    @PostMapping("/stays")
//    public void addStay(@RequestBody Stay stay) {
//        stayService.add(stay);
//    } stay里面加入传参数image之后，就改成下面这个了,因为前端测试传惨不是原来的json格式了

    @PostMapping("/stays")
    public void addStay(
            @RequestParam("name") String name,
            @RequestParam("address") String address,
            @RequestParam("description") String description,
            //@RequestParam("host") String host,这个就不需要了，因为通过principle传入
            @RequestParam("guest_number") int guestNumber,
            @RequestParam("images") MultipartFile[] images, Principal principal) {

        Stay stay = new Stay.Builder().setName(name)
                .setAddress(address)
                .setDescription(description)
                .setGuestNumber(guestNumber)
                .setHost(new User.Builder().setUsername(principal.getName()).build())
                .build();
        stayService.add(stay, images);
    }

    @GetMapping(value = "/stays/reservations/{stayId}")
    public List<Reservation> listReservations(@PathVariable Long stayId, Principal principal) {
        return reservationService.listByStay(stayId);
    }


    @DeleteMapping("/stays/{stayId}")
    public void deleteStay(@PathVariable Long stayId) {
        stayService.deleteByID(stayId);
    }

}
