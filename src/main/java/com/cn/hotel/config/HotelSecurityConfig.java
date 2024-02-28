package com.cn.hotel.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class HotelSecurityConfig {

	@Autowired
	private  UserDetailsService userDetailsService;

	/**
	 *When implementing the "remember me" functionality in Spring Security, you generally want to avoid intercepting all requests (`anyRequest().authenticated()`), because when a user accesses the application with a remembered session (using "remember me"), they should not be required to authenticate again for every request. Instead, they should be automatically logged in based on the remembered session.
	 * By specifying `.anyRequest().authenticated()` without any exceptions, you are effectively requiring every request to be authenticated, including requests from remembered sessions. This conflicts with the purpose of the "remember me" functionality, which is to allow users to stay logged in across sessions without re-authenticating.
	 * In the configuration you provided, you're deleting the remember-me cookie during logout with `.deleteCookies("remember-me")`, which is a common practice to ensure that the remember-me token is invalidated when a user logs out. However, if you're still intercepting all requests with `.anyRequest().authenticated()`, the remember-me functionality won't work as expected because the user will be forced to re-authenticate even with a valid remember-me token.
	 * To fix this issue, you should remove `.anyRequest().authenticated()` from your configuration, so that requests from remembered sessions are not required to be authenticated. This allows the remember-me functionality to work properly, where users are automatically logged in based on the remembered session without being prompted for credentials.
	 */

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception
	{
		http.csrf().disable()
				.authorizeHttpRequests()
				.antMatchers("/user/register").permitAll()
				.antMatchers("/").authenticated()
				.and()
				.rememberMe().userDetailsService(userDetailsService)
				.and()
				.formLogin()
				.loginPage("/login")
				.permitAll()
				.and()
				.logout()
				.deleteCookies("remember-me")
				.and()
				.oauth2Login()
				.loginPage("/login");
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


}
