package com.inventory.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "requests")
@Data
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private User teacher;

    @ManyToOne
    @JoinColumn(name = "worker_id")
    private User worker;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private int quantity;

    private String status; // e.g., REQUESTED, APPROVED, DELIVERED
}
