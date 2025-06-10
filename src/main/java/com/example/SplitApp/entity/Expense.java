package com.example.SplitApp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Entity
@Setter
@Table(name = "expenses")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String description;

    @Column(name = "paid_by", nullable = false)
    private String paidBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "expense", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExpenseSplit> splits;

    @Enumerated(EnumType.STRING)
    private SplitType splitType = SplitType.EQUAL;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public Expense() {}

    public Expense(BigDecimal amount, String description, String paidBy) {
        this.amount = amount;
        this.description = description;
        this.paidBy = paidBy;
    }
}