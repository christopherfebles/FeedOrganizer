package com.cjf.twitterlistmanager.dao;

import com.cjf.twitterlistmanager.model.Key;

public interface KeyDAO {

  public Key getKeyById(int id);

  public Key getKeyByServiceName(String service);

}
