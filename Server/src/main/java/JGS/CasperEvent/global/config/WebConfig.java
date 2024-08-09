package JGS.CasperEvent.global.config;

import JGS.CasperEvent.domain.event.service.adminService.AdminService;
import JGS.CasperEvent.global.jwt.filter.JwtAuthorizationFilter;
import JGS.CasperEvent.global.jwt.filter.JwtUserFilter;
import JGS.CasperEvent.global.jwt.filter.VerifyAdminFilter;
import JGS.CasperEvent.global.jwt.filter.VerifyUserFilter;
import JGS.CasperEvent.global.jwt.service.UserService;
import JGS.CasperEvent.global.jwt.util.JwtProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer {

//    @Override
//    public void addCorsMappings(CorsRegistry registration) {
//        registration.addMapping("/**")
//                .allowCredentials(true)
//                .allowedOrigins("http://localhost:5173", "https://d3phfzvzx3wm4l.cloudfront.net/")
//                .allowedMethods("GET", "POST", "PUT", "DELETE")
//                .allowedHeaders("*");
//    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean() {
        FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CorsFilter(corsConfigurationSource()));
        registrationBean.setOrder(0);
        return registrationBean;
    }

    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("https://d3phfzvzx3wm4l.cloudfront.net/");
        config.addAllowedOrigin("https://hybrid-jgs.shop");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public FilterRegistrationBean verifyUserFilter(ObjectMapper mapper, UserService userService) {
        FilterRegistrationBean<Filter> filterRegistrationBean =
                new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new VerifyUserFilter(mapper, userService));
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.addUrlPatterns("/event/auth");
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean jwtFilter(JwtProvider provider, ObjectMapper mapper) {
        FilterRegistrationBean<Filter> filterRegistrationBean = new
                FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new JwtUserFilter(provider, mapper));
        filterRegistrationBean.setOrder(2);
        filterRegistrationBean.addUrlPatterns("/event/auth");
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean verifyAdminFilter(ObjectMapper mapper, AdminService adminService) {

        FilterRegistrationBean<Filter> filterRegistrationBean = new
                FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new VerifyAdminFilter(mapper, adminService));
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.addUrlPatterns("/admin/auth");
        return filterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean jwtAdminFilter(JwtProvider provider, ObjectMapper mapper) {
        FilterRegistrationBean<Filter> filterRegistrationBean = new
                FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new JwtUserFilter(provider, mapper));
        filterRegistrationBean.setOrder(2);
        filterRegistrationBean.addUrlPatterns("/admin/auth");
        return filterRegistrationBean;
    }
    @Bean
    public FilterRegistrationBean jwtAuthorizationFilter(JwtProvider provider, ObjectMapper mapper) {
        FilterRegistrationBean<Filter> filterRegistrationBean = new
                FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new JwtAuthorizationFilter(provider, mapper));
        filterRegistrationBean.setOrder(3);
        return filterRegistrationBean;
    }


}
