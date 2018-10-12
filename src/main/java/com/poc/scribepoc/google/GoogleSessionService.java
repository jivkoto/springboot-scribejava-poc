/**
 * 
 */
package com.poc.scribepoc.google;

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
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;

import lombok.extern.slf4j.Slf4j;

/**
 * Session service that executes google specific calls using scribejava library.
 * The component is session scoped as it has to survive request/response through google. We need to match 
 * the made from the request during the response handling time.
 * 
 * @author Jivko Mitrev
 */
@Slf4j
@Component
@SessionScope
public class GoogleSessionService {

  private static final String PROTECTED_RESOURCE_URL = "https://www.googleapis.com/oauth2/v2/userinfo";
  
  /** Flag if the request call is initiated. */
  private boolean initiated;
  
  /** CSRF guard and request/response mapping after authentication */
  private String secretState;
  
  private GoogleAppConfig appConfig;
  
  @Autowired
  public void setAppConfig(GoogleAppConfig appConfigArg) {
    appConfig = appConfigArg;
  }
  
  public String generateRequestToken() {
    secretState = "secret" + new Random().nextInt(999_999);
    final OAuth20Service service = createOAuthService(secretState);
    
    //pass access_type=offline to get refresh token
    final Map<String, String> additionalParams = new HashMap<>();
    additionalParams.put(GoogleFieldConstants.FIELD_ACCESS_TYPE, GoogleFieldConstants.VALUE_OFFLINE);
    //force to reget refresh token (if user are asked not the first time)
    additionalParams.put(GoogleFieldConstants.FIELD_PROMPT, GoogleFieldConstants.VALUE_CONSENT);
    
    initiated = true;
    return service.getAuthorizationUrl(additionalParams);
  }
  
  public OAuth2AccessToken handleCallback(GoogleAccessTokenResponse tokenResponseArg) 
      throws ExecutionException, InterruptedException, IOException {
    if (!initiated || secretState == null) {
      log.error("Request not initiated!");
      return null;
    }
    
    if (tokenResponseArg.getError() != null 
        || tokenResponseArg.getCode() == null) {
      log.error("Response error");
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
  
  public GoogleBaseUserInfo getUserInfo(OAuth2AccessToken accessToken) 
      throws ExecutionException, InterruptedException, IOException {
    
    final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
    final OAuth20Service service = createOAuthService(null);
    service.signRequest(accessToken, request);
    final Response response = service.execute(request);
    
    if (HttpStatus.OK.value() != response.getCode()) {
      log.error("[:handleCallback] response code not expected {}", response.getCode());
      return null;
    }

    log.info("[:getUserInfo] {}", response.getBody());
    
    ObjectMapper mapper = new ObjectMapper();
    TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String,Object>>() {};
    HashMap<String, Object> valueMap = mapper.readValue(response.getBody(), typeRef);
    
    String email = (String)valueMap.get(GoogleFieldConstants.FIELD_EMAIL);
    String id = (String)valueMap.get(GoogleFieldConstants.FIELD_ID);
    String name = (String)valueMap.get(GoogleFieldConstants.FIELD_NAME);
    boolean verifiedEmail = (boolean)valueMap.get(GoogleFieldConstants.FIELD_VERIFIED_EMAIL);
    String givenName = (String)valueMap.get(GoogleFieldConstants.FIELD_GIVEN_NAME);
    String familyName = (String)valueMap.get(GoogleFieldConstants.FIELD_FAMILY_NAME);
    String link = (String)valueMap.get(GoogleFieldConstants.FIELD_LINK);
    String picture = (String)valueMap.get(GoogleFieldConstants.FIELD_PICTURE);
    
    return new GoogleBaseUserInfo(id, name, email, verifiedEmail, givenName, familyName, link, picture);
  }
  
  private OAuth20Service createOAuthService(String secretStateArg) {
    ServiceBuilder builder = new ServiceBuilder(appConfig.getClientId())
        .apiSecret(appConfig.getClientSecret())
        .scope("email") // replace with desired scope
        .callback(appConfig.getCallback());
     if (secretStateArg != null) {
       builder.state(secretStateArg);
     }
     return builder.build(GoogleApi20.instance());
  }
  
}
