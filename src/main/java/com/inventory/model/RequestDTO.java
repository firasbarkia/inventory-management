package com.inventory.model;

public class RequestDTO {
    private Long id;
    private Long teacherId;
    private User teacher;
    private Long workerId;
    private User worker;
    private Long itemId;
    private Item item;
    private int quantity;
    private String status;

    public RequestDTO() {}

    public RequestDTO(Request request) {
        this.id = request.getId();
        this.teacher = request.getTeacher();
        this.worker = request.getWorker();
        this.item = request.getItem();
        this.quantity = request.getQuantity();
        this.status = request.getStatus();
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getTeacher() { return teacher; }
    public void setTeacher(User teacher) { this.teacher = teacher; }
    public User getWorker() { return worker; }
    public void setWorker(User worker) { this.worker = worker; }
    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
