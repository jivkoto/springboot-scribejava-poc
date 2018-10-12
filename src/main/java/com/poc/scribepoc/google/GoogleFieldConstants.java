package com.poc.scribepoc.google;

/**
 * Constants for Google field names
 * 
 * @author Jivko Mitrev
 */
public class GoogleFieldConstants {

  // access token request fields
  public static final String FIELD_ACCESS_TYPE = "access_type";
  public static final String FIELD_PROMPT = "prompt";
  public static final String VALUE_OFFLINE = "offline";
  public static final String VALUE_CONSENT = "consent";
      
  // access token response fields
  public static final String FIELD_CODE = "code";
  public static final String FIELD_STATE = "state";
  public static final String FIELD_ERROR = "error";
  public static final String FIELD_SCOPE = "scope";
  
  // user info response fields
  public static final String FIELD_ID = "id";
  public static final String FIELD_EMAIL = "email";
  public static final String FIELD_VERIFIED_EMAIL = "verified_email";
  public static final String FIELD_NAME = "name";
  public static final String FIELD_GIVEN_NAME = "given_name";
  public static final String FIELD_FAMILY_NAME = "family_name";
  public static final String FIELD_LINK = "link";
  public static final String FIELD_PICTURE = "picture";
  
  private GoogleFieldConstants() {}
  
}
