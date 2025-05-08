package com.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.inventory.model.Request;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByItemId(Long itemId);
}
