package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.PrintSettingsDTO;
import com.web_app.yaviPrint.entity.PrintSettings;
import com.web_app.yaviPrint.repository.PrintSettingsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrintSettingsService {

    private final PrintSettingsRepository printSettingsRepository;

    @Transactional
    public PrintSettingsDTO createPrintSettings(PrintSettingsDTO settingsDTO) {
        PrintSettings settings = new PrintSettings();
        settings.setPaperSize(settingsDTO.getPaperSize() != null ? settingsDTO.getPaperSize() : "A4");
        settings.setPrintType(settingsDTO.getPrintType() != null ? settingsDTO.getPrintType() : "BW");
        settings.setDuplex(settingsDTO.getDuplex() != null ? settingsDTO.getDuplex() : false);
        settings.setPageRanges(settingsDTO.getPageRanges());
        settings.setCopies(settingsDTO.getCopies() != null ? settingsDTO.getCopies() : 1);
        settings.setOrientation(settingsDTO.getOrientation() != null ? settingsDTO.getOrientation() : "PORTRAIT");
        settings.setQuality(settingsDTO.getQuality() != null ? settingsDTO.getQuality() : "STANDARD");
        settings.setFitToPage(settingsDTO.getFitToPage() != null ? settingsDTO.getFitToPage() : true);

        PrintSettings savedSettings = printSettingsRepository.save(settings);
        return mapToPrintSettingsDTO(savedSettings);
    }

    public PrintSettingsDTO getPrintSettingsById(Long settingsId) {
        PrintSettings settings = printSettingsRepository.findById(settingsId)
                .orElseThrow(() -> new RuntimeException("Print settings not found with id: " + settingsId));
        return mapToPrintSettingsDTO(settings);
    }

    @Transactional
    public PrintSettingsDTO updatePrintSettings(Long settingsId, PrintSettingsDTO settingsDTO) {
        PrintSettings settings = printSettingsRepository.findById(settingsId)
                .orElseThrow(() -> new RuntimeException("Print settings not found with id: " + settingsId));

        if (settingsDTO.getPaperSize() != null) {
            settings.setPaperSize(settingsDTO.getPaperSize());
        }
        if (settingsDTO.getPrintType() != null) {
            settings.setPrintType(settingsDTO.getPrintType());
        }
        if (settingsDTO.getDuplex() != null) {
            settings.setDuplex(settingsDTO.getDuplex());
        }
        if (settingsDTO.getPageRanges() != null) {
            settings.setPageRanges(settingsDTO.getPageRanges());
        }
        if (settingsDTO.getCopies() != null) {
            settings.setCopies(settingsDTO.getCopies());
        }
        if (settingsDTO.getOrientation() != null) {
            settings.setOrientation(settingsDTO.getOrientation());
        }
        if (settingsDTO.getQuality() != null) {
            settings.setQuality(settingsDTO.getQuality());
        }
        if (settingsDTO.getFitToPage() != null) {
            settings.setFitToPage(settingsDTO.getFitToPage());
        }

        PrintSettings updatedSettings = printSettingsRepository.save(settings);
        return mapToPrintSettingsDTO(updatedSettings);
    }

    @Transactional
    public void deletePrintSettings(Long settingsId) {
        PrintSettings settings = printSettingsRepository.findById(settingsId)
                .orElseThrow(() -> new RuntimeException("Print settings not found with id: " + settingsId));
        printSettingsRepository.delete(settings);
    }

    public PrintSettingsDTO getDefaultSettings() {
        PrintSettingsDTO defaultSettings = new PrintSettingsDTO();
        defaultSettings.setPaperSize("A4");
        defaultSettings.setPrintType("BW");
        defaultSettings.setDuplex(false);
        defaultSettings.setCopies(1);
        defaultSettings.setOrientation("PORTRAIT");
        defaultSettings.setQuality("STANDARD");
        defaultSettings.setFitToPage(true);
        return defaultSettings;
    }

    public boolean validateSettings(PrintSettingsDTO settings) {
        // Validate paper size
        if (!List.of("A4", "A3", "PHOTO").contains(settings.getPaperSize())) {
            return false;
        }

        // Validate print type
        if (!List.of("BW", "COLOR").contains(settings.getPrintType())) {
            return false;
        }

        // Validate orientation
        if (!List.of("PORTRAIT", "LANDSCAPE").contains(settings.getOrientation())) {
            return false;
        }

        // Validate quality
        if (!List.of("DRAFT", "STANDARD", "HIGH").contains(settings.getQuality())) {
            return false;
        }

        // Validate copies
        if (settings.getCopies() != null && (settings.getCopies() < 1 || settings.getCopies() > 100)) {
            return false;
        }

        return true;
    }

    public double calculateInkUsage(PrintSettingsDTO settings, int totalPages) {
        double baseInkUsage = 0.5; // ml per page (simplified)

        // Adjust for print type
        if ("COLOR".equals(settings.getPrintType())) {
            baseInkUsage *= 3; // Color uses more ink
        }

        // Adjust for quality
        if ("HIGH".equals(settings.getQuality())) {
            baseInkUsage *= 1.5;
        } else if ("DRAFT".equals(settings.getQuality())) {
            baseInkUsage *= 0.7;
        }

        // Adjust for paper size
        if ("A3".equals(settings.getPaperSize())) {
            baseInkUsage *= 2; // A3 uses twice the ink
        }

        return baseInkUsage * totalPages * settings.getCopies();
    }

    public String generatePrintCommand(PrintSettingsDTO settings, String fileName) {
        StringBuilder command = new StringBuilder();

        // Paper size
        command.append("-o media=").append(settings.getPaperSize());

        // Print type
        if ("COLOR".equals(settings.getPrintType())) {
            command.append(" -o ColorModel=RGB");
        } else {
            command.append(" -o ColorModel=Gray");
        }

        // Duplex printing
        if (settings.getDuplex()) {
            command.append(" -o Duplex=DuplexNoTumble");
        }

        // Copies
        if (settings.getCopies() > 1) {
            command.append(" -o copies=").append(settings.getCopies());
        }

        // Orientation
        if ("LANDSCAPE".equals(settings.getOrientation())) {
            command.append(" -o landscape");
        }

        // Quality
        if ("DRAFT".equals(settings.getQuality())) {
            command.append(" -o PrintQuality=Draft");
        } else if ("HIGH".equals(settings.getQuality())) {
            command.append(" -o PrintQuality=High");
        }

        command.append(" ").append(fileName);

        return command.toString();
    }

    private PrintSettingsDTO mapToPrintSettingsDTO(PrintSettings settings) {
        PrintSettingsDTO dto = new PrintSettingsDTO();
        dto.setId(settings.getId());
        dto.setPaperSize(settings.getPaperSize());
        dto.setPrintType(settings.getPrintType());
        dto.setDuplex(settings.isDuplex());
        dto.setPageRanges(settings.getPageRanges());
        dto.setCopies(settings.getCopies());
        dto.setOrientation(settings.getOrientation());
        dto.setQuality(settings.getQuality());
        dto.setFitToPage(settings.isFitToPage());
        return dto;
    }
}