package com.NTG.Cridir.SecurityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
            public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http.csrf(csrf -> csrf.disable()) // disable CSRF for Postman testing
                        .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/auth/**").permitAll()   // signup/login/reset are public
                                .anyRequest().permitAll()            // everything else needs auth
                        );

                return http.build();
            }

    }


