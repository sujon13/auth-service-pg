package example.demo.config;

import example.demo.service.auth.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String PREFIX = "/api/v1";


    private final JwtRequestFilter jwtRequestFilter;

//    @Bean
//    public JwtRequestFilter jwtRequestFilter(UserDetailsService userDetailsService) {
//        return new JwtRequestFilter(userDetailsService);
//    }
//
//    @Bean
//    public AuthenticationProvider customAuthenticationProvider() {
//        return new CustomAuthenticationProvider();
//    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //DaoAuthenticationProvider
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/hello", "/secure").permitAll() // Public endpoints
                        .requestMatchers(PREFIX + "/authenticate", PREFIX + "/password").permitAll() //Public
                        .requestMatchers(PREFIX + "/signup/", "/error").permitAll() // Public endpoints
                        .requestMatchers(HttpMethod.GET, PREFIX + "/signup/checkUserName").permitAll()
                        .requestMatchers(HttpMethod.POST, PREFIX + "/signup/send-otp", PREFIX + "/signup/verify-otp").permitAll()
                        .requestMatchers(HttpMethod.GET, PREFIX + "/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, PREFIX + "/users").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, PREFIX + "/users/*/assignRole", PREFIX + "/users/*/verify")
                            .hasRole("ADMIN")
                        .requestMatchers(PREFIX + "/users/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/test").hasAnyRole("USER")
                        .anyRequest().authenticated()  // Secure other endpoints
                )
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
                //.authenticationProvider(customAuthenticationProvider())
                //.httpBasic(Customizer.withDefaults());

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);  // Add JWT filter
        return http.build();
    }

//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
}
