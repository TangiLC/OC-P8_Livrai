package com.livrai.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

  /*@Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private UserService userService;*/

  @GetMapping("/login")
  public String loginPage() {
    return "login"; // Correspond à login.html dans templates
  }

  /*@PostMapping("/login")
  public String login(
    @RequestParam String email,
    @RequestParam String password,
    HttpSession session
  ) {
    // Authentification via Spring Security
    Authentication authentication = authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(email, password)
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);

    // Récupérer l'utilisateur depuis la base de données
    User user = userService
      .getUserByEmail(email)
      .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

    // Stocker l'ID de l'utilisateur en session
    session.setAttribute("userId", user.getId());

    return "redirect:/home";
  }*/

  @GetMapping("/logout")
  public String logoutPage(Model model) {
    model.addAttribute("logoutMessage", "Vous avez été déconnecté.");
    return "login";
  }
}
