package com.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory.model.WorkerNotification;

public interface WorkerNotificationRepository extends JpaRepository<WorkerNotification, Long> {
}
