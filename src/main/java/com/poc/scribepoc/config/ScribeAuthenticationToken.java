/**
 * 
 */
package com.poc.scribepoc.config;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.github.scribejava.core.model.OAuth2AccessToken;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Authenticate that represents scribe authentication with social network.
 * 
 * @author Jivko Mitrev
 */
@Getter @Setter @ToString(includeFieldNames = true)
public class ScribeAuthenticationToken extends AbstractAuthenticationToken {

  private static final long serialVersionUID = 1L;
  
  private OAuth2AccessToken token;
  private TokenType tokenType;
  private UserDetails userDetails;
  
  public ScribeAuthenticationToken(OAuth2AccessToken tokenArg, TokenType tokenTypeArg,  UserDetails userDetailsArg) {
    super(null);
    token = tokenArg;
    tokenType = tokenTypeArg;
    userDetails = userDetailsArg;
  }
  
  public ScribeAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
  }

  @Override
  public Object getCredentials() {
    return token;
  }

  @Override
  public Object getPrincipal() {
    return userDetails;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((token == null) ? 0 : token.hashCode());
    result = prime * result + ((tokenType == null) ? 0 : tokenType.hashCode());
    result = prime * result + ((userDetails == null) ? 0 : userDetails.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    ScribeAuthenticationToken other = (ScribeAuthenticationToken) obj;
    if (token == null) {
      if (other.token != null)
        return false;
    } else if (!token.equals(other.token)) {
      return false;
    }
    if (tokenType != other.tokenType)
      return false;
    if (userDetails == null) {
      if (other.userDetails != null)
        return false;
    } else if (!userDetails.equals(other.userDetails)) {
      return false;
    }
    return true;
  }

  
}
