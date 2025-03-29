package com.foodorderingsystem.FoodOrderingApplication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/api/auth/testauth").hasAnyAuthority("ADMIN", "RESTAURANT_OWNER")
                        .requestMatchers("/api/auth/**").permitAll()  // Public Endpoints
//                        Restaurants Endpoints
                        .requestMatchers(HttpMethod.GET, "/api/restaurant/**").hasAnyAuthority("ADMIN", "CUSTOMER", "RESTAURANT_OWNER")
                        .requestMatchers(HttpMethod.POST, "/api/restaurant/**").hasAnyAuthority("ADMIN", "RESTAURANT_OWNER")
//                        Cart Endpoints
                        .requestMatchers("/api/cart/**").hasAuthority("CUSTOMER")
//                        Order Endpoint
                        .requestMatchers("/api/order/**").hasRole("CUSTOMER")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
