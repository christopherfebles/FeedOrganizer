package com.cjf.twitterlistmanager.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cjf.twitterlistmanager.model.User;
import com.cjf.twitterlistmanager.service.LoginService;
import com.cjf.twitterlistmanager.service.TwitterService;

@Controller
public class LoginController {

  @Autowired
  private LoginService loginService;

  @Autowired
  private TwitterService twitterService;

  @ModelAttribute("loginUser")
  public User getUser() {

    return new User();
  }

  @RequestMapping(value = "/")
  public String rootRedirect() {

    return "redirect:login.form";
  }

  @RequestMapping(value = "/login.form", method = RequestMethod.GET)
  public String loginForm() {

    return "login";
  }

  @RequestMapping(value = "/login.do", method = RequestMethod.POST)
  public void loginDo(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // Always redirect to Twitter for account security
    OAuthService service = twitterService.getNewOAuthService();
    Token requestToken = service.getRequestToken();
    String authUrl = service.getAuthorizationUrl(requestToken);

    request.getSession().setAttribute("requestToken", requestToken);
    request.getSession().setAttribute("OAuthService", service);
    response.sendRedirect(authUrl);
  }

  @RequestMapping(value = "/twitterCallback.do")
  public String twitterCallbackDo(HttpServletRequest request) throws JSONException {

    Token requestToken = (Token) request.getSession().getAttribute("requestToken");
    OAuthService service = (OAuthService) request.getSession().getAttribute("OAuthService");

    String verify = request.getParameter("oauth_verifier");

    Verifier v = new Verifier(verify);
    Token accessToken = service.getAccessToken(requestToken, v);
    JSONObject userData = twitterService.loadUserData(accessToken);

    User newUser = loginService.createUser(userData, accessToken);
    request.getSession().setAttribute("user", newUser);

    return "redirect:main.form";
  }

}
