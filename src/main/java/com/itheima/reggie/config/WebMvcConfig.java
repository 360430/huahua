package com.itheima.reggie.config;

import com.itheima.reggie.common.JacksonObjectMapper;
import com.itheima.reggie.interceptor.LoginCheckFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @Author: 哈拉少本少
 * @Date: 2023/05/20/15:05
 * @Description:
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private LoginCheckFilter loginCheckFilter;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginCheckFilter).addPathPatterns("/**")
                .excludePathPatterns("/backend/**","/front/**","/employee/login","/employee/logout"
                        ,"/user/login","/user/logout","/user/sendMsg","/common/**");
    }
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

        //设置消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器 序列化和反序列化
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器追加到mvc框架的转换器集合中
        converters.add(0,messageConverter);
    }
}
