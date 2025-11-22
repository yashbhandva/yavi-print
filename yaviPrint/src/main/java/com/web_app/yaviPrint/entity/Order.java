package com.web_app.yaviPrint.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@ToString
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String tokenId;

    private String qrCodeUrl;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private int totalPages;

    private String printSettings;

    private double totalAmount;
    private double shopAmount;
    private double platformFee;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.PENDING;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime printedAt;
    private LocalDateTime collectedAt;
    private LocalDateTime cancelledAt;

    private String paymentId;
    private boolean paymentStatus = false;

    public Order() {}

    public Order(User user, Shop shop, String tokenId) {
        this.user = user;
        this.shop = shop;
        this.tokenId = tokenId;
    }

    public enum OrderStatus {
        PENDING, ACCEPTED, PRINTING, READY, COMPLETED, CANCELLED, REJECTED
    }

    public boolean canBeCancelled() {
        return status == OrderStatus.PENDING || status == OrderStatus.ACCEPTED;
    }

    public boolean isReadyForPickup() {
        return status == OrderStatus.READY;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTokenId() { return tokenId; }
    public void setTokenId(String tokenId) { this.tokenId = tokenId; }
    public String getQrCodeUrl() { return qrCodeUrl; }
    public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Shop getShop() { return shop; }
    public void setShop(Shop shop) { this.shop = shop; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public String getPrintSettings() { return printSettings; }
    public void setPrintSettings(String printSettings) { this.printSettings = printSettings; }
    public boolean isPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(boolean paymentStatus) { this.paymentStatus = paymentStatus; }
}