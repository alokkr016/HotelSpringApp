package com.cn.hotel.config;

import com.cn.hotel.jwt.JwtAuthenticationFilter;
import com.cn.hotel.model.User;
import com.cn.hotel.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class HotelSecurityConfig {

    @Autowired
    JwtAuthenticationFilter filter;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserRepository userRepository;

    /**
     * When implementing the "remember me" functionality in Spring Security, you generally want to avoid intercepting all requests (`anyRequest().authenticated()`), because when a user accesses the application with a remembered session (using "remember me"), they should not be required to authenticate again for every request. Instead, they should be automatically logged in based on the remembered session.
     * By specifying `.anyRequest().authenticated()` without any exceptions, you are effectively requiring every request to be authenticated, including requests from remembered sessions. This conflicts with the purpose of the "remember me" functionality, which is to allow users to stay logged in across sessions without re-authenticating.
     * In the configuration you provided, you're deleting the remember-me cookie during logout with `.deleteCookies("remember-me")`, which is a common practice to ensure that the remember-me token is invalidated when a user logs out. However, if you're still intercepting all requests with `.anyRequest().authenticated()`, the remember-me functionality won't work as expected because the user will be forced to re-authenticate even with a valid remember-me token.
     * To fix this issue, you should remove `.anyRequest().authenticated()` from your configuration, so that requests from remembered sessions are not required to be authenticated. This allows the remember-me functionality to work properly, where users are automatically logged in based on the remembered session without being prompted for credentials.
     */

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
				.csrf().disable()
				.authorizeHttpRequests()
				.antMatchers("/user/register","/auth/login").permitAll()
				.and()
				.rememberMe().userDetailsService(userDetailsService)
				.and()
				.formLogin()
				.loginPage("/login").permitAll()
				.and()
				.logout().deleteCookies("remember-me")
				.and()
				.oauth2Login()
				.loginPage("/login")
				.userInfoEndpoint()
				.oidcUserService(this.oidcUserService());

        //For JWT

//     http
//                .csrf().disable()
//                .authorizeHttpRequests()
//                .antMatchers("/user/register","/auth/login").permitAll()
//                .anyRequest()
//                .authenticated()
//                .and()
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
//
//        http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    //	@Bean
    private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        return userRequest -> {
            OidcUserService oidcUserService = new OidcUserService();
            OidcUser oidcUser = oidcUserService.loadUser(userRequest);
            User user = userRepository.findByUsername(oidcUser.getAttribute("email")).orElseThrow(() -> new UsernameNotFoundException("User name not found" + oidcUser.getAttribute("email")));

            return new DefaultOidcUser(user.getAuthorities(), userRequest.getIdToken());
        };
    }


}
