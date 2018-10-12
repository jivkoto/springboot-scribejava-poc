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
import org.springframework.http.HttpStatus;
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
    final OAuth20Service service = createOAuthService(secretState);

    initiated = true;
    return service.getAuthorizationUrl();
  }

  public OAuth2AccessToken handleCallback(FacebookAccessTokenResponse tokenResponseArg) 
      throws ExecutionException, InterruptedException, IOException {
		if (!initiated || secretState == null) {
			log.error("[:handleCallback] Request not initiated!");
			return null;
		}
		
		if (tokenResponseArg.getErrorCode() != null 
		    || tokenResponseArg.getErrorMessage() != null 
		    || tokenResponseArg.getCode() == null) {
		  log.error("[:handleCallback] Response error");
		  return null;
		}
		
		if (tokenResponseArg.getState() == null || !secretState.equals(tokenResponseArg.getState())) {
			log.error("Request does not match {} != {}", secretState, tokenResponseArg.getState());
			return null;
		} else {
		  final OAuth20Service service = createOAuthService(secretState);
			return service.getAccessToken(tokenResponseArg.getCode());
		}
	}
  
  public FacebookBaseUserInfo getUserInfo(OAuth2AccessToken accessTokenArg) 
      throws ExecutionException, InterruptedException, IOException {
    
    final OAuth20Service service = createOAuthService(secretState);
    final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
    service.signRequest(accessTokenArg, request);
    
    final Response response = service.execute(request);
    if (HttpStatus.OK.value() != response.getCode()) {
      log.error("[:handleCallback] response code not expected {}", response.getCode());
      return null;
    }

    log.info("[:getUserInfo] {}", response.getBody());
    
    ObjectMapper mapper = new ObjectMapper();
    TypeReference<Map<String, String>> typeRef = new TypeReference<Map<String,String>>() {};
    HashMap<String, String> valueMap = mapper.readValue(response.getBody(), typeRef);
    
    String email = valueMap.get(FacebookFieldConstants.FIELD_EMAIL);
    String id = valueMap.get(FacebookFieldConstants.FIELD_ID);
    String name = valueMap.get(FacebookFieldConstants.FIELD_NAME);
    
    return new FacebookBaseUserInfo(id, name, email);
  }
  
  private OAuth20Service createOAuthService(String secretStateArg) {
    ServiceBuilder builder = new ServiceBuilder(facebookAppConfig.getClientId())
        .apiSecret(facebookAppConfig.getClientSecret())
        .callback(facebookAppConfig.getCallback())
        .scope(FB_PERMISSION_EMAIL);
    if (secretStateArg != null) {
      builder.state(secretState);
    }
    return builder.build(FacebookApi.customVersion(facebookAppConfig.getApiVersion()));
  }
}