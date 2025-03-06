package com.livrai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

  @GetMapping("/login")
  public String loginPage() {
    return "login"; // Correspond à login.html dans templates
  }

  @GetMapping("/logout")
  public String logoutPage(Model model) {
    model.addAttribute("logoutMessage", "Vous avez été déconnecté.");
    return "login";
  }
}
