package com.se.idoctor.security;

import com.se.idoctor.security.filter.AuthenticationFilter;
import com.se.idoctor.security.filter.ExceptionHandlerFilter;
import com.se.idoctor.security.filter.JWTAuthorizationFilter;
import com.se.idoctor.security.manager.CustomAuthenticationManager;
import com.se.idoctor.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private CustomAuthenticationManager customAuthenticationManager;
    private UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(customAuthenticationManager);
        authenticationFilter.setFilterProcessesUrl("/authenticate");

        // Define CORS configuration
        http.cors().configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(List.of("http://localhost:8081", "http://localhost:5173"));
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            config.setAllowedHeaders(List.of("*"));
            config.setExposedHeaders(List.of("Authorization"));
            config.setAllowCredentials(true);
            return config;
        });

        http.headers().frameOptions().disable()
                .and()
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(requests -> requests.requestMatchers("/h2/**").permitAll()
                        .requestMatchers(HttpMethod.POST, SecurityConstants.REGISTER_PATH).permitAll()
                        .requestMatchers(HttpMethod.POST, "api/doctor/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "api/userx/verify-reset-password/*").permitAll()
                        .requestMatchers(HttpMethod.PUT, "api/userx/reset-password/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "api/email/forgot-password/*").permitAll()
                        .requestMatchers(HttpMethod.POST, "api/document/{username}/upload").permitAll()
                        .requestMatchers("reset-password/*").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "api/userx/current").authenticated()
                        .requestMatchers(HttpMethod.GET, "api/userx/all").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "api/userx/{id}").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "api/email/send").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "api/doctor/approval/").hasAuthority("ADMIN")
                        .anyRequest().authenticated()
                        .and()
                        .addFilterBefore(new ExceptionHandlerFilter(), AuthenticationFilter.class)
                        .addFilter(authenticationFilter)
                        .addFilterAfter(new JWTAuthorizationFilter(userService), AuthenticationFilter.class)
                ).sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}