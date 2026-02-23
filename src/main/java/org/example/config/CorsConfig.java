package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Global CORS configuration.
 *
 * @CrossOrigin on individual controllers only covers successful responses.
 * Spring's error handler (4xx / 5xx) bypasses those annotations, so the
 * browser sees a "CORS error" whenever an endpoint returns an error status.
 *
 * A CorsFilter runs as a servlet filter — before Spring MVC and before the
 * error handler — so CORS headers are added to EVERY response.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allow any origin (React dev server on :5173, production build, etc.)
        config.addAllowedOriginPattern("*");

        // Allow all HTTP methods
        config.addAllowedMethod("*");

        // Allow all headers (Content-Type, Authorization, etc.)
        config.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // every path

        return new CorsFilter(source);
    }
}
