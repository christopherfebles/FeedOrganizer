package com.cjf.twitterlistmanager.dao;

import com.cjf.twitterlistmanager.model.User;

public interface UserDAO {

  public User getUserById(int userId);

  public User getUserByName(String screenName);

  public void saveUser(User user);

}
