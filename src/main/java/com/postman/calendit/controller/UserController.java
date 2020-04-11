package com.postman.calendit.controller;

import java.util.Collections;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.postman.calendit.service.UserService;

@Controller
public class UserController {
  
  @Autowired
  UserService userService;

  @GetMapping("/user")
  @ResponseBody
  public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
    return Collections.singletonMap("name", principal.getAttribute("name"));
  }
  
  @GetMapping("verifyUser")
  public String verifyUser(@AuthenticationPrincipal OAuth2User principal, Model model) {
    String id = principal.getAttribute("email");
    String name = principal.getName();
    if(!userService.checkIfUserExists(id)) {
      model.addAttribute("registeredUser", userService.registerUser(id, name));
      System.out.println("Registered user : " + id + " " + name);
    }
    return "index";
  }
}
