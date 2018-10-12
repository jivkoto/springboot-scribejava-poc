/**
 * 
 */
package com.poc.scribepoc.config;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * Base filter for Scribe social network integration
 * 
 * @author Jivko Mitrev
 */
public abstract class ScribeAbstractAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  protected UserDetailsService userDetailsService;
  
  /**
   * @param defaultFilterProcessesUrl
   */
  public ScribeAbstractAuthenticationFilter(String defaultFilterProcessesUrl) {
    super(defaultFilterProcessesUrl);
  }

  /**
   * @param requiresAuthenticationRequestMatcher
   */
  public ScribeAbstractAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
    super(requiresAuthenticationRequestMatcher);
  }

  public UserDetailsService getUserDetailsService() {
    return userDetailsService;
  }

  public void setUserDetailsService(UserDetailsService userDetailsService) {
    this.userDetailsService = userDetailsService;
  }
  
}
