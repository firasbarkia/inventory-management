package com.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory.model.SupplierNotification;

public interface SupplierNotificationRepository extends JpaRepository<SupplierNotification, Long> {
}
