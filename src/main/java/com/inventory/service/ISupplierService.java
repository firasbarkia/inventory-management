package com.inventory.service;

import java.util.List;
import java.util.Optional;

import com.inventory.model.Supplier;

public interface ISupplierService {
    Supplier createSupplier(Supplier supplier);
    Optional<Supplier> getSupplierById(Long id);
    List<Supplier> getAllSuppliers();
    Supplier updateSupplier(Long id, Supplier updatedSupplier);
    void deleteSupplier(Long id);
}
