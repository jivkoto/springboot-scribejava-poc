/**
 * 
 */
package com.poc.scribepoc.fb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import lombok.extern.slf4j.Slf4j;

/**
 * Facebook login controller. Can generate authentication url where the user will authenticate infront of 
 * Facebook. Can handle callback from facebook where to extract the access token and obtain user details.
 * This information can be used for user match in the local DB and further the user can be logged in with that
 * information.
 * 
 * @author Jivko Mitrev
 */
@Slf4j
@RestController
public class FacebookLoginController {
	
	private FacebookSessionService fbSessionService;
	
	@Autowired
	public void setFbSessionService(FacebookSessionService fbSessionService) {
		this.fbSessionService = fbSessionService;
	}

	/**
	 * Generates facebook login url and redirects to it
	 * 
	 * @return RedirectView to facebook where to authenticate
	 */
	@GetMapping(path = "/login/facebook")
	public RedirectView loginFacebook() {
		log.info("In login");
		String autorizationUrl = fbSessionService.generateRequestToken();
		return new RedirectView(autorizationUrl);
	}
	
	/**
	 * Callback where facebook will send authentication token response (or error) for our request.
	 * 
	 * @param accessTokenResponseArg - facebook response information
	 * @return FacebookBaseUserInfo - representing the user information
	 * @throws Exception
	 */
	@GetMapping(path = "/login/facebook/callback")
	public FacebookBaseUserInfo loginFacebookCallback(FacebookAccessTokenResponse accessTokenResponseArg) 
	    throws Exception {//TODO better handling
	  
		log.info("[:loginFacebookCallback] In login callback, {}", accessTokenResponseArg);
		FacebookBaseUserInfo baseInfo = fbSessionService.handleCallback(accessTokenResponseArg);
		if (baseInfo != null) {
			log.info("[:loginFacebookCallback] {}", baseInfo);
		} else {
			log.info("[:loginFacebookCallback] null");
		}
		return baseInfo;
	}
}
