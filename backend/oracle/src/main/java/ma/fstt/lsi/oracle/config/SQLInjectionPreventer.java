package ma.fstt.lsi.oracle.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class SQLInjectionPreventer implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        Map<String, String[]> parameters = request.getParameterMap();
        for (String[] values : parameters.values()) {
            for (String value : values) {
                if (containsSQLInjection(value)) {
                    throw new SecurityException("Potential SQL injection detected");
                }
            }
        }
        return true;
    }

    private boolean containsSQLInjection(String value) {
        String pattern = ".*('|;|--|\\/\\*|\\*\\/|xp_|sp_).*";
        return value != null && value.matches(pattern);
    }
}