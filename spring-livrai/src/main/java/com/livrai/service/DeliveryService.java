package com.livrai.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.livrai.entity.Delivery;
import com.livrai.repository.DeliveryRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class DeliveryService {
    
    @Autowired
    private DeliveryRepository deliveryRepository;
    
    public Delivery createDelivery(Delivery delivery) {
        delivery.setStatus(Delivery.PENDING);
        return deliveryRepository.save(delivery);
    }
    
    public Delivery getDeliveryById(int id) {
        return deliveryRepository.findById(id).orElse(null);
    }
    
    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }
    
    public List<Delivery> getAllDeliveriesByUserId(int userId) {
        return deliveryRepository.findByUserId(userId);
    }
    
    public List<Delivery> getAllUpcomingDeliveries() {
        return deliveryRepository.findByStatusIn(List.of(Delivery.PENDING, Delivery.ACCEPTED));
    }
    
    public List<Delivery> getAllPastDeliveries() {
        return deliveryRepository.findByStatusIn(List.of(Delivery.FINISHED, Delivery.REJECTED));
    }
    
    public List<Delivery> getUpcomingDeliveriesByUserId(int userId) {
        return deliveryRepository.findByUserIdAndStatusIn(
            userId, List.of(Delivery.PENDING, Delivery.ACCEPTED)
        );
    }
    
    public List<Delivery> getPastDeliveriesByUserId(int userId) {
        return deliveryRepository.findByUserIdAndStatusIn(
            userId, List.of(Delivery.FINISHED, Delivery.REJECTED)
        );
    }
    
    public Delivery acceptDeliveryById(int id) {
        return deliveryRepository.findById(id).map(delivery -> {
            delivery.setStatus(Delivery.ACCEPTED);
            return deliveryRepository.save(delivery);
        }).orElseThrow(() -> new RuntimeException("Delivery not found"));
    }
    
    public Delivery rejectDeliveryById(int id) {
        return deliveryRepository.findById(id).map(delivery -> {
            delivery.setStatus(Delivery.REJECTED);
            return deliveryRepository.save(delivery);
        }).orElseThrow(() -> new RuntimeException("Delivery not found"));
    }
    
    public Delivery billDeliveryById(int id, double price) {
        return deliveryRepository.findById(id).map(delivery -> {
            delivery.setStatus(Delivery.FINISHED);
            delivery.setPrice(price);
            return deliveryRepository.save(delivery);
        }).orElseThrow(() -> new RuntimeException("Delivery not found"));
    }
    
    public void deleteDelivery(int id) {
        deliveryRepository.deleteById(id);
    }
}