/**
 * 
 */
package com.poc.scribepoc.fb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.poc.scribepoc.config.ScribeFacebookAuthenticationFilter;

import lombok.extern.slf4j.Slf4j;

/**
 * Facebook login controller. Can generate authentication url where the user will authenticate infront of 
 * Facebook. Callback is handled by {@link ScribeFacebookAuthenticationFilter}
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
		log.info("[:loginFacebook] In login");
		String autorizationUrl = fbSessionService.generateRequestToken();
		return new RedirectView(autorizationUrl);
	}
	
}
