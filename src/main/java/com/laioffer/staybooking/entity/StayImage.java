package com.laioffer.staybooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "stay_image")
public class StayImage implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String url;

    @ManyToOne //创造一个FK指向Stay table，一个stay 多个图片
    @JoinColumn(name = "stay_id")//改名此FK
    @JsonIgnore // 不返还此信息给前端在response body里面。why？
                // 自己测试删除不加这个JsonIgonre，结果会多一个stay信息，stay里面有image，然后又有stay，嵌套死循环
    private Stay stay;

    public StayImage() {}

    public StayImage(String url, Stay stay) {
        this.url = url;
        this.stay = stay;
    }

    public String getUrl() {
        return url;
    }

    public StayImage setUrl(String url) {
        this.url = url;
        return this;
    }

    public Stay getStay() {
        return stay;
    }

    public StayImage setStay(Stay stay) {
        this.stay = stay;
        return this;
    }
}

