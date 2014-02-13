package com.cjf.twitterlistmanager.service.impl;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cjf.twitterlistmanager.dao.KeyDAO;
import com.cjf.twitterlistmanager.dao.UserDAO;
import com.cjf.twitterlistmanager.model.Key;
import com.cjf.twitterlistmanager.model.User;
import com.cjf.twitterlistmanager.service.LoginService;

@Service
public class LoginServiceImpl implements LoginService {

  @Autowired
  private UserDAO userDAO;

  @Autowired
  private KeyDAO keyDAO;

  @Override
  public User getUser(User user) {

    if (user != null && user.getUserId() != -1) {
      user = userDAO.getUserById(user.getUserId());
    } else if (user != null && user.getScreenName() != null
        && !user.getScreenName().trim().equals("")) {
      // !user.getScreenName().isEmpty() ) {
      user = userDAO.getUserByName(user.getScreenName());
    }
    return user;
  }

  @Override
  public Key getKey(String service) {

    return keyDAO.getKeyByServiceName(service);
  }

  @Override
  public User createUser(JSONObject userData, Token accessToken) {

    User newUser = null;

    try {
      newUser =
          new User(userData.getInt("id"), userData.getString("screen_name"),
              accessToken.getToken(), accessToken.getSecret(), accessToken.getRawResponse());
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }

    userDAO.saveUser(newUser);
    newUser.setTwitterData(userData);

    return newUser;
  }

}
