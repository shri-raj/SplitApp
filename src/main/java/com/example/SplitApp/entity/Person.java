package com.example.SplitApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Entity
@Setter
@Table(name = "people")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(name = "total_paid", precision = 10, scale = 2)
    private BigDecimal totalPaid = BigDecimal.ZERO;

    @Column(name = "total_owed", precision = 10, scale = 2)
    private BigDecimal totalOwed = BigDecimal.ZERO;

    public Person() {}

    public Person(String name) {
        this.name = name;
    }

    public BigDecimal getBalance() {
        return totalPaid.subtract(totalOwed);
    }
}