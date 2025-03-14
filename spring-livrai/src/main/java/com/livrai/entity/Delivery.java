package com.livrai.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "delivery")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

  public static final String PENDING = "En attente";
  public static final String ACCEPTED = "Acceptée";
  public static final String REJECTED = "Refusée";
  public static final String FINISHED = "Terminée";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "userId", nullable = false)
  private Integer userId;

  @Column
  private Integer volume;

  @Column
  private Integer weight;

  @Column
  private Double price;

  @Column
  private String status;

  @Transient
  private String clientName; // Non stocké en base, peut être calculé à partir d'une relation

  
}
