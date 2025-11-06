package com.securityy.data.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.securityy.data.filter.JwtAuthFilter;
import com.securityy.data.util.JwtUtil;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private long expMinutes;
    
    @Bean
    JwtUtil jwtUtil() { 
    	return new JwtUtil(secret, expMinutes); 
    	
    }


    @Bean
    SecurityFilterChain security(HttpSecurity http, JwtUtil jwt) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .headers(h -> h.frameOptions(f -> f.sameOrigin()))
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/login", "/public/**", "/h2-console/**").permitAll()
                .requestMatchers("/api/securityy/admin").hasRole("ADMIN")
                .requestMatchers("/api/securityy/user").hasAnyRole("USER","ADMIN")
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .accessDeniedHandler((req, res, e) -> res.sendError(HttpStatus.FORBIDDEN.value()))
            )
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        // Add JWT filter before UsernamePasswordAuthenticationFilter
        http.addFilterBefore(new JwtAuthFilter(jwt), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }
    @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        var cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of("http://localhost:3000","http://localhost:4200"));
        cors.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        cors.setAllowedHeaders(List.of("*"));
        cors.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }
}