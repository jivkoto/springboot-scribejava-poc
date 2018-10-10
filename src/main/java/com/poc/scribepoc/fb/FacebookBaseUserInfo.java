/**
 * 
 */
package com.poc.scribepoc.fb;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Base Facebook user information.
 * 
 * @author Jivko Mitrev
 */
@Getter @Setter @ToString(includeFieldNames = true)
@NoArgsConstructor
public class FacebookBaseUserInfo {

	private String id;
	private String name;
	
	/** Email of the facebook user. If the email is not verified it will be null */
	private String email;

	public FacebookBaseUserInfo(String idArg, String nameArg, String emailArg) {
		id = idArg;
		name = nameArg;
		email = emailArg;
	}
	
}
