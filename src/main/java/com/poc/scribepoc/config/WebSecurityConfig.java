/**
 * 
 */
package com.poc.scribepoc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.poc.scribepoc.fb.FacebookSessionService;
import com.poc.scribepoc.google.GoogleSessionService;

/**
 * Web security configuration for the app
 * 
 * @author Jivko Mitrev
 */
@Configurable
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  /** User details service used to find users by id */
  private UserDetailsService userDetailsService;
  
  private FacebookSessionService fbSessionService;
  
  private GoogleSessionService googleService;

  @Autowired
  public void setUserDetailsService(UserDetailsService userDetailsServiceArg) {
    userDetailsService = userDetailsServiceArg;
  }

  @Autowired
  public void setFbSessionService(FacebookSessionService fbSessionService) {
    this.fbSessionService = fbSessionService;
  }
  
  @Autowired
  public void setGoogleSessionService(GoogleSessionService googleServiceArg) {
    googleService = googleServiceArg;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
    .authorizeRequests()
      .antMatchers("/index.html").permitAll()
      .antMatchers("/login/facebook/**").permitAll()
      .antMatchers("/login/google/**").permitAll()
      .anyRequest().authenticated()
      .and()
    .formLogin().and()
    .logout().invalidateHttpSession(true).logoutSuccessUrl("/index.html").permitAll().and()
    .httpBasic().and()
    .csrf().disable();
    
    // add filters for facebook and google callbacks
    http.addFilterBefore(getFacebookAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    http.addFilterBefore(getGoogleAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
  }
  
  @Bean
  @Override
  public UserDetailsService userDetailsService() {
    // User details must contain users with emails that facebook or google will return to succeed the 
    // authentication
    UserDetails user = User.withDefaultPasswordEncoder()
        .username("jivkoto@gmail.com")
        .password("password")
        .roles("USER")
        .build();

    return new InMemoryUserDetailsManager(user);
  }

  public ScribeFacebookAuthenticationFilter getFacebookAuthenticationFilter() {
    ScribeFacebookAuthenticationFilter authFilter = new ScribeFacebookAuthenticationFilter();
    authFilter.setFacebookService(fbSessionService);
    authFilter.setUserDetailsService(userDetailsService);
    return authFilter;
  }
  
  public ScribeGoogleAuthenticationFilter getGoogleAuthenticationFilter() {
    ScribeGoogleAuthenticationFilter authFilter = new ScribeGoogleAuthenticationFilter();
    authFilter.setGoogleSessionService(googleService);
    authFilter.setUserDetailsService(userDetailsService);
    return authFilter;
  }

}
