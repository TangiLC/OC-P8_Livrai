package com.livrai.controller;


import com.livrai.entity.Delivery;
import com.livrai.entity.User;
import com.livrai.service.DeliveryService;
import com.livrai.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/deliveries")
public class DeliveriesController {

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String getDeliveries(@SessionAttribute(name = "userId", required = false) Integer userId, Model model) {
        if (userId == null) {
            return "redirect:/login"; // Rediriger si non connecté
        }

        User user = userService.getUserById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        List<Delivery> deliveries = user.isAdmin() ? deliveryService.getAllDeliveries()
                : deliveryService.getAllDeliveriesByUserId(user.getId());

        List<Delivery> upcomingDeliveries = new ArrayList<>();
        List<Delivery> pastDeliveries = new ArrayList<>();

        Map<Integer, String> clientNames = new HashMap<>();
        if (user.isAdmin()) {
            List<User> clients = userService.getAllNonAdminUsers();
            for (User client : clients) {
                clientNames.put(client.getId(), client.getName());
            }
        }

        for (Delivery delivery : deliveries) {
            if (Delivery.ACCEPTED.equals(delivery.getStatus()) || Delivery.PENDING.equals(delivery.getStatus())) {
                upcomingDeliveries.add(delivery);
            } else {
                pastDeliveries.add(delivery);
            }

            if (user.isAdmin()) {
                delivery.setClientName(clientNames.get(delivery.getUserId()));
            }
        }

        model.addAttribute("upcomingDeliveries", upcomingDeliveries);
        model.addAttribute("pastDeliveries", pastDeliveries);

        return "deliveries"; // Correspond à deliveries.html (Thymeleaf)
    }
}
