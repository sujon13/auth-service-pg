package example.demo.config;

import example.demo.service.Util;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.client.RestClient;

@Getter
@Configuration
@RequiredArgsConstructor
public class Config {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    //@Scope("request")
    public Util util() {
        return new Util();
    }
//    @Bean
//    WebClient webClient(OAuth2AuthorizedClientManager authorizedClientManager) {
//        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
//                new ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
//        return WebClient.builder()
//                .apply(oauth2Client.oauth2Configuration())
//                .build();
//    }

    @Bean
    RestClient restClient() {
        return RestClient.create();
    }

//    @Bean
//    OAuth2AuthorizedClientManager authorizedClientManager(
//            ClientRegistrationRepository clientRegistrationRepository,
//            OAuth2AuthorizedClientRepository authorizedClientRepository) {
//
//        OAuth2AuthorizedClientProvider authorizedClientProvider =
//                OAuth2AuthorizedClientProviderBuilder.builder()
//                        .authorizationCode()
//                        .refreshToken()
//                        .build();
//        DefaultOAuth2AuthorizedClientManager authorizedClientManager = new DefaultOAuth2AuthorizedClientManager(
//                clientRegistrationRepository, authorizedClientRepository);
//        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
//
//        return authorizedClientManager;
//    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        String hashedUserPassword = passwordEncoder().encode("password1");
//        var user1 = User
//                .withUsername("user1")
//                .password(hashedUserPassword)
//                .roles("USER")
//                .build();
//
//        hashedUserPassword = passwordEncoder().encode("password2");
//        var user2 = User
//                .withUsername("user2")
//                .password(hashedUserPassword)
//                .roles("USER")
//                .build();
//
//        String hashedAdminPassword = passwordEncoder().encode("admin");
//        var admin = User.withUsername("admin")
//                .password(hashedAdminPassword)
//                .roles("ADMIN")
//                .build();
//        return new InMemoryUserDetailsManager(admin, user1, user2);
//    }

}
