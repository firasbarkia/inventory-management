package com.inventory.service;

import com.inventory.model.Request;

import java.util.List;
import java.util.Optional;

public interface IRequestService {
    Request createRequest(Long teacherId, Long itemId, int quantity);
    List<Request> getAllRequests();
    Optional<Request> getRequestById(Long id);
    Request assignWorker(Long requestId, Long workerId);
    Request markAsDelivered(Long requestId);
}
