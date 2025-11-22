package com.web_app.yaviPrint.controller;

import com.web_app.yaviPrint.dto.AddressDTO;
import com.web_app.yaviPrint.service.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @PostMapping("/validate")
    public ResponseEntity<?> validateAddress(@Valid @RequestBody AddressDTO addressDTO) {
        try {
            var validatedAddress = addressService.validateAndFormatAddress(addressDTO);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Address validated successfully",
                    "data", validatedAddress
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }

    @PostMapping("/format")
    public ResponseEntity<?> formatAddress(@RequestBody AddressDTO addressDTO) {
        try {
            var formattedAddress = addressService.mapToAddressDTO(addressService.mapToAddressEntity(addressDTO));
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", formattedAddress
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        }
    }
}