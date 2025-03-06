package com.livrai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.livrai.entity.Delivery;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {
  List<Delivery> findByUserId(Integer userId);

  List<Delivery> findByStatus(String status);
}
