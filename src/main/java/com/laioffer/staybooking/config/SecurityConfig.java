package com.laioffer.staybooking.config;

import com.laioffer.staybooking.filter.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import javax.sql.DataSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private JwtFilter jwtFilter;


    @Bean//Spring托管变成一个bean，此method返回的obj变成bean可以注入到其他class里
    public PasswordEncoder passwordEncoder() {
        //给用户密码加密
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //敞开url请求
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/register/*").permitAll()
                .antMatchers(HttpMethod.POST, "/authenticate/*").permitAll()
                //.antMatchers("/stays").permitAll() //（测试时ok）不需要先登陆，直接能操作，所以要改
                //.antMatchers("/stays/*").permitAll() // 应该先登陆 然后才能upload啊什么的,改成下面，用返回的token确认用户是否登陆
                .antMatchers("/stays").hasAuthority("ROLE_HOST")
                .antMatchers("/stays/*").hasAuthority("ROLE_HOST")
                .antMatchers("/search").hasAuthority("ROLE_GUEST")
                .antMatchers("/reservations").hasAuthority("ROLE_GUEST")
                .anyRequest().authenticated()
                .and()
                .csrf()
                .disable();

        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                //.addFilterBefore(corsFilter, jwtFilter); //不需要这行了，因为在crosFilter里面加了最高priority的annotation
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
                //加入自己写的jwtfilter，在默认filter之前，先测试是否能通过自定义的filter
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication().dataSource(dataSource)
                .passwordEncoder(passwordEncoder())
                .usersByUsernameQuery("SELECT username, password, enabled FROM user WHERE username = ?")
                .authoritiesByUsernameQuery("SELECT username, authority FROM authority WHERE username = ?");
    }

    @Override//重写是为了下面@Bean
    @Bean //Spring托管变成一个bean，此method返回的obj变成bean可以注入到其他class里
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    // 

}

