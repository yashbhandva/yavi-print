package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.PageRangeSelectionDTO;
import com.web_app.yaviPrint.entity.Order;
import com.web_app.yaviPrint.entity.PageRangeSelection;
import com.web_app.yaviPrint.repository.OrderRepository;
import com.web_app.yaviPrint.repository.PageRangeSelectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PageRangeSelectionService {

    private final PageRangeSelectionRepository pageRangeSelectionRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public PageRangeSelectionDTO createPageRange(Long orderId, PageRangeSelectionDTO rangeDTO) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));

        // Validate page range
        validatePageRange(rangeDTO, order.getTotalPages());

        PageRangeSelection range = new PageRangeSelection();
        range.setOrder(order);
        range.setStartPage(rangeDTO.getStartPage());
        range.setEndPage(rangeDTO.getEndPage());
        range.setRange(rangeDTO.getIsRange());
        range.setCopies(rangeDTO.getCopies() != null ? rangeDTO.getCopies() : 1);

        PageRangeSelection savedRange = pageRangeSelectionRepository.save(range);
        return mapToPageRangeSelectionDTO(savedRange);
    }

    public List<PageRangeSelectionDTO> getOrderPageRanges(Long orderId) {
        return pageRangeSelectionRepository.findByOrderId(orderId).stream()
                .map(this::mapToPageRangeSelectionDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateOrderPageRanges(Long orderId, List<PageRangeSelectionDTO> rangeDTOs) {
        // Delete existing ranges
        pageRangeSelectionRepository.deleteByOrderId(orderId);

        // Create new ranges
        for (PageRangeSelectionDTO rangeDTO : rangeDTOs) {
            createPageRange(orderId, rangeDTO);
        }
    }

    @Transactional
    public void deletePageRange(Long rangeId) {
        pageRangeSelectionRepository.deleteById(rangeId);
    }

    public int calculateTotalPagesFromRanges(Long orderId) {
        List<PageRangeSelection> ranges = pageRangeSelectionRepository.findByOrderId(orderId);
        int totalPages = 0;

        for (PageRangeSelection range : ranges) {
            if (range.isRange()) {
                int pagesInRange = range.getEndPage() - range.getStartPage() + 1;
                totalPages += pagesInRange * range.getCopies();
            } else {
                totalPages += range.getCopies(); // Single page
            }
        }

        return totalPages;
    }

    public String generatePrintInstructions(Long orderId) {
        List<PageRangeSelection> ranges = pageRangeSelectionRepository.findByOrderId(orderId);
        if (ranges.isEmpty()) {
            return "Print all pages";
        }

        StringBuilder instructions = new StringBuilder();
        for (PageRangeSelection range : ranges) {
            if (range.isRange()) {
                instructions.append(String.format("Pages %d-%d (%d copies)",
                        range.getStartPage(), range.getEndPage(), range.getCopies()));
            } else {
                instructions.append(String.format("Page %d (%d copies)",
                        range.getStartPage(), range.getCopies()));
            }
            instructions.append("; ");
        }

        return instructions.toString().replaceAll("; $", "");
    }

    public boolean validatePageRanges(Long orderId, int totalDocumentPages) {
        List<PageRangeSelection> ranges = pageRangeSelectionRepository.findByOrderId(orderId);

        for (PageRangeSelection range : ranges) {
            if (range.isRange()) {
                if (range.getStartPage() < 1 || range.getEndPage() > totalDocumentPages ||
                        range.getStartPage() > range.getEndPage()) {
                    return false;
                }
            } else {
                if (range.getStartPage() < 1 || range.getStartPage() > totalDocumentPages) {
                    return false;
                }
            }
        }

        return true;
    }

    private void validatePageRange(PageRangeSelectionDTO rangeDTO, int totalDocumentPages) {
        if (rangeDTO.getIsRange()) {
            if (rangeDTO.getStartPage() < 1 || rangeDTO.getEndPage() > totalDocumentPages) {
                throw new RuntimeException("Page range exceeds document boundaries");
            }
            if (rangeDTO.getStartPage() > rangeDTO.getEndPage()) {
                throw new RuntimeException("Start page cannot be greater than end page");
            }
        } else {
            if (rangeDTO.getStartPage() < 1 || rangeDTO.getStartPage() > totalDocumentPages) {
                throw new RuntimeException("Page number exceeds document boundaries");
            }
        }

        if (rangeDTO.getCopies() != null && rangeDTO.getCopies() < 1) {
            throw new RuntimeException("Copies must be at least 1");
        }
    }

    private PageRangeSelectionDTO mapToPageRangeSelectionDTO(PageRangeSelection range) {
        PageRangeSelectionDTO dto = new PageRangeSelectionDTO();
        dto.setId(range.getId());
        dto.setOrderId(range.getOrder().getId());
        dto.setStartPage(range.getStartPage());
        dto.setEndPage(range.getEndPage());
        dto.setIsRange(range.isRange());
        dto.setCopies(range.getCopies());
        return dto;
    }
}