package com.inventory.service;

import com.inventory.model.Item;
import com.inventory.model.Supplier;

import java.util.List;
import java.util.Optional;

public interface ISupplierService {
    Supplier createSupplier(Supplier supplier);
    Optional<Supplier> getSupplierById(Long id);
    List<Supplier> getAllSuppliers();
    Supplier updateSupplier(Long id, Supplier updatedSupplier);
    void deleteSupplier(Long id);

    Item addItemToSupplier(Long supplierId, Item item);
    Item updateItem(Long itemId, Item updatedItem);
    void deleteItem(Long itemId);
}
