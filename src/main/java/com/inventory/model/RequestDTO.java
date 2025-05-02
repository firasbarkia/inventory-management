package com.inventory.model;

public class RequestDTO {
    private Long id;
    private Long teacherId;
    private Long workerId;
    private Long itemId;
    private int quantity;
    private String status;

    public RequestDTO() {}

    public RequestDTO(Request request) {
        this.id = request.getId();
        this.teacherId = request.getTeacher() != null ? request.getTeacher().getId() : null;
        this.workerId = request.getWorker() != null ? request.getWorker().getId() : null;
        this.itemId = request.getItem() != null ? request.getItem().getId() : null;
        this.quantity = request.getQuantity();
        this.status = request.getStatus();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getTeacherId() { return teacherId; }
    public void setTeacherId(Long teacherId) { this.teacherId = teacherId; }
    public Long getWorkerId() { return workerId; }
    public void setWorkerId(Long workerId) { this.workerId = workerId; }
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
