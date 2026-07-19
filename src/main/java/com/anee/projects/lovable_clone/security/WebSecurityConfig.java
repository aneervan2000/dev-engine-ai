package com.anee.projects.lovable_clone.security;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.security.autoconfigure.actuate.web.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * <h6>Configuration class for application-wide web security settings</h6>
 *<p>
 *  This class sets up the security filter chain and password encoding mechanisms
 *  using Spring Security. It disables CSRF protection, configures stateless session
 *  management, and permits all requests to endpoints under <code>/api/**</code>.
 *</p>
 *
 * <p>
 *     <b>Key Features:</b>
 *     <ul>
 *      <li>Disables CSRF protection for stateless REST APIs.</li>
 *      <li>Configures session management to be stateless, suitable for JWT or token-based authentication.</li>
 *      <li>Permits all HTTP requests to <code>/api/**</code> endpoints.</li>
 *      <li>Provides a BCrypt password encoder bean for secure password hashing.</li>
 *     </ul>
 * </p>
 *
 */
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    /**
     * Configures the application's security filter chain.
     *
     * <p>
     * Disables CSRF protection, sets session management to stateless, and allows
     * all requests to <code>/api/**</code> endpoints without authentication.
     * </p>
     *
     * @param httpSecurity the {@link HttpSecurity} to modify
     * @return the configured {@link SecurityFilterChain}
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {

        httpSecurity
                .csrf(csrfConfig -> csrfConfig.disable())
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .dispatcherTypeMatchers(DispatcherType.ASYNC).permitAll()
                        .requestMatchers("/api/auth/**", "/webhooks/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();
    }

    /**
     * Provides the authentication manager bean.
     *
     * <p>
     * This bean is used for handling authentication processes within the application.
     * </p>
     *
     * @param authenticationConfiguration the {@link AuthenticationConfiguration} to obtain the manager from
     * @return the {@link AuthenticationManager} instance
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Provides a password encoder bean using BCrypt hashing algorithm.
     *
     * <p>
     * This bean is used for encoding and verifying user passwords securely.
     * </p>
     *
     * @return a {@link PasswordEncoder} instance using BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
