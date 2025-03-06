package com.livrai.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.livrai.entity.User;
import com.livrai.service.UserService;

@Controller
public class ClientController {

  @Autowired
  private UserService userService;

  @GetMapping("/clients")
  public String getClients(Model model) {
    List<User> clients = userService.getAllNonAdminUsers();
    model.addAttribute("clients", clients);
    return "clients"; // Retourne clients.html (Thymeleaf)
  }

  @PostMapping("/clients")
  public String addClient(
    @RequestParam String email,
    @RequestParam String name,
    @RequestParam String password
  ) {
    userService.addUser(new User(null, email, name, password, false));
    return "redirect:/clients"; // Redirige vers la liste des clients après ajout
  }
}
