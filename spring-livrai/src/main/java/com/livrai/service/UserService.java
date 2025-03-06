package com.livrai.service;

import com.livrai.entity.User;
import com.livrai.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  // Méthode requise par l'interface UserDetailsService
  @Override
  public UserDetails loadUserByUsername(String email)
    throws UsernameNotFoundException {
    Optional<User> userOpt = userRepository.findByEmail(email);
    User user = userOpt.orElseThrow(() ->
      new UsernameNotFoundException(
        "Utilisateur non trouvé avec l'email: " + email
      )
    );

    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
    authorities.add(
      new SimpleGrantedAuthority(user.isAdmin() ? "ROLE_ADMIN" : "ROLE_USER")
    );

    return new org.springframework.security.core.userdetails.User(
      user.getEmail(),
      user.getPassword(),
      authorities
    );
  }

  public User addUser(User user) {
    // Encode le mot de passe avant de sauvegarder
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  public Optional<User> getUserById(int id) {
    return userRepository.findById(id);
  }

  public Optional<User> getUserByEmail(String email) {
    return userRepository.findByEmail(email);
  }

  public List<User> getAllNonAdminUsers() {
    return userRepository.findByIsAdminFalse();
  }

  public User updateUser(User user) {
    // Vérifiez si le mot de passe a été modifié avant de l'encoder à nouveau
    Optional<User> existingUser = userRepository.findById(user.getId());
    if (
      existingUser.isPresent() &&
      (
        user.getPassword() == null ||
        user.getPassword().isEmpty() ||
        user.getPassword().equals(existingUser.get().getPassword())
      )
    ) {
      // Conserve l'ancien mot de passe s'il n'a pas été modifié
      user.setPassword(existingUser.get().getPassword());
    } else {
      // Encode le nouveau mot de passe
      user.setPassword(passwordEncoder.encode(user.getPassword()));
    }
    return userRepository.save(user);
  }

  // Méthode utilitaire pour vérifier si un mot de passe en clair correspond au mot de passe encodé d'un utilisateur
  public boolean checkPassword(User user, String rawPassword) {
    return passwordEncoder.matches(rawPassword, user.getPassword());
  }
}
