/**
 * 
 */
package com.poc.scribepoc.google;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Response from Google with access token information
 * 
 * @author Jivko Mitrev
 */
@Getter @Setter @ToString(includeFieldNames = true)
public class GoogleAccessTokenResponse {

  private String code;
  private String state;
  private String scope;
  private String error;
}
