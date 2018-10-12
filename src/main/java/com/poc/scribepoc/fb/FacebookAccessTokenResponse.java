/**
 * 
 */
package com.poc.scribepoc.fb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Response from Facebook with access token information
 * 
 * @author Jivko Mitrev
 */
@Getter @Setter @ToString(callSuper = true, includeFieldNames = true)
@NoArgsConstructor
public class FacebookAccessTokenResponse {

	private String code;
	private String state;
	
	private String error_code;
	
	private String error_message;
	
  public FacebookAccessTokenResponse(String codeArg, String stateArg, String errorCodeArg, String errorMessageArg) {
    super();
    this.code = codeArg;
    this.state = stateArg;
    this.error_code = errorCodeArg;
    this.error_message = errorMessageArg;
  }
	
	public String getErrorCode() {
	  return error_code;
	}
	
	public String getErrorMessage() {
	  return error_message;
	}

}

