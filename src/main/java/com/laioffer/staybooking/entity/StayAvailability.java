package com.laioffer.staybooking.entity;


//stay_id is the foreign key(many) --> stay(1)，得在class里面声明一个many to one的关系
//但是stay id 藏到 StayAvailabilityKey里了
//按照hibernate默认，多对1或者多对多，建立新的表格来完成对应关系
//我们这儿用另一种方式：创建一个fk,使得stay不会创建新的column

//primary key 是需要的，因为table里往往数据比较多
//这里我们选择组合键（data + stay_id）作为PK,在StayAvaliabilityKey class里存着

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonProperty;


import javax.persistence.*;
@Entity
@Table(name = "stay_availability")
@JsonDeserialize(builder = StayAvailability.Builder.class)
public class StayAvailability {
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private StayAvailabilityKey id; //生成两col：date，stay_id

    //如果不写下面这个private field，那么fk不存在，联系不到stay这个表，所以要加ManyToOne的关系
    @ManyToOne//只写这个的话，这样写会多一个column
    @MapsId("stay_id") //写了这个就是join了column，不会生成新的col，而是用stay_id这列作为fk
    private Stay stay; //stay 也会有个stay,所以用mapsID
    // fk，但不建立新但col，而是沿用id里面的

    private StayAvailabilityState state;

    public StayAvailability() {}

    public StayAvailability(Builder builder) {
        this.id = builder.id;
        this.stay = builder.stay;
        this.state = builder.state;
    }

    public StayAvailabilityKey getId() {
        return id;
    }

    public Stay getStay() {
        return stay;
    }

    public StayAvailabilityState getState() {
        return state;
    }

    public static class Builder {
        @JsonProperty("id")
        private StayAvailabilityKey id;

        @JsonProperty("stay")
        private Stay stay;

        @JsonProperty("state")
        private StayAvailabilityState state;

        public Builder setId(StayAvailabilityKey id) {
            this.id = id;
            return this;
        }

        public Builder setStay(Stay stay) {
            this.stay = stay;
            return this;
        }

        public Builder setState(StayAvailabilityState state) {
            this.state = state;
            return this;
        }

        public StayAvailability build() {
            return new StayAvailability(this);
        }
    }

}

//json
//{
//      stay_id: 1234
//      address: xxx,st
//      desc: xxx is a hotel
//      availability: [
//              data: 12/18/2021
//              state: avaliable
//              stay:{
//                  stay_id: 1234
////                address: xxx,st
////                desc: xxx is a hotel
////                availability:[]   ---> 所以要jsonIgnore,不然死循环了
//              }
//      ] 在我们这儿，不想出现死循环，而且根本也就不想返回这个avaliability，因为我们serch的时候出来的都是available的
//}