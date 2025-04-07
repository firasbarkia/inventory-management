package com.inventory.service;

import com.inventory.model.Item;
import com.inventory.model.Supplier;
import com.inventory.repository.ItemRepository;
import com.inventory.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierService implements ISupplierService {

    private final SupplierRepository supplierRepository;
    private final ItemRepository itemRepository;

    public SupplierService(SupplierRepository supplierRepository, ItemRepository itemRepository) {
        this.supplierRepository = supplierRepository;
        this.itemRepository = itemRepository;
    }

    public Supplier createSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }

    public Optional<Supplier> getSupplierById(Long id) {
        return supplierRepository.findById(id);
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Supplier updateSupplier(Long id, Supplier updatedSupplier) {
        return supplierRepository.findById(id).map(supplier -> {
            supplier.setName(updatedSupplier.getName());
            supplier.setContactInfo(updatedSupplier.getContactInfo());
            return supplierRepository.save(supplier);
        }).orElseThrow(() -> new RuntimeException("Supplier not found"));
    }

    public void deleteSupplier(Long id) {
        supplierRepository.deleteById(id);
    }

    public Item addItemToSupplier(Long supplierId, Item item) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        item.setSupplier(supplier);
        return itemRepository.save(item);
    }

    public Item updateItem(Long itemId, Item updatedItem) {
        return itemRepository.findById(itemId).map(item -> {
            item.setName(updatedItem.getName());
            item.setQuantity(updatedItem.getQuantity());
            return itemRepository.save(item);
        }).orElseThrow(() -> new RuntimeException("Item not found"));
    }

    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }
}
