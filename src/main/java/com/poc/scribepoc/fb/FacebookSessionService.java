/**
 * 
 */
package com.poc.scribepoc.fb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Session service that executes facebook specific calls using scribejava library.
 * The component is session scoped as it has to survive request/response through facebook. We need to match 
 * the made from the request during the response handling time.
 * 
 * @author Jivko Mitrev
 */
@Slf4j
@Component
@SessionScope
public class FacebookSessionService {
  

  private static final String PROTECTED_RESOURCE_URL = "https://graph.facebook.com/v3.1/me?fields=id,name,email";

  private static final String FB_PERMISSION_EMAIL = "email";

  /** Flag if the request call is initiated. */
  private boolean initiated;
  
  /** CSRF guard and request/response mapping after authentication */
  private String secretState;

  private FacebookAppConfig facebookAppConfig;

  @Autowired
  public void setFacebookAppConfig(FacebookAppConfig facebookAppConfig) {
    this.facebookAppConfig = facebookAppConfig;
  }

  public String generateRequestToken() {

    secretState = "secret" + new Random().nextInt(999_999);

    final OAuth20Service service = new ServiceBuilder(facebookAppConfig.getClientId())
        .apiSecret(facebookAppConfig.getClientSecret())
        .state(secretState)
        .callback(facebookAppConfig.getCallback())
        .scope(FB_PERMISSION_EMAIL)
        .build(FacebookApi.customVersion(facebookAppConfig.getApiVersion()));

    initiated = true;
    return service.getAuthorizationUrl();
  }

  public FacebookBaseUserInfo handleCallback(FacebookAccessTokenResponse tokenResponseArg) 
      throws ExecutionException, InterruptedException, IOException {
		if (!initiated || secretState == null) {
			log.error("Request not initiated!");
			//TODO GO AWAY
			return null;
		}
		
		if (tokenResponseArg.getErrorCode() != null 
		    || tokenResponseArg.getErrorMessage() != null 
		    || tokenResponseArg.getCode() == null) {
		  log.error("Response error");
		  return null;
		}
		
    //TODO check USER cancel
		
		if (tokenResponseArg.getState() == null || !secretState.equals(tokenResponseArg.getState())) {
			log.error("Request does not match {} != {}", secretState, tokenResponseArg.getState());
			//TODO Code does not match GO AWAY
			return null;
		} else {
		    final OAuth20Service service = new ServiceBuilder(facebookAppConfig.getClientId())
		                .apiSecret(facebookAppConfig.getClientSecret())
		                .state(secretState)
		                .callback(facebookAppConfig.getCallback())
		                .scope(FB_PERMISSION_EMAIL)
		                .build(FacebookApi.customVersion(facebookAppConfig.getApiVersion()));
			final OAuth2AccessToken accessToken = service.getAccessToken(tokenResponseArg.getCode());
			final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
			service.signRequest(accessToken, request);
			
			final Response response = service.execute(request);
			log.info("{}", response.getCode());
			log.info("{}", response.getBody());
			
			ObjectMapper mapper = new ObjectMapper();
			TypeReference<Map<String, String>> typeRef = new TypeReference<Map<String,String>>() {};
			HashMap<String, String> valueMap = mapper.readValue(response.getBody(), typeRef);
			
			String email = valueMap.get("email");
			String id = valueMap.get("id");
			String name = valueMap.get("name");
			
			return new FacebookBaseUserInfo(id, name, email);
		}
	}
}