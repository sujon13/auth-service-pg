package example.demo.config;

import example.demo.service.auth.JwtRequestFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String PREFIX = "/api/v1";
    private final ApiKeyChecker apiKeyChecker;
    private final JwtRequestFilter jwtRequestFilter;

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/hello",
            "/secure",

            PREFIX + "/authenticate", // login
            PREFIX + "/password",

            PREFIX + "/oauth2/google/authenticate",
            PREFIX + "/oauth2/google/callback",
            PREFIX + "/oauth2/register",

            PREFIX + "/signup",
            PREFIX + "/signup/checkUserName",
            PREFIX + "/signup/send-otp",
            PREFIX + "/signup/verify-otp",

            "/error"
    );

    public static List<String> getPublicEndpoints() {
        return PUBLIC_ENDPOINTS;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //DaoAuthenticationProvider
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(PUBLIC_ENDPOINTS.toArray(new String[0])).permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()// Allow all OPTIONS requests (preflight)

                        //.requestMatchers(PREFIX + "/authenticate", PREFIX + "/password").permitAll()
                        .requestMatchers(PREFIX + "/logout").authenticated()

                        //.requestMatchers(PREFIX + "/oauth2/google/authenticate", PREFIX + "/oauth2/google/callback",
                        //        PREFIX + "/oauth2/register").permitAll()
                        //.requestMatchers(PREFIX + "/signup", "/error").permitAll()
                        //.requestMatchers(HttpMethod.GET, PREFIX + "/signup/checkUserName").permitAll()
                        //.requestMatchers(HttpMethod.POST, PREFIX + "/signup/send-otp", PREFIX + "/signup/verify-otp").permitAll()
                        .requestMatchers(HttpMethod.GET, PREFIX + "/users")
                            .access((authentication, context) ->
                                        apiKeyChecker.check(context.getRequest())
                                                ? new AuthorizationDecision(true)
                                                : new AuthorizationDecision(false)
                                )
                        .requestMatchers(HttpMethod.POST, PREFIX + "/users").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, PREFIX + "/users/*/assignRole", PREFIX + "/users/*/verify").hasRole("ADMIN")
                        .requestMatchers(PREFIX + "/users/me", PREFIX + "/users/dropdown").authenticated()
                        .requestMatchers("/test").hasAnyRole("USER")
                        .anyRequest().authenticated()  // Secure other endpoints
                )
                .sessionManagement(configurer -> configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
                //.authenticationProvider(customAuthenticationProvider())
                //.httpBasic(Customizer.withDefaults());

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);  // Add JWT filter
        return http.build();
    }

    /*@Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        //config.addAllowedOrigin("https://tasktrackerpg.netlify.app");
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://103.9.187.246:5173"));
        config.addAllowedMethod(CorsConfiguration.ALL);
        config.addAllowedHeader(CorsConfiguration.ALL);
        config.addExposedHeader("Location"); // Expose the Location header
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }*/
}
