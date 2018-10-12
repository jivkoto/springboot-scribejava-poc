/**
 * 
 */
package com.poc.scribepoc.google;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.poc.scribepoc.config.ScribeGoogleAuthenticationFilter;

import lombok.extern.slf4j.Slf4j;

/**
 * Google login controller. Can generate authentication url where the user will authenticate infront of 
 * Google. Callback is handled by {@link ScribeGoogleAuthenticationFilter}
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
    log.info("request: {}", autorizationUrl);
    return new RedirectView(autorizationUrl);
  }
}


  