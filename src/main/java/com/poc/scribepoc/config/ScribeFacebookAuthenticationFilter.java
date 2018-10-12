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
import com.poc.scribepoc.fb.FacebookAccessTokenResponse;
import com.poc.scribepoc.fb.FacebookBaseUserInfo;
import com.poc.scribepoc.fb.FacebookFieldConstants;
import com.poc.scribepoc.fb.FacebookSessionService;

import lombok.extern.slf4j.Slf4j;

/**
 * Facebook scribe integration filter. Handles callback from Facebook and matches with local user details 
 * service
 * 
 * @author Jivko Mitrev
 */
@Slf4j
public class ScribeFacebookAuthenticationFilter extends ScribeAbstractAuthenticationFilter {

  public static final String DEFAULT_FILTER_PROCESS_URL = "/login/facebook/callback";

  private FacebookSessionService facebookService;
  
  public ScribeFacebookAuthenticationFilter() {
    super(DEFAULT_FILTER_PROCESS_URL);
  }
  /**
   * @param defaultFilterProcessesUrl
   */
  public ScribeFacebookAuthenticationFilter(String defaultFilterProcessesUrl) {
    super(defaultFilterProcessesUrl);
  }

  /**
   * @param requiresAuthenticationRequestMatcher
   */
  public ScribeFacebookAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher) {
    super(requiresAuthenticationRequestMatcher);
  }
  
  public void setFacebookService(FacebookSessionService facebookServiceArg) {
    facebookService = facebookServiceArg;
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
    
    String code = request.getParameter(FacebookFieldConstants.FIELD_CODE);
    String state = request.getParameter(FacebookFieldConstants.FIELD_STATE);
    String errorMessage = request.getParameter(FacebookFieldConstants.FIELD_ERROR_MESSAGE);
    String errorCode = request.getParameter(FacebookFieldConstants.FIELD_ERROR_CODE);
    
    FacebookAccessTokenResponse fbResponse = new FacebookAccessTokenResponse(code, state, errorMessage, errorCode);
    
    log.info("[:attemptAuthentication] response: {}", fbResponse);
    try {
      OAuth2AccessToken accessToken = facebookService.handleCallback(fbResponse);
      FacebookBaseUserInfo userInfo = facebookService.getUserInfo(accessToken);
      if (userInfo == null || userInfo.getEmail() == null) {
        throw new InternalAuthenticationServiceException("No user info or user email");
      }
      
      UserDetails userDetails = userDetailsService.loadUserByUsername(userInfo.getEmail());
      ScribeAuthenticationToken token = new  ScribeAuthenticationToken(accessToken, TokenType.FACEBOOK, userDetails);
      token.setAuthenticated(true);
      
      return token;

    } catch (Exception e) {
      throw new InternalAuthenticationServiceException("No user info", e);
    }
  }
}
