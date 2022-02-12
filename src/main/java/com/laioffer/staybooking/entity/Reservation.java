package com.laioffer.staybooking.entity;


//添加删除reservation 操作

import javax.persistence.Entity;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;


@Entity
@Table(name = "reservation")
@JsonDeserialize(builder = Reservation.Builder.class) //与json相关，
// deserialize是为了把从前端接收的json格式数据转换成对象，通过builder pattern来达成
//以上这个功能其实是jackson完成的，此功能包含在了spring boot里面，存在于spring-boot-starter-web 这个dependency里
public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) //数据库添加的时候id自动加一
    private Long id;

    @JsonProperty("checkin_date")
    private LocalDate checkinDate;

    @JsonProperty("checkout_date")
    private LocalDate checkoutDate;

    @ManyToOne //一个guest对应多个预定
    @JoinColumn(name = "user_id") // 把guest信息对应rename成user_id
    private User guest; // fk指向user，table

    @ManyToOne //一个stay对应多个预定
    @JoinColumn(name = "stay_id")
    private Stay stay; //fk对应stay table


    public Reservation() {}  //这个default constructor作用，hibernate生成对象时要用

    private Reservation(Builder builder) {
        this.id = builder.id;
        this.checkinDate = builder.checkinDate;
        this.checkoutDate = builder.checkoutDate;
        this.guest = builder.guest;
        this.stay = builder.stay;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getCheckinDate() {
        return checkinDate;
    }

    public LocalDate getCheckoutDate() {
        return checkoutDate;
    }

    public User getGuest() {
        return guest;
    }

    public Reservation setGuest(User guest) {
        this.guest = guest;
        return this;
    }

    public Stay getStay() {
        return stay;
    }


    // so you can create reservation without creating a reservation object first
    //我们想用builder 对象来创建 reservation 对象，所以要用static class
    public static class Builder {
        @JsonProperty("id") //让hibernate知道jason格式在这边对应什么
        private Long id;

        @JsonProperty("checkin_date")
        private LocalDate checkinDate;

        @JsonProperty("checkout_date")
        private LocalDate checkoutDate;

        @JsonProperty("guest")
        private User guest;

        @JsonProperty("stay")
        private Stay stay;


        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setCheckinDate(LocalDate checkinDate) {
            this.checkinDate = checkinDate;
            return this;
        }

        public Builder setCheckoutDate(LocalDate checkoutDate) {
            this.checkoutDate = checkoutDate;
            return this;
        }

        public Builder setGuest(User guest) {
            this.guest = guest;
            return this;
        }

        public Builder setStay(Stay stay) {
            this.stay = stay;
            return this;
        }

        public Reservation build() {
            return new Reservation(this);
        }
    }
}

