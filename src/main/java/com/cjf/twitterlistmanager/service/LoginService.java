package com.cjf.twitterlistmanager.service;

import org.json.JSONObject;
import org.scribe.model.Token;

import com.cjf.twitterlistmanager.model.Key;
import com.cjf.twitterlistmanager.model.User;

public interface LoginService {

  public User createUser(JSONObject userData, Token accessToken);

  public User getUser(User user);

  public Key getKey(String service);

}
