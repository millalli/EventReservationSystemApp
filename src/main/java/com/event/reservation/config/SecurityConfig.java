package com.event.reservation.config;

import com.event.reservation.view.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // 1. Configure Public Resources
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/line-awesome/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/events")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/register")).permitAll()
        );

        // 2. H2 Console Fixes
        http.csrf(csrf -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")));
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        // 3. Let Vaadin configure its defaults
        super.configure(http);

        // 4. Set Login View
        setLoginView(http, LoginView.class);

        // 5. THE FIX: Logic to decide where to go after login
        http.formLogin(form -> form
                .successHandler((request, response, authentication) -> {
                    // Check if the user has the ORGANIZER role
                    boolean isOrganizer = authentication.getAuthorities().stream()
                            .anyMatch(r -> r.getAuthority().equals("ROLE_ORGANIZER"));

                    // Check if the user has the ADMIN role
                    boolean isAdmin = authentication.getAuthorities().stream()
                            .anyMatch(r -> r.getAuthority().equals("ROLE_ADMIN"));

                    if (isOrganizer) {
                        response.sendRedirect("/organizer/dashboard");
                    } else if (isAdmin) {
                        response.sendRedirect("/admin/dashboard");
                    } else {
                        response.sendRedirect("/dashboard"); // Default for Clients
                    }
                })
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}