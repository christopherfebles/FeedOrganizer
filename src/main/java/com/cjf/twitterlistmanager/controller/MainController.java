package com.cjf.twitterlistmanager.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cjf.twitterlistmanager.model.User;
import com.cjf.twitterlistmanager.service.LoginService;
import com.cjf.twitterlistmanager.service.TwitterService;

@Controller
public class MainController {

  @Autowired
  private TwitterService twitterService;

  @Autowired
  private LoginService loginService;

  @RequestMapping(value = "/main.form", method = RequestMethod.GET)
  public String mainForm(HttpServletRequest request, Model model) {

    User user = (User) request.getSession().getAttribute("user");

    if (user == null) {
      return "redirect:login.form";
    }

    user = twitterService.loadFriendsList(user);
    user = twitterService.loadLists(user);

    request.getSession().setAttribute("user", user);

    String listId = (String) request.getSession().getAttribute("lastList");
    if (listId == null)
      listId = "-1";

    model.addAttribute("listId", listId);

    return "main";
  }

  @RequestMapping(value = "/main.do")
  public String mainDo(@RequestParam(value = "idList") String idList, @RequestParam(
      value = "listDropdown") String listId,
      @RequestParam(value = "logout", required = false) String logout, HttpServletRequest request)
      throws JSONException {

    if (logout != null) {
      request.getSession().invalidate();
      return "redirect:login.form";
    }

    User user = (User) request.getSession().getAttribute("user");

    // Trim idList
    idList = idList.substring(1);
    ArrayList<String> finalList = new ArrayList<String>(Arrays.asList(idList.split(",")));

    JSONArray listMembers = twitterService.loadListMembers(listId, user);
    ArrayList<String> oldList = new ArrayList<String>();
    for (int x = 0; x < listMembers.length(); x++) {
      oldList.add(listMembers.getJSONObject(x).getString("id_str"));
    }

    ArrayList<String> addMembers = new ArrayList<String>();
    ArrayList<String> removeMembers = new ArrayList<String>();

    for (String oldMember : oldList) {
      if (!finalList.contains(oldMember)) {
        removeMembers.add(oldMember);
      }
    }

    for (String newMember : finalList) {
      if (!oldList.contains(newMember)) {
        addMembers.add(newMember);
      }
    }

    twitterService.addMembersToList(user, addMembers, listId);
    twitterService.removeMembersFromList(user, removeMembers, listId);

    request.getSession().setAttribute("lastList", listId);

    return "redirect:/main.form";
  }

  @RequestMapping(value = "/getFriendsList.do")
  public void getFriendsListDo(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    User user = (User) request.getSession().getAttribute("user");

    String JSONFriendsList = (new JSONArray(user.getFriendsListJSON())).toString();

    response.setContentType("application/json");
    response.getWriter().write(JSONFriendsList);
  }

  @RequestMapping(value = "/getSubscribedLists.do")
  public void getSubscribedListsDo(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    User user = (User) request.getSession().getAttribute("user");

    String subscribedLists = user.getLists().toString();

    response.setContentType("application/json");
    response.getWriter().write(subscribedLists);

  }

  @RequestMapping(value = "/getListMembers.do")
  public void getListMembersDo(@RequestParam("listId") String listId, HttpServletRequest request,
      HttpServletResponse response) throws IOException {

    User user = (User) request.getSession().getAttribute("user");

    JSONArray listMembers = twitterService.loadListMembers(listId, user);
    String listMembersStr = listMembers.toString();

    response.setContentType("application/json");
    response.getWriter().write(listMembersStr);
  }

  @RequestMapping(value = "/getUserData.do")
  public void getUserDataDo(HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    User user = (User) request.getSession().getAttribute("user");

    response.setContentType("application/json");
    response.getWriter().write(user.getTwitterData().toString());
  }
}
