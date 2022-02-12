package com.laioffer.staybooking.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

@Configuration //偏向于配置类
public class ElasticsearchConfig{
    @Value("${elasticsearch.address}") //从application.properties拿过来的
    private String elasticsearch_address;

    @Value("${elasticsearch.username}")
    private String elasticsearch_username;

    @Value("${elasticsearch.password}")
    private String elasticsearch_password;

    @Bean //变成一个bean，把此方法的返回值注入到其他使用此对象的class中用，说明这个返回值会在其他class中用到
          //如果很多class都要用到这个对象，那么每个class中用到的对象一般是共享一个，也可能每个都是新的，取决于怎么配置
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration
                = ClientConfiguration.builder()
                .connectedTo(elasticsearch_address)
                .withBasicAuth(elasticsearch_username, elasticsearch_password)
                .build();

        return RestClients.create(clientConfiguration).rest();
    }
}

