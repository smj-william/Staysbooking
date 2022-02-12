package com.laioffer.staybooking.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

//这就是存pk的地方，用组合键的模式做pk（stay_id + data）

//加上embeddable才能告诉spring把这个当作组合键pk来处理
@Embeddable
public class StayAvailabilityKey implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long stay_id;
    private LocalDate date;

    public StayAvailabilityKey() {}

    public StayAvailabilityKey(Long stay_id, LocalDate date) {
        this.stay_id = stay_id;
        this.date = date;
    }

    public Long getStay_id() {
        return stay_id;
    }

    public StayAvailabilityKey setStay_id(Long stay_id) {
        this.stay_id = stay_id;
        return this;
    }

    public LocalDate getDate() {
        return date;
    }

    public StayAvailabilityKey setDate(LocalDate date) {
        this.date = date;
        return this;
    }

    //用作pk的话，最好要实现以下两个method：equals & hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StayAvailabilityKey that = (StayAvailabilityKey) o;
        return stay_id.equals(that.stay_id) && date.equals(that.date);
    }// 可以按照不同维度和模式去判断两object是否一样
    // class应用到pk里，判断的时候是按照pk是否一样，而不是按照java自带的存储地址一样，所以要override

    @Override
    public int hashCode() {
        return Objects.hash(stay_id, date);
    }
}
