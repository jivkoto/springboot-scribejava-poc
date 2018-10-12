/**
 * 
 */
package com.poc.scribepoc.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.poc.scribepoc.google.GoogleAccessTokenResponse;
import com.poc.scribepoc.google.GoogleBaseUserInfo;
import com.poc.scribepoc.google.GoogleFieldConstants;
import com.poc.scribepoc.google.GoogleSessionService;

import lombok.extern.slf4j.Slf4j;

/**
 * Google scribe integration filter. Handles callback from Google and matches with local user details 
 * service
 * 
 * @author Jivko Mitrev
 */
@Slf4j
public class ScribeGoogleAuthenticationFilter extends ScribeAbstractAuthenticationFilter {

  public static final String DEFAULT_FILTER_PROCESS_URL = "/login/google/callback";

  private GoogleSessionService googleService;

  public ScribeGoogleAuthenticationFilter() {
    super(DEFAULT_FILTER_PROCESS_URL);
  }
  
  /**
   * @param defaultFilterProcessesUrl
   */
  public ScribeGoogleAuthenticationFilter(String defaultFilterProcessesUrl) {
    super(defaultFilterProcessesUrl);
  }

  /**
   * @param requiresAuthenticationRequestMatcher
   */
  public ScribeGoogleAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
    super(requiresAuthenticationRequestMatcher);
  }
  
  public void setGoogleSessionService(GoogleSessionService googleServiceArg) {
    googleService = googleServiceArg;
  }

  /**
   * @see org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter#attemptAuthentication(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
   */
  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
    
    log.info("[:attemptAuthentication] In filter");
    
    if (userDetailsService == null) {
      log.error("[:attemptAuthentication] No user details service to match with.");
      throw new InternalAuthenticationServiceException("No user details service to match with.");
    }
    
    String code = request.getParameter(GoogleFieldConstants.FIELD_CODE);
    String state = request.getParameter(GoogleFieldConstants.FIELD_STATE);
    String scope = request.getParameter(GoogleFieldConstants.FIELD_SCOPE);
    String error = request.getParameter(GoogleFieldConstants.FIELD_ERROR);
    
    String uri = request.getRequestURI();
    String query = request.getContextPath();
    
    log.info("Scope header:{}, {}", uri, query);
    
    GoogleAccessTokenResponse googleResponse = new GoogleAccessTokenResponse(code, state, scope, error);
    
    log.info("[:attemptAuthentication] response: {}", googleResponse);
    
    try {
      OAuth2AccessToken accessToken = googleService.handleCallback(googleResponse);
      GoogleBaseUserInfo userInfo = googleService.getUserInfo(accessToken);
      if (userInfo == null || userInfo.getEmail() == null) {
        throw new InternalAuthenticationServiceException("No user info or user email");
      }
      
      UserDetails userDetails = userDetailsService.loadUserByUsername(userInfo.getEmail());
      ScribeAuthenticationToken token = new ScribeAuthenticationToken(accessToken, TokenType.GOOGLE, userDetails);
      token.setAuthenticated(true);
      
      return token;
      
    } catch (Exception e) {
      throw new InternalAuthenticationServiceException("No user info", e);
    }
  }

}
