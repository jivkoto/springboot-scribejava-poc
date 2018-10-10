/**
 * 
 */
package com.poc.scribepoc.google;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Google application configuration
 * 
 * @author Jivko Mitrev
 */
@Component
@ConfigurationProperties("config.google")
@Getter @Setter @ToString(includeFieldNames = true)
public class GoogleAppConfig {

  private String clientId;
  private String clientSecret;
  private String callback;
}
