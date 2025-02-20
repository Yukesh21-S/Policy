package com.PolicyManagement.config;

import com.PolicyManagement.service.CustomerService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final CustomerService customerService;

    public SecurityConfiguration(@Lazy CustomerService customerService) {
        this.customerService = customerService;
    }

    // Bean for password encoding
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    // Bean for authentication provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(customerService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    // Bean for security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authz -> authz
                        // Public access to registration, static resources, and login pages
                        .requestMatchers("/registration/**", "/js/**", "/css/**", "/img/**", "/home").permitAll()
                        // Role-based access control for customer and admin endpoints
                        .requestMatchers("/customer/**").hasRole("CUSTOMER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login") // Shared login processing URL
                        .permitAll()
                        .successHandler(authenticationSuccessHandler())
                )     .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")  // Shared login page for Google
                        .defaultSuccessUrl("/customer/home", true)  // Redirect after successful login
                        .userInfoEndpoint(userInfo -> userInfo.userAuthoritiesMapper(this::mapAuthorities)) // Map user roles
                        .successHandler(oAuth2AuthenticationSuccessHandler()) // Custom success handler for OAuth2
                )
                .rememberMe(rememberMe -> rememberMe
                        .key("uniqueAndSecretKey")  // Key for token generation (secure and unique)
                        .tokenValiditySeconds(1209600)  // 2 weeks (time in seconds)
                        .userDetailsService(customerService)  // Specify the UserDetailsService
                )
                .logout(logout -> logout
                        .invalidateHttpSession(true)  // Invalidate session
                        .clearAuthentication(true)  // Clear authentication
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))  // Logout URL
                        .logoutSuccessUrl("/login?logout")  // Redirect after logout
                        .permitAll()
                );

        return http.build();
    }

    private Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> grantedAuthorities) {
        List<GrantedAuthority> authorities = new ArrayList<>(grantedAuthorities);

        // Iterate through the authorities to find the OAuth2User's email
        String email = null;
        for (GrantedAuthority authority : grantedAuthorities) {
            if (authority instanceof OAuth2User oauth2User) {
                email = oauth2User.getAttribute("email");
                break;
            }
        }

        // Assign roles based on email domain
        if (email != null) {
            if (email.endsWith("@admin.com")) {
                authorities.add(new SimpleGrantedAuthority("ADMIN"));
            } else {
                authorities.add(new SimpleGrantedAuthority("CUSTOMER"));
            }
        }

        return authorities;
    }


    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler();
    }

    private static class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
        private static final Logger logger = LoggerFactory.getLogger(AuthenticationSuccessHandler.class);

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_CUSTOMER"));
            logger.info("Is admin: " + isAdmin);
            logger.info("Is admin: " + authentication.getAuthorities());

            if (isAdmin) {
                setDefaultTargetUrl("/customer/home");
            } else {
                setDefaultTargetUrl("/admin/admin-home");
            }
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

    private static class OAuth2AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
        private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

        @Override
        public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
            OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
            OAuth2User oAuth2User = oAuth2AuthenticationToken.getPrincipal();

            String email = oAuth2User.getAttribute("email");
            logger.info("OAuth2 User Email: {}", email);

            setDefaultTargetUrl("/registration");
            super.onAuthenticationSuccess(request, response, authentication);
        }
    }

    // Custom mapping for OAuth2 authorities


    @Bean
    public SavedRequestAwareAuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler();
    }
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }


}

