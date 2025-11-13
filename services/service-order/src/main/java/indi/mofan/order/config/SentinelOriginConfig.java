package indi.mofan.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.csp.sentinel.adapter.spring.webmvc_v6x.callback.RequestOriginParser;

@Configuration
public class SentinelOriginConfig {

    @Bean
    public RequestOriginParser requestOriginParser() {
        return request -> {
            // 从查询参数或请求头中解析用户类型作为请求来源（origin）
            String userType = request.getParameter("userType");
            if (userType == null || userType.isEmpty()) {
                userType = request.getHeader("X-User-Type");
            }
            return (userType == null || userType.isEmpty()) ? "anonymous" : userType;
        };
    }
}