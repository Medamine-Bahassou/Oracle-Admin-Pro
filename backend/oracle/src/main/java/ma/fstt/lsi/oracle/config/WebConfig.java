package ma.fstt.lsi.oracle.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private SQLInjectionPreventer sqlInjectionPreventer;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sqlInjectionPreventer);
    }
}