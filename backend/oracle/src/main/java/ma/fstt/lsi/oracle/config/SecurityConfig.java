package ma.fstt.lsi.oracle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SecurityConfig {
    @Bean
    public SQLInjectionPreventer sqlInjectionPreventer() {
        return new SQLInjectionPreventer();
    }
}
