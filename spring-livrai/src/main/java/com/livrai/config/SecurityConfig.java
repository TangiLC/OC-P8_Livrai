package com.livrai.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

  @Autowired
  private CustomAuthenticationSuccessHandler successHandler;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http)
    throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .authorizeHttpRequests(auth ->
        auth
          .requestMatchers("/", "/login", "/clients", "/style.css")
          .permitAll() // Pages accessibles sans authentification
          .requestMatchers("/admin/**")
          .hasRole("ADMIN") // Accès réservé aux admins
          .anyRequest()
          .authenticated() // Toute autre requête nécessite une connexion
      )
      .formLogin(form ->
        form
          .loginPage("/login")
          .usernameParameter("email")
          .passwordParameter("password")
          .successHandler(successHandler)
          .permitAll()
      )
      .logout(logout ->
        logout
          .logoutUrl("/logout")
          .logoutSuccessUrl("/login?logout")
          .invalidateHttpSession(true)
          .deleteCookies("JSESSIONID")
          .permitAll()
      );
    return http.build();
  }

  @Bean
  public AuthenticationProvider authenticationProvider(
    UserDetailsService userDetailsService
  ) {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder());
    return provider;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    // ATTENTION: À NE PAS UTILISER EN PRODUCTION
    // Utilise les mots de passe en texte clair
    return NoOpPasswordEncoder.getInstance();
  }
}
