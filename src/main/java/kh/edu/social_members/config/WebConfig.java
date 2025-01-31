package kh.edu.social_members.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload-img}")
    private String uploadPath;

    //이미지를 처리할 때 1.static 폴더 아래 이미지
    //               2.업로드 폴더 위치 이미지 설정
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/static/img/");

        registry.addResourceHandler("/uploaded/**").
                addResourceLocations("file:" + uploadPath+ "/");

    }
}
