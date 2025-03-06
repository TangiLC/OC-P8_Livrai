package com.livrai.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.livrai.entity.Delivery;
import com.livrai.service.DeliveryService;

@Controller
public class CommandController {

  @Autowired
  private DeliveryService deliveryService;

  @GetMapping("/command")
  public String showCommandPage() {
    return "command"; // Correspond à command.html (Thymeleaf)
  }

  @PostMapping("/command")
  public String createCommand(
    @SessionAttribute(name = "userId", required = false) Integer clientId,
    @RequestParam int volume,
    @RequestParam int weight
  ) {
    if (clientId == null) {
      return "redirect:/login"; // Rediriger si non connecté
    }

    Delivery delivery = new Delivery();
    delivery.setUserId(clientId);
    delivery.setVolume(volume);
    delivery.setWeight(weight);
    delivery.setStatus(Delivery.PENDING);

    deliveryService.createDelivery(delivery);
    return "redirect:/command"; // Redirection après la commande
  }
}
