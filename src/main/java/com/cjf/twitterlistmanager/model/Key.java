package com.cjf.twitterlistmanager.model;

import org.springframework.stereotype.Component;

@Component
public class Key {

  private int id;
  private String service;
  private String apiKey;
  private String apiSecret;

  public Key() {

    this.id = -1;
  }

  public Key(int id, String service, String apiKey, String apiSecret) {

    this.id = id;
    this.service = service;
    this.apiKey = apiKey;
    this.apiSecret = apiSecret;
  }

  public int getId() {

    return id;
  }

  public void setId(int id) {

    this.id = id;
  }

  public String getService() {

    return service;
  }

  public void setService(String service) {

    this.service = service;
  }

  public String getApiKey() {

    return apiKey;
  }

  public void setApiKey(String apiKey) {

    this.apiKey = apiKey;
  }

  public String getApiSecret() {

    return apiSecret;
  }

  public void setApiSecret(String apiSecret) {

    this.apiSecret = apiSecret;
  }

}
