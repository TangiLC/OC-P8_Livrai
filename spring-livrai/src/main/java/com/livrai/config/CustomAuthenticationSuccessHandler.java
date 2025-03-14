package com.livrai.config;

import java.io.IOException;

import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.livrai.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomAuthenticationSuccessHandler
  implements AuthenticationSuccessHandler {

  private final ApplicationContext applicationContext;

  // Utilisation de l'injection de constructeur au lieu de @Autowired sur un champ
  public CustomAuthenticationSuccessHandler(
    ApplicationContext applicationContext
  ) {
    this.applicationContext = applicationContext;
  }

  @Override
  public void onAuthenticationSuccess(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication
  ) throws IOException, ServletException {
    String email = authentication.getName();

    // Obtenez UserRepository directement lorsque nécessaire (lazy loading)
    UserRepository userRepository = applicationContext.getBean(
      UserRepository.class
    );

    // Récupérer l'utilisateur à partir du repository
    userRepository
      .findByEmail(email)
      .ifPresent(user -> {
        // Stocker l'ID de l'utilisateur dans la session
        request.getSession().setAttribute("user_id", user.getId());
      });

    // Rediriger vers la page par défaut
    response.sendRedirect(request.getContextPath() + "/");
  }
}
