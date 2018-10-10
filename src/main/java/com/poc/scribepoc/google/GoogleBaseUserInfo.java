/**
 * 
 */
package com.poc.scribepoc.google;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Base Google user information.
 * 
 * @author Jivko Mitrev
 */
@Getter @Setter @ToString(includeFieldNames = true)
public class GoogleBaseUserInfo {
  
  private String id;
  private String email;
  private String name;
  private boolean verifiedEmail;
  private String givenName;
  private String familyName;
  private String link;
  private String picture;
  
  public GoogleBaseUserInfo(String idArg, String nameArg, String emailArg, boolean verifiedEmailArg, 
                            String givenNameArg, String familyNameArg, String linkArg, String pictureArg) {
    id = idArg;
    email = emailArg;
    name = nameArg;
    verifiedEmail  = verifiedEmailArg;
    givenName = givenNameArg;
    familyName = familyNameArg;
    link = linkArg;
    picture = pictureArg;
  }
}
