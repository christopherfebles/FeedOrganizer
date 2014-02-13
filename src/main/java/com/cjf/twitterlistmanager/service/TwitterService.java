package com.cjf.twitterlistmanager.service;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import com.cjf.twitterlistmanager.model.User;

public interface TwitterService {

  public JSONObject loadUserData(Token accessToken);

  public Token getNewRequestToken();

  public OAuthService getNewOAuthService();

  public User loadFriendsList(User user);

  public User loadLists(User user);

  public JSONArray loadListMembers(String listId, User user);

  public void addMembersToList(User user, ArrayList<String> newMembers, String listId);

  public void removeMembersFromList(User user, ArrayList<String> newMembers, String listId);
}
