package com.cjf.twitterlistmanager.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cjf.twitterlistmanager.model.Key;
import com.cjf.twitterlistmanager.model.User;
import com.cjf.twitterlistmanager.service.LoginService;
import com.cjf.twitterlistmanager.service.TwitterService;

@Service
public class TwitterServiceImpl implements TwitterService {

  private final static String DEFAULT_CALLBACK =
      "http://www.christopherfebles.com/FeedOrganizer/twitterCallback.do";

  private OAuthService oAuthService;
  @Autowired
  private LoginService loginService;

  @Override
  public JSONObject loadUserData(Token accessToken) {

    if (oAuthService == null)
      oAuthService = this.getNewOAuthService();

    OAuthRequest oRequest =
        new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/account/verify_credentials.json");
    oAuthService.signRequest(accessToken, oRequest);
    Response sResponse = oRequest.send();

    String body = sResponse.getBody();
    JSONObject userData = null;
    try {
      userData = new JSONObject(body);
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }
    return userData;
  }

  @Override
  public Token getNewRequestToken() {

    if (oAuthService == null)
      oAuthService = this.getNewOAuthService();

    return oAuthService.getRequestToken();
  }

  @Override
  public OAuthService getNewOAuthService() {

    Key keys = loginService.getKey("Twitter");

    OAuthService service =
        new ServiceBuilder().provider(TwitterApi.SSL.class).apiKey(keys.getApiKey())
            .apiSecret(keys.getApiSecret()).callback(DEFAULT_CALLBACK).build();
    this.oAuthService = service;
    return service;
  }

  @Override
  public User loadFriendsList(User user) {

    if (oAuthService == null)
      oAuthService = this.getNewOAuthService();

    ArrayList<User> userList = new ArrayList<User>();
    ArrayList<JSONObject> JSONList = new ArrayList<JSONObject>();
    Token accessToken = user.getAccessTokenObj();
    StringBuffer friendIdList = new StringBuffer("");
    int cursor = -1;

    do {
      OAuthRequest oRequest =
          new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/friends/ids.json?cursor="
              + cursor + "&screen_name=" + user.getScreenName());
      oAuthService.signRequest(accessToken, oRequest);
      Response sResponse = oRequest.send();
      String body = sResponse.getBody();
      try {
        JSONObject friends = new JSONObject(body);
        JSONArray friendIds = friends.getJSONArray("ids");
        // if ( !friendIdList.toString().isEmpty() )
        if (!friendIdList.toString().trim().equals(""))
          friendIdList.append(",");
        friendIdList.append(friendIds.toString().replace("[", "").replace("]", ""));
        cursor = friends.getInt("next_cursor");
      } catch (JSONException e) {
        throw new RuntimeException(e);
      }
    } while (cursor != 0);

    // Lookup Twitter User Objects
    HashSet<String> nodupes =
        new HashSet<String>(Arrays.asList(friendIdList.toString().split(",")));
    ArrayList<String> friendIdArrayList = new ArrayList<String>(nodupes);

    int arrayIndex = 0;
    int numParms = 0;
    StringBuffer idParm = new StringBuffer();
    for (String id : friendIdArrayList) {
      if (numParms != 0)
        idParm.append(",");
      idParm.append(id);
      numParms++;
      arrayIndex++;
      if (numParms == 99 || arrayIndex >= friendIdArrayList.size()) {
        numParms = 0;
        String parmList = idParm.toString();
        idParm = new StringBuffer();

        OAuthRequest oRequest =
            new OAuthRequest(Verb.POST, "https://api.twitter.com/1.1/users/lookup.json?user_id="
                + parmList);
        oAuthService.signRequest(accessToken, oRequest);
        Response sResponse = oRequest.send();
        String body = sResponse.getBody();

        try {
          JSONArray friendArray = new JSONArray(body);
          for (int x = 0; x < friendArray.length(); x++) {
            JSONObject userData = friendArray.getJSONObject(x);
            User newUser =
                new User(userData.getInt("id"), userData.getString("screen_name"), userData);
            if (!userList.contains(newUser)) {
              userList.add(newUser);
              JSONList.add(userData);
            } else {
              System.err.println("***************** ERROR *****************");
              System.err.println("Skipping duplicate: " + newUser.getScreenName());
              System.err.println("Duplicate ID: " + newUser.getUserId());
              User original = userList.get(userList.indexOf(newUser));
              System.err.println("Original ID: " + original.getUserId());
              System.err.println("Original screenname: " + original.getScreenName());
            }
          }
        } catch (JSONException e) {
          throw new RuntimeException(e);
        }
      }
    }

    if (userList.size() != friendIdArrayList.size())
      throw new RuntimeException("Missing some friend accounts. Expected: "
          + friendIdArrayList.size() + " Found: " + userList.size());

    user.setFriendsList(userList);
    user.setFriendsListJSON(JSONList);

    return user;
  }

  @Override
  public User loadLists(User user) {

    if (oAuthService == null)
      oAuthService = this.getNewOAuthService();

    JSONArray listJSONArray = null;
    Token accessToken = user.getAccessTokenObj();

    OAuthRequest oRequest =
        new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/lists/list.json?screen_name="
            + user.getScreenName());
    oAuthService.signRequest(accessToken, oRequest);
    Response sResponse = oRequest.send();
    String body = sResponse.getBody();
    try {
      JSONArray tmpJSONArray = new JSONArray(body);
      // Add only Lists I don't own
      listJSONArray = new JSONArray();
      for (int x = 0; x < tmpJSONArray.length(); x++) {
        JSONObject list = tmpJSONArray.getJSONObject(x);
        if (list.getString("full_name").toLowerCase()
            .contains("@" + user.getScreenName().toLowerCase())) {
          listJSONArray.put(list);
        }
      }
    } catch (JSONException e) {
      throw new RuntimeException(e);
    }

    user.setLists(listJSONArray);

    return user;
  }

  @Override
  public JSONArray loadListMembers(String listId, User user) {

    if (oAuthService == null)
      oAuthService = this.getNewOAuthService();

    Token accessToken = user.getAccessTokenObj();
    JSONArray retVal = null;
    ArrayList<JSONObject> membersArrayList = new ArrayList<JSONObject>();
    Hashtable<String, JSONObject> uniqueMemberList = new Hashtable<String, JSONObject>();
    int cursor = -1;

    do {
      OAuthRequest oRequest =
          new OAuthRequest(Verb.GET, "https://api.twitter.com/1.1/lists/members.json?cursor="
              + cursor + "&list_id=" + listId);
      oAuthService.signRequest(accessToken, oRequest);
      Response sResponse = oRequest.send();
      String body = sResponse.getBody();
      try {
        JSONObject members = new JSONObject(body);
        JSONArray userList = members.getJSONArray("users");

        for (int x = 0; x < userList.length(); x++) {
          // membersArrayList.add(userList.getJSONObject(x));
          JSONObject member = userList.getJSONObject(x);
          uniqueMemberList.put(member.getString("id_str"), member);
        }

        cursor = members.getInt("next_cursor");
      } catch (JSONException e) {
        throw new RuntimeException(e);
      }
    } while (cursor != 0);

    membersArrayList.addAll(uniqueMemberList.values());
    Collections.sort(membersArrayList, new Comparator<JSONObject>() {

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

    retVal = new JSONArray(membersArrayList);

    return retVal;
  }

  @Override
  public void addMembersToList(User user, ArrayList<String> newMembers, String listId) {

    this.operateOnList(user, newMembers, listId,
        "https://api.twitter.com/1.1/lists/members/create_all.json");

  }

  @Override
  public void removeMembersFromList(User user, ArrayList<String> newMembers, String listId) {

    this.operateOnList(user, newMembers, listId,
        "https://api.twitter.com/1.1/lists/members/destroy_all.json");
  }

  private void operateOnList(User user, ArrayList<String> newMembers, String listId, String url) {

    if (oAuthService == null)
      oAuthService = this.getNewOAuthService();

    Token accessToken = user.getAccessTokenObj();

    // Add members to list
    int arrayIndex = 0;
    int numParms = 0;
    StringBuffer idParm = new StringBuffer();
    for (String id : newMembers) {
      if (numParms != 0)
        idParm.append(",");
      idParm.append(id);
      numParms++;
      arrayIndex++;
      if (numParms == 99 || arrayIndex >= newMembers.size()) {
        numParms = 0;
        String parmList = idParm.toString();
        idParm = new StringBuffer();

        OAuthRequest oRequest =
            new OAuthRequest(Verb.POST, url + "?list_id=" + listId + "&user_id=" + parmList);
        oAuthService.signRequest(accessToken, oRequest);
        // Response sResponse =
        oRequest.send();
        // String body = sResponse.getBody();
      }
    }
  }

}
