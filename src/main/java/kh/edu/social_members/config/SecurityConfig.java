package kh.edu.social_members.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    //비밀번호 암호화 처리하는 자바 기능 불러와서
    //Bean 설정하여 스프링부트 자체에서 사용자가 작성한 비밀번호를
    //암호화 처리하여 저장할 수 있도록 설정
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {//자바에서 만든 passwordencode.java 파일 가져와 비밀번호 암호화 기능사용하겠단 설정
    return new BCryptPasswordEncoder();
}

@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf-> csrf.disable())
            .authorizeHttpRequests(auth-> auth.anyRequest().permitAll())
            .formLogin((form-> form.disable())); //접속해서 비밀번호 사용 안하는 설정
    return http.build();
}



















}
