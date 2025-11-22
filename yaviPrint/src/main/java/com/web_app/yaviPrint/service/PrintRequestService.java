package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.*;
import com.web_app.yaviPrint.entity.*;
import com.web_app.yaviPrint.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrintRequestService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final DocumentFileRepository documentFileRepository;
    private final PrintPickupCodeRepository printPickupCodeRepository;
    private final QrCodeService qrCodeService;
    private final EmailService emailService;

    @Transactional
    public PrintRequestResponseDTO createPrintRequest(PrintRequestCreateDTO requestDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Shop shop = shopRepository.findById(requestDTO.getShopId())
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + requestDTO.getShopId()));

        DocumentFile documentFile = documentFileRepository.findById(requestDTO.getDocumentFileId())
                .orElseThrow(() -> new RuntimeException("Document file not found with id: " + requestDTO.getDocumentFileId()));

        // Generate unique token ID
        String tokenId = generateTokenId();

        // Create order
        Order order = new Order(user, shop, tokenId);
        order.setFileName(documentFile.getFileName());
        order.setFileUrl(documentFile.getFileUrl());
        order.setFileType(documentFile.getFileType());
        order.setFileSize(documentFile.getFileSize());
        order.setTotalPages(documentFile.getTotalPages());
        order.setPrintSettings(convertPrintSettingsToJson(requestDTO.getPrintSettings()));
        order.setTotalAmount(calculateTotalAmount(shop, documentFile, requestDTO));
        order.setPlatformFee(calculatePlatformFee(order.getTotalAmount()));

        Order savedOrder = orderRepository.save(order);

        // Generate QR code and pickup code
        String qrCodeUrl = qrCodeService.generateQrCode(tokenId);
        createPrintPickupCode(savedOrder, qrCodeUrl);

        // Send confirmation email
        emailService.sendOrderConfirmation(user.getEmail(), user.getName(), tokenId);

        return mapToPrintRequestResponseDTO(savedOrder);
    }

    public PrintRequestResponseDTO getPrintRequestById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        return mapToPrintRequestResponseDTO(order);
    }

    public PrintRequestResponseDTO getPrintRequestByToken(String tokenId) {
        Order order = orderRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new RuntimeException("Order not found with token: " + tokenId));
        return mapToPrintRequestResponseDTO(order);
    }

    public List<PrintRequestResponseDTO> getUserPrintRequests(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToPrintRequestResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PrintRequestResponseDTO> getShopPrintRequests(Long shopId) {
        return orderRepository.findByShopId(shopId).stream()
                .map(this::mapToPrintRequestResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public PrintRequestResponseDTO updatePrintRequestStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
        order.setStatus(orderStatus);

        if (orderStatus == Order.OrderStatus.PRINTING) {
            order.setPrintedAt(LocalDateTime.now());
        } else if (orderStatus == Order.OrderStatus.COMPLETED) {
            order.setCollectedAt(LocalDateTime.now());
        }

        Order updatedOrder = orderRepository.save(order);
        return mapToPrintRequestResponseDTO(updatedOrder);
    }

    @Transactional
    public void cancelPrintRequest(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        if (!order.canBeCancelled()) {
            throw new RuntimeException("Order cannot be cancelled in current status");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    private String generateTokenId() {
        String tokenId;
        do {
            tokenId = "A" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        } while (orderRepository.findByTokenId(tokenId).isPresent());
        return tokenId;
    }

    private void createPrintPickupCode(Order order, String qrCodeUrl) {
        PrintPickupCode pickupCode = new PrintPickupCode();
        pickupCode.setOrder(order);
        pickupCode.setTokenId(order.getTokenId());
        pickupCode.setQrCodeUrl(qrCodeUrl);
        pickupCode.setShortCode(generateShortCode());
        pickupCode.setGeneratedAt(LocalDateTime.now());
        pickupCode.setExpiresAt(LocalDateTime.now().plusDays(3)); // 3 days expiry
        pickupCode.setUsed(false);

        printPickupCodeRepository.save(pickupCode);

        // Update order with QR code URL
        order.setQrCodeUrl(qrCodeUrl);
        orderRepository.save(order);
    }

    private String generateShortCode() {
        return String.valueOf((int) (Math.random() * 9000) + 1000); // 4-digit code
    }

    private Double calculateTotalAmount(Shop shop, DocumentFile documentFile, PrintRequestCreateDTO requestDTO) {
        PrintSettingsDTO settings = requestDTO.getPrintSettings();
        int copies = requestDTO.getCopies() != null ? requestDTO.getCopies() : 1;

        return shop.calculatePrice(
                documentFile.getTotalPages(),
                "COLOR".equals(settings.getPrintType()),
                "A3".equals(settings.getPaperSize()),
                copies
        );
    }

    private Double calculatePlatformFee(Double totalAmount) {
        return totalAmount * 0.10; // 10% platform fee
    }

    private String convertPrintSettingsToJson(PrintSettingsDTO settings) {
        // Simple JSON conversion - in production use Jackson
        return String.format(
                "{\"paperSize\":\"%s\",\"printType\":\"%s\",\"duplex\":%s,\"copies\":%d}",
                settings.getPaperSize(), settings.getPrintType(), settings.getDuplex(), settings.getCopies()
        );
    }

    private PrintRequestResponseDTO mapToPrintRequestResponseDTO(Order order) {
        PrintRequestResponseDTO dto = new PrintRequestResponseDTO();
        dto.setId(order.getId());
        dto.setTokenId(order.getTokenId());
        dto.setQrCodeUrl(order.getQrCodeUrl());
        dto.setUser(mapToUserResponseDTO(order.getUser()));
        dto.setShop(mapToShopResponseDTO(order.getShop()));
        dto.setDocumentFile(mapToDocumentFileDTO(order));
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus().name());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setPrintedAt(order.getPrintedAt());
        dto.setCollectedAt(order.getCollectedAt());
        dto.setPaymentStatus(order.isPaymentStatus());
        return dto;
    }

    private UserResponseDTO mapToUserResponseDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        return dto;
    }

    private ShopResponseDTO mapToShopResponseDTO(Shop shop) {
        ShopResponseDTO dto = new ShopResponseDTO();
        dto.setId(shop.getId());
        dto.setShopName(shop.getShopName());
        dto.setAddress(shop.getAddress());
        dto.setCity(shop.getCity());
        dto.setBwPricePerPage(shop.getBwPricePerPage());
        dto.setColorPricePerPage(shop.getColorPricePerPage());
        return dto;
    }

    private DocumentFileDTO mapToDocumentFileDTO(Order order) {
        DocumentFileDTO dto = new DocumentFileDTO();
        dto.setFileName(order.getFileName());
        dto.setFileType(order.getFileType());
        dto.setTotalPages(order.getTotalPages());
        return dto;
    }
}