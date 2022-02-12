package com.laioffer.staybooking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

//Foreign key 是host，一个host --> N stays
@Entity
@Table(name = "Stay")
@JsonDeserialize(builder = Stay.Builder.class)
public class Stay implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;
    private String address;

    @JsonProperty("guest_number")//guestNumber --> 前端json格式都时候，名字叫guest_number
    private int guestNumber;

    @ManyToOne
    @JoinColumn(name = "user_id")//改名把host改成user_id
    private User host;

    //MySQL 是relational数据库（ACID）,
    // A原子性，要么都成功要么失败；
    // C一致性，stay里有，那stay availability里面也要有
    //cascade是集联操作，在增删改查时保持数据一致性
    // fetch,当去stay里面读数据的时候，StayAvailability里面也有相关信息，eager就是都读，lazy就是不返回StayAvailability相关信息
    // mappedBy:一个stay对应多个StayAvailability，用mappedBy传参告诉hibernate具体定义去对应哪个
    @JsonIgnore //StayAvailability 不返回给前端显示，只返回上面所有
    @OneToMany(mappedBy = "stay", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StayAvailability> availabilities;

    //mappedBy那边对应的private field是stay
    // fetch - lazy: 每次读取stay信息是，其image的信息也会全部读取出来
    @OneToMany(mappedBy = "stay", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<StayImage> images;

    public Stay() {}

    private Stay(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.address = builder.address;
        this.guestNumber = builder.guestNumber;
        this.host = builder.host;
        this.availabilities = builder.availabilities;
        this.images = builder.images;
    }

    public List<StayImage> getImages() {
        return images;
    }

    public Stay setImages(List<StayImage> images) {
        this.images = images;
        return this;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public int getGuestNumber() {
        return guestNumber;
    }

    public User getHost() {
        return host;
    }

    public List<StayAvailability> getAvailabilities() {
        return availabilities;
    }

    public Stay setAvailabilities(List<StayAvailability> availabilities) {
        this.availabilities = availabilities;
        return this;
    }

    public static class Builder {

        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("description")
        private String description;

        @JsonProperty("address")
        private String address;

        @JsonProperty("guest_number")
        private int guestNumber;

        @JsonProperty("host")
        private User host;

        @JsonProperty("availabilities")
        private List<StayAvailability> availabilities;

        @JsonProperty("images")
        private List<StayImage> images;


        public Builder setImages(List<StayImage> images) {
            this.images = images;
            return this;
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setGuestNumber(int guestNumber) {
            this.guestNumber = guestNumber;
            return this;
        }

        public Builder setHost(User host) {
            this.host = host;
            return this;
        }

        public Builder setAvailabilities(List<StayAvailability> availabilities) {
            this.availabilities = availabilities;
            return this;
        }

        public Stay build() {
            return new Stay(this);
        }
    }

}
