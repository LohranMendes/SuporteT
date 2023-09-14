package com.gruposuporte.projetosuporte.security;

import com.gruposuporte.projetosuporte.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@EnableMethodSecurity
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;

    @Autowired
    public SecurityConfig(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector) {
        return new MvcRequestMatcher.Builder(introspector);
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(
                                    mvc.pattern(HttpMethod.GET, "/"),
                                    mvc.pattern(HttpMethod.GET, "/css/**"),
                                    mvc.pattern(HttpMethod.GET, "/img/**"),
                                    mvc.pattern(HttpMethod.GET, "/scss/**"),
                                    mvc.pattern(HttpMethod.GET, "/vendor/**"),
                                    mvc.pattern(HttpMethod.GET, "/js/**"),
                                    mvc.pattern(HttpMethod.GET, "/register"),
                                    mvc.pattern(HttpMethod.GET, "/login"),
                                    mvc.pattern(HttpMethod.GET, "/home"),
                                    mvc.pattern(HttpMethod.POST, "/register-user"),
                                    mvc.pattern(HttpMethod.GET, "/register-agent"),
                                    mvc.pattern(HttpMethod.POST, "/register-agent")
                            ).permitAll()
                            .anyRequest()
                            .authenticated();
                }).formLogin(form -> {
                    form.loginPage("/login")
                            .defaultSuccessUrl("/", true)
                            .permitAll();
                });
        return http.build();
    }

}
