package com.cjf.twitterlistmanager.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.Token;
import org.springframework.stereotype.Component;

@Component
public class User implements Comparable<User> {

  private int userId;
  private String screenName;
  private String accessToken;
  private String tokenSecret;
  private String tokenRaw;
  private JSONObject twitterData;
  private ArrayList<User> friendsList;
  private ArrayList<JSONObject> friendsListJSON;
  private JSONArray lists;

  public User() {

    this.userId = -1;
  }

  public User(int userId, String screenName, JSONObject twitterData) {

    this.setUserId(userId);
    this.setScreenName(screenName);
    this.setTwitterData(twitterData);
  }

  public User(int userId, String screenName, String accessToken, String tokenSecret, String tokenRaw) {

    this.setUserId(userId);
    this.setScreenName(screenName);
    this.setAccessToken(accessToken);
    this.setTokenSecret(tokenSecret);
    this.setTokenRaw(tokenRaw);
  }

  public int getUserId() {

    return userId;
  }

  public void setUserId(int userId) {

    this.userId = userId;
  }

  public String getScreenName() {

    return screenName;
  }

  public void setScreenName(String screenName) {

    this.screenName = screenName;
  }

  public String getAccessToken() {

    return accessToken;
  }

  public void setAccessToken(String accessToken) {

    this.accessToken = accessToken;
  }

  public void setTwitterData(JSONObject twitterData) {

    JSONObject sslData = new JSONObject(twitterData.toString().replace("http:", "https:"));
    this.twitterData = sslData;
  }

  public JSONObject getTwitterData() {

    return twitterData;
  }

  public void setTokenSecret(String tokenSecret) {

    this.tokenSecret = tokenSecret;
  }

  public String getTokenSecret() {

    return tokenSecret;
  }

  public void setTokenRaw(String tokenRaw) {

    this.tokenRaw = tokenRaw;
  }

  public String getTokenRaw() {

    return tokenRaw;
  }

  public Token getAccessTokenObj() {

    return new Token(this.getAccessToken(), this.getTokenSecret(), this.getTokenRaw());
  }

  public void setFriendsList(ArrayList<User> friendsList) {

    this.friendsList = friendsList;
    Collections.sort(this.friendsList);
  }

  public ArrayList<User> getFriendsList() {

    return friendsList;
  }

  public void setFriendsListJSON(ArrayList<JSONObject> friendsListJSON) {

    this.friendsListJSON = friendsListJSON;
    Collections.sort(this.friendsListJSON, new Comparator<JSONObject>() {

      @Override
      public int compare(JSONObject arg0, JSONObject arg1) {

        int retVal;
        try {
          retVal = arg0.getString("name").compareToIgnoreCase(arg1.getString("name"));
        } catch (JSONException e) {
          throw new RuntimeException(e);
        }
        return retVal;
      }

    });
  }

  public ArrayList<JSONObject> getFriendsListJSON() {

    return friendsListJSON;
  }

  public void setLists(JSONArray lists) {

    this.lists = lists;
  }

  public JSONArray getLists() {

    return lists;
  }

  @Override
  public int compareTo(User otherUser) {

    try {
      return this.getTwitterData().getString("name")
          .compareToIgnoreCase(otherUser.getTwitterData().getString("name"));
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean equals(Object o) {

    User other = (User) o;
    boolean retVal = other.getScreenName().equalsIgnoreCase(this.getScreenName());

    return retVal;
  }

}
