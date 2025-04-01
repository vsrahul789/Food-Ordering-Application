package com.foodorderingsystem.FoodOrderingApplication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.foodorderingsystem.FoodOrderingApplication.security.JwtAuthFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

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
                        .requestMatchers(HttpMethod.POST, "/api/restaurant").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/restaurant/{restaurantId}/menu").hasAuthority("RESTAURANT_OWNER")
//                        Cart Endpoints
                        .requestMatchers("/api/cart/**").hasAuthority("CUSTOMER")
//                        Order Endpoint
                        .requestMatchers(HttpMethod.PUT, "/api/order/**").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/order/**").hasAuthority("CUSTOMER")
                        .requestMatchers(HttpMethod.POST,"/api/order/**").hasAuthority("CUSTOMER")
                        .requestMatchers(HttpMethod.DELETE, "/api/order/**").hasAuthority("CUSTOMER")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
