package com.inventory.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.model.Item;
import com.inventory.service.IItemService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final IItemService itemService;

    public ItemController(IItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPPLIER')")
    public ResponseEntity<Item> addItemToSupplier(@RequestParam Long supplierId, @RequestBody Item item) {
        return ResponseEntity.ok(itemService.addItemToSupplier(supplierId, item));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Item>> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

     @PutMapping("/{id}")
     @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPPLIER')")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item updatedItem) {
        return ResponseEntity.ok(itemService.updateItem(id, updatedItem));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPPLIER')")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
