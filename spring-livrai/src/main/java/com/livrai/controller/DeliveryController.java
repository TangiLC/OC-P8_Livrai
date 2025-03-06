package com.livrai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.livrai.entity.User;
import com.livrai.service.DeliveryService;
import com.livrai.service.UserService;

@Controller
@RequestMapping("/livraisons")
public class DeliveryController {

  @Autowired
  private DeliveryService deliveryService;

  @Autowired
  private UserService userService;

  @PostMapping
  public String manageDelivery(
    @SessionAttribute(name = "userId", required = false) Integer userId,
    @RequestParam int id,
    @RequestParam String action,
    @RequestParam(required = false) Double price
  ) {
    if (userId == null) {
      return "redirect:/login"; // Rediriger si non connecté
    }

    User user = userService
      .getUserById(userId)
      .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

    if (user.isAdmin()) {
      switch (action) {
        case "ACCEPT" -> deliveryService.acceptDeliveryById(id);
        case "REJECT" -> deliveryService.rejectDeliveryById(id);
        case "BILL" -> {
            if (price != null) {
                deliveryService.billDeliveryById(id, price);
            } }
      }
    }

    return "redirect:/livraisons"; // Redirige après l'action
  }
}
