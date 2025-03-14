package com.livrai.controller;

import com.livrai.entity.Delivery;
import com.livrai.entity.User;
import com.livrai.service.DeliveryService;
import com.livrai.service.UserService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/livraisons")
public class DeliveryController {

  @Autowired
  private DeliveryService deliveryService;

  @Autowired
  private UserService userService;

  @GetMapping
  public String listDeliveries(
    @SessionAttribute(name = "user_id", required = false) Integer userId,
    Model model
  ) {
    // Vérification si l'utilisateur est connecté
    if (userId == null) {
      return "redirect:/login";
    }

    // Récupération de l'utilisateur
    User user = userService
      .getUserById(userId)
      .orElseThrow(() -> new RuntimeException("User not found"));

    // Récupération des livraisons selon le rôle de l'utilisateur
    List<Delivery> deliveries = user.isAdmin()
      ? deliveryService.getAllDeliveries()
      : deliveryService.getAllDeliveriesByUserId(user.getId());

    // Séparation des livraisons à venir et passées
    List<Delivery> upcomingDeliveries = new ArrayList<>();
    List<Delivery> pastDeliveries = new ArrayList<>();

    // Si admin, récupère les noms des clients
    Map<Integer, String> clientNames = new HashMap<>();
    if (user.isAdmin()) {
      List<User> clients = userService.getAllNonAdminUsers();
      for (User client : clients) {
        clientNames.put(client.getId(), client.getName());
      }
    }

    // Classification des livraisons
    for (Delivery delivery : deliveries) {
      if (
        Delivery.ACCEPTED.equals(delivery.getStatus()) ||
        Delivery.PENDING.equals(delivery.getStatus())
      ) {
        upcomingDeliveries.add(delivery);
      } else {
        pastDeliveries.add(delivery);
      }

      // Ajout du nom du client pour les admins
      if (user.isAdmin()) {
        delivery.setClientName(clientNames.get(delivery.getUserId()));
      }
    }

    // Ajout des attributs au modèle
    model.addAttribute("upcomingDeliveries", upcomingDeliveries);
    model.addAttribute("pastDeliveries", pastDeliveries);
    model.addAttribute("admin", user.isAdmin());

    return "deliveries";
  }

  @PostMapping
  public String processDeliveryAction(
    @RequestParam("id") int id,
    @RequestParam("action") String action,
    @RequestParam(value = "price", required = false) Double price,
    @SessionAttribute(name = "user_id", required = false) Integer userId
  ) {
    // Vérification si l'utilisateur est connecté
    if (userId == null) {
      return "redirect:/login";
    }

    // Vérification que l'utilisateur est admin
    User user = userService
      .getUserById(userId)
      .orElseThrow(() -> new RuntimeException("User not found"));

    if (!user.isAdmin()) {
      return "redirect:/livraisons";
    }

    // Traitement de l'action selon le type
    switch (action) {
      case "ACCEPT":
        deliveryService.acceptDeliveryById(id);
        break;
      case "REJECT":
        deliveryService.rejectDeliveryById(id);
        break;
      case "BILL":
        if (price != null) {
          deliveryService.billDeliveryById(id, price);
        }
        break;
    }

    return "redirect:/livraisons";
  }

  @GetMapping("/create")
  public String showCreateForm(
    Model model,
    @SessionAttribute(name = "user_id", required = false) Integer userId
  ) {
    // Vérification si l'utilisateur est connecté
    if (userId == null) {
      return "redirect:/login";
    }

    model.addAttribute("delivery", new Delivery());
    return "createDelivery";
  }

  @PostMapping("/create")
  public String createDelivery(
    @ModelAttribute Delivery delivery,
    @SessionAttribute(name = "user_id", required = false) Integer userId
  ) {
    // Vérification si l'utilisateur est connecté
    if (userId == null) {
      return "redirect:/login";
    }

    delivery.setUserId(userId);
    deliveryService.createDelivery(delivery);
    return "redirect:/livraisons";
  }

  @GetMapping("/{id}")
  public String viewDelivery(
    @PathVariable("id") int id,
    Model model,
    @SessionAttribute(name = "user_id", required = false) Integer userId
  ) {
    // Vérification si l'utilisateur est connecté
    if (userId == null) {
      return "redirect:/login";
    }

    User user = userService
      .getUserById(userId)
      .orElseThrow(() -> new RuntimeException("User not found"));

    Delivery delivery = deliveryService.getDeliveryById(id);

    // Vérifier que l'utilisateur a le droit de voir cette livraison
    if (
      delivery == null || (!user.isAdmin() && delivery.getUserId() != userId)
    ) {
      return "redirect:/livraisons";
    }

    // Ajouter le nom du client si admin
    if (user.isAdmin()) {
      userService
        .getUserById(delivery.getUserId())
        .ifPresent(client -> delivery.setClientName(client.getName()));
    }

    model.addAttribute("delivery", delivery);
    model.addAttribute("admin", user.isAdmin());

    return "deliveryDetails";
  }
}
