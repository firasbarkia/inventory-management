package com.inventory.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.model.Item;
import com.inventory.repository.ItemRepository;
import com.inventory.repository.SupplierRepository;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemRepository itemRepository;
    private final SupplierRepository supplierRepository;

    public ItemController(ItemRepository itemRepository, SupplierRepository supplierRepository) {
        this.itemRepository = itemRepository;
        this.supplierRepository = supplierRepository;
    }

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return itemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPPLIER')")
    @PostMapping
    public ResponseEntity<Object> createItem(@RequestBody Item item) {
        if (item.getSupplier() != null && item.getSupplier().getId() != null) {
            return supplierRepository.findById(item.getSupplier().getId())
                    .<ResponseEntity<Object>>map(supplier -> {
                        item.setSupplier(supplier);
                        Item saved = itemRepository.save(item);
                        return ResponseEntity.ok(saved);
                    })
                    .orElse(ResponseEntity.badRequest().body("Supplier with ID " + item.getSupplier().getId() + " does not exist"));
        } else {
            Item saved = itemRepository.save(item);
            return ResponseEntity.ok(saved);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPPLIER')")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @RequestBody Item item) {
        return itemRepository.findById(id).map(existing -> {
            existing.setName(item.getName());
            existing.setQuantity(item.getQuantity());
            if (item.getSupplier() != null && item.getSupplier().getId() != null) {
                supplierRepository.findById(item.getSupplier().getId()).ifPresent(existing::setSupplier);
            }
            Item updated = itemRepository.save(existing);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPPLIER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
