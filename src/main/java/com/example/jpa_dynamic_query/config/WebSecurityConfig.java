    package com.example.jpa_dynamic_query.config;

    import com.example.jpa_dynamic_query.auth.filter.AuthTokenFilter;
    import com.example.jpa_dynamic_query.auth.handler.AuthEntryPointJwt;
    import com.example.jpa_dynamic_query.auth.detail.UserDetailsServiceImpl;
    import com.example.jpa_dynamic_query.auth.handler.CustomAccessDeniedHandler;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.http.HttpMethod;
    import org.springframework.security.authentication.AuthenticationManager;
    import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
    import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
    import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
    import org.springframework.security.config.http.SessionCreationPolicy;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
    import org.springframework.web.cors.CorsConfiguration;
    import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
    import org.springframework.web.filter.CorsFilter;

    @Configuration
    @EnableMethodSecurity
    public class WebSecurityConfig {
        private static final String[] PUBLIC_ENDPOINTS = {
                "auth/**"
        };

        @Autowired
        UserDetailsServiceImpl userDetailsService;

        @Autowired
        private AuthEntryPointJwt authEntryPointJwt;

        @Autowired
        private CustomAccessDeniedHandler customAccessDeniedHandler;

        @Bean
        public AuthTokenFilter authTokenFilter() {
            return new AuthTokenFilter();
        }

        @Bean
        public DaoAuthenticationProvider daoAuthenticationProvider() {
            DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
            daoAuthenticationProvider.setUserDetailsService(userDetailsService);
            daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

            return daoAuthenticationProvider;
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
                throws Exception {
            return authenticationConfiguration.getAuthenticationManager();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            // CORS configuration in HttpSecurity
            http.cors(cors -> cors.configurationSource(corsConfigurationSource())) // Add this line for CORS config
                    .csrf(AbstractHttpConfigurer::disable)
                    .exceptionHandling(exception -> exception.authenticationEntryPoint(authEntryPointJwt)
                            .accessDeniedHandler(customAccessDeniedHandler))
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS).permitAll()
                            .anyRequest().authenticated());

            http.authenticationProvider(daoAuthenticationProvider());
            http.addFilterBefore(authTokenFilter(), UsernamePasswordAuthenticationFilter.class);
            return http.build();
        }
    
        // CORS configuration bean
        @Bean
        public UrlBasedCorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.addAllowedOrigin("http://localhost:4200");
            corsConfiguration.addAllowedMethod("*");
            corsConfiguration.addAllowedHeader("*");
            corsConfiguration.setAllowCredentials(true);

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", corsConfiguration);

            return source;
        }
    }
