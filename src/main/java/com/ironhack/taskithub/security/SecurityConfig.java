package com.ironhack.taskithub.security;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ironhack.taskithub.security.filters.CustomAuthenticationFilter;
import com.ironhack.taskithub.security.filters.CustomAuthorizationFilter;
import com.ironhack.taskithub.service.UserService;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

/**
 * This is the main configuration class for security in the application. It
 * enables web security,
 * sets up the password encoder, and sets up the security filter chain.
 */
@Configuration
@EnableWebSecurity // indicates it is a security config class using spring web security
@RequiredArgsConstructor
public class SecurityConfig {

    // Remove direct autowiring of UserService
    // @Autowired
    // private final UserService userService;

    // Autowired instance of the AuthenticationManagerBuilder (provided by Spring Security)
    @Autowired
    private final AuthenticationManagerBuilder authManagerBuilder;

    /**
     * Bean definition for PasswordEncoder
     *
     * @return an instance of the DelegatingPasswordEncoder
     */
    @Bean
    public PasswordEncoder encoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // Add a method to supply UserDetailsService
    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return userService;
    }

    /**
     * Bean definition for AuthenticationManager
     *
     * @param authenticationConfiguration the instance of
     *                                    AuthenticationConfiguration
     * @return an instance of the AuthenticationManager
     * @throws Exception if there is an issue getting the instance of the
     *                   AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Configure the AuthenticationManagerBuilder with the UserService and
     * PasswordEncoder
     *
     * @param authManagerBuilder the instance of AuthenticationManagerBuilder
     * @throws Exception if there is an issue configuring the AuthenticationManagerBuilder
     */
    @Autowired
    public void configure(AuthenticationManagerBuilder authManagerBuilder, UserService userService) throws Exception {
        authManagerBuilder.userDetailsService(userDetailsService(userService)).passwordEncoder(encoder());
    }
    /**
     * Bean definition for SecurityFilterChain
     *
     * @param http the instance of HttpSecurity
     * @return an instance of the SecurityFilterChain
     * @throws Exception if there is an issue building the SecurityFilterChain
     */
    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // CustomAuthenticationFilter instance created
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(
                authManagerBuilder.getOrBuild());

        // set the URL that the filter should process
        customAuthenticationFilter.setFilterProcessesUrl("/api/login");

        // disable CSRF protection because we are using tokens, not session
        http.csrf().disable(); // NOTE: "csrf" marked for removal/deprecation

        // set the session creation policy to stateless, to not maintain sessions in the
        // server
        http.sessionManagement().sessionCreationPolicy(STATELESS); // NOTE: "sessionManagement" marked for removal/deprecation

        // set up authorization for different request matchers and user roles
        http.authorizeHttpRequests((requests) -> requests

                .requestMatchers(HttpMethod.POST, "/users").hasAnyAuthority("ADMIN")
                .requestMatchers(HttpMethod.GET, "/users/username/{username}").hasAnyAuthority("ADMIN", "USER")
                .requestMatchers(HttpMethod.GET, "/users/{id}").hasAnyAuthority("ADMIN", "USER")
                .requestMatchers(HttpMethod.GET, "/users").hasAnyAuthority("ADMIN", "USER")
                .requestMatchers(HttpMethod.PUT, "/users/{id}").hasAnyAuthority("ADMIN", "USER")
                .requestMatchers(HttpMethod.DELETE, "/users/{id}").hasAnyAuthority("ADMIN")

                .requestMatchers(HttpMethod.GET, "/departments/**").hasAnyAuthority("ADMIN")
                .requestMatchers(HttpMethod.GET, "/departments").hasAnyAuthority("ADMIN")
                .requestMatchers(HttpMethod.POST, "/departments").hasAnyAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/departments/{id}").hasAnyAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/departments/{id}").hasAnyAuthority("ADMIN")

                .requestMatchers(HttpMethod.GET, "/tasks/**").hasAnyAuthority("ADMIN", "USER")
                .requestMatchers(HttpMethod.POST, "/tasks").hasAnyAuthority("ADMIN", "USER")
                .requestMatchers(HttpMethod.PUT, "/tasks/{id}").hasAnyAuthority("ADMIN", "USER")
                .requestMatchers(HttpMethod.DELETE, "/tasks/{id}").hasAnyAuthority("ADMIN", "USER")

                .anyRequest().permitAll()); // any other endpoints DON'T require authentication

        // add the custom authentication filter to the http security object
        http.addFilter(customAuthenticationFilter);

        // Add the custom authorization filter before the standard authentication
        // filter.
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        // Build the security filter chain to be returned.
        return http.build();
    }
}
