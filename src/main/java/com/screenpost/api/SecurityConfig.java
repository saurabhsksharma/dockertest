package com.screenpost.api;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	DataSource dataSource;
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web
			.ignoring()
			.antMatchers(HttpMethod.OPTIONS, 
				"/auth/login", 
				"/auth/check_token",
				"/auth/authorize");
	    web
	    	.ignoring()
	    	.antMatchers(
	    		"/v2/api-docs", 
	    		"/swagger-resources/**", 
	    		"/configuration/**", 
	    		"/configuration/ui", 
	    		"/swagger-ui.html", 
	    		"/webjars/**");
	    web
	    	.ignoring()
	    	.antMatchers(
	    		"/admin/signup",
	    		"/user/signup",
	    		"/user/isuserexist",
	    		"/resetpassword",
	    		"/email/**");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors()
		.and().csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) 	
		.and().authorizeRequests()
			.antMatchers(
					"/auth/**",
					"/admin/signup",
					"/user/signup",
					"/user/isuserexist",
					"/resetpassword",
					"/email/**")
			.permitAll()
			.anyRequest().authenticated()
		.and()
	        .anonymous().disable()
	        .exceptionHandling().authenticationEntryPoint(unauthorizedEntryPoint());
	}
	
	@Bean
	public AuthenticationEntryPoint unauthorizedEntryPoint() {
	    return (request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication()
		.passwordEncoder(bCryptPasswordEncoder())
		.dataSource(dataSource)
		.usersByUsernameQuery(
			"select username, password, active from oauth_user_det where username=?")
		.authoritiesByUsernameQuery(
			"select username, role from oauth_user_det where username=?");
	}
}