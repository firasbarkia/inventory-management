package com.inventory.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.model.Item;
import com.inventory.model.Request;
import com.inventory.model.Supplier;
import com.inventory.repository.ItemRepository;
import com.inventory.repository.RequestRepository;
import com.inventory.repository.SupplierRepository;

@Service
public class ItemService implements IItemService {

    private final ItemRepository itemRepository;
    private final SupplierRepository supplierRepository;
    private final RequestRepository requestRepository;

    public ItemService(ItemRepository itemRepository, SupplierRepository supplierRepository, RequestRepository requestRepository) {
        this.itemRepository = itemRepository;
        this.supplierRepository = supplierRepository;
        this.requestRepository = requestRepository;
    }

    @Override
    public Item addItemToSupplier(Long supplierId, Item item) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        item.setSupplier(supplier);
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(Long itemId, Item updatedItem) {
        return itemRepository.findById(itemId).map(item -> {
            item.setName(updatedItem.getName());
            item.setQuantity(updatedItem.getQuantity());
            item.setMinStockLevel(updatedItem.getMinStockLevel());
            return itemRepository.save(item);
        }).orElseThrow(() -> new RuntimeException("Item not found"));
    }

    @Override
    @Transactional
    public void deleteItem(Long itemId) {
        List<Request> requests = requestRepository.findByItemId(itemId);
        if (!requests.isEmpty()) {
            throw new RuntimeException("Cannot delete item because it has associated requests.");
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return itemRepository.findById(itemId);
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }
}
