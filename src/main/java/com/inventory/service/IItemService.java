package com.inventory.service;

import java.util.List;
import java.util.Optional;

import com.inventory.model.Item;

public interface IItemService {
    Item addItemToSupplier(Long supplierId, Item item);
    Item updateItem(Long itemId, Item updatedItem);
    void deleteItem(Long itemId);
    Optional<Item> getItemById(Long itemId);
    List<Item> getAllItems();
}
