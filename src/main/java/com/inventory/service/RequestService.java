package com.inventory.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.inventory.model.Item;
import com.inventory.model.Request;
import com.inventory.model.User;
import com.inventory.repository.ItemRepository;
import com.inventory.repository.RequestRepository;
import com.inventory.repository.UserRepository;

@Service
public class RequestService implements IRequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final NotificationService notificationService;

    public RequestService(RequestRepository requestRepository, UserRepository userRepository, ItemRepository itemRepository, NotificationService notificationService) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.notificationService = notificationService;
    }

    public Request createRequest(Long teacherId, Long itemId, int quantity) {
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (item.getQuantity() < quantity) {
            throw new RuntimeException("Not enough stock");
        }

        Request request = new Request();
        request.setTeacher(teacher);
        request.setItem(item);
        request.setQuantity(quantity);
        request.setStatus("REQUESTED");
        return requestRepository.save(request);
    }

    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    public Optional<Request> getRequestById(Long id) {
        return requestRepository.findById(id);
    }

    public Request assignWorker(Long requestId, Long workerId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        User worker = userRepository.findById(workerId)
                .orElseThrow(() -> new RuntimeException("Worker not found"));
        request.setWorker(worker);
        request.setStatus("APPROVED");
        Request savedRequest = requestRepository.save(request);
        notificationService.createWorkerNotification(savedRequest);
        return savedRequest;
    }

    public Request markAsDelivered(Long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        Item item = request.getItem();

        if (item.getQuantity() < request.getQuantity()) {
            throw new RuntimeException("Not enough stock to deliver");
        }

        item.setQuantity(item.getQuantity() - request.getQuantity());
        itemRepository.save(item);

        request.setStatus("DELIVERED");
        return requestRepository.save(request);
    }
}
