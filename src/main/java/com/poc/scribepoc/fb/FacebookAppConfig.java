/**
 * 
 */
package com.poc.scribepoc.fb;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * Facebook application configuration
 * 
 * @author Jivko Mitrev
 */
@Component
@ConfigurationProperties("config.facebook")
@Getter @Setter
public class FacebookAppConfig {

    private String clientId;
    private String clientSecret;
    private String callback;
    private String apiVersion;
}
