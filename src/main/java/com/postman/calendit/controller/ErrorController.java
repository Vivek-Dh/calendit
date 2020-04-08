package com.postman.calendit.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;

public class ErrorController {
  @GetMapping("/error")
  public String error(HttpServletRequest request) {
      String message = (String) request.getSession().getAttribute("error.message");
      request.getSession().removeAttribute("error.message");
      return message;
  }
}
