/**
 * 
 */
package com.poc.scribepoc.google;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import lombok.extern.slf4j.Slf4j;

/**
 * Google login controller. Can generate authentication url where the user will authenticate infront of 
 * Google. Can handle callback from google where to extract the access token and obtain user details.
 * This information can be used for user match in the local DB and further the user can be logged in with that
 * information.
 * 
 * @author Jivko Mitrev
 */
@Slf4j
@RestController
public class GoogleLoginController {
  
  private GoogleSessionService googleSessionService;
  
  @Autowired
  public void setGoogleSessionService(GoogleSessionService googleSessionServiceArg) {
    googleSessionService = googleSessionServiceArg;
  }
  
  /**
   * Generates google login url and redirects to it
   * 
   * @return RedirectView to google where to authenticate
   */
  @GetMapping(path = "/login/google")
  public RedirectView loginGoogle() {
    log.info("[:loginGoogle] In");
    String autorizationUrl = googleSessionService.generateRequestToken();
    return new RedirectView(autorizationUrl);
  }
  
  /**
   * Callback where google will send authentication token response (or error) for our request.
   * 
   * @param accessTokenResponseArg - google response information
   * @return GoogleBaseUserInfo - representing the user information
   * @throws Exception
   */
  @GetMapping(path = "/login/google/callback")
  public GoogleBaseUserInfo loginGoogleCallback(GoogleAccessTokenResponse accessTokenResponseArg)
      throws Exception {//TODO better handling
    log.info("[:loginFacebookCallback] In login callback, {}", accessTokenResponseArg);
    GoogleBaseUserInfo baseInfo = googleSessionService.handleCallback(accessTokenResponseArg);
    if (baseInfo != null) {
      log.info("{}", baseInfo);
    } else {
      log.info("null");
    }
    return baseInfo;
  }
  
}


  