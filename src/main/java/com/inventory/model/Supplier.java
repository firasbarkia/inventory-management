package com.inventory.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

import com.inventory.model.Item;

@Entity
@Table(name = "suppliers")
@Data
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String contactInfo;

    @OneToMany(mappedBy = "supplier")
    private List<Item> items;
}
