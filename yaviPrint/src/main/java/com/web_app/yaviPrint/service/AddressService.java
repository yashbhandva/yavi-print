package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.AddressDTO;
import com.web_app.yaviPrint.entity.Address;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    public AddressDTO mapToAddressDTO(Address address) {
        if (address == null) return null;

        AddressDTO dto = new AddressDTO();
        dto.setStreet(address.getStreet());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setPincode(address.getPincode());
        dto.setLandmark(address.getLandmark());
        dto.setAddressType(address.getAddressType());
        return dto;
    }

    public Address mapToAddressEntity(AddressDTO addressDTO) {
        if (addressDTO == null) return null;

        Address address = new Address();
        address.setStreet(addressDTO.getStreet());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setPincode(addressDTO.getPincode());
        address.setLandmark(addressDTO.getLandmark());
        address.setAddressType(addressDTO.getAddressType());
        return address;
    }

    public AddressDTO validateAndFormatAddress(AddressDTO addressDTO) {
        // Basic validation
        if (addressDTO.getStreet() == null || addressDTO.getStreet().trim().isEmpty()) {
            throw new RuntimeException("Street address is required");
        }
        if (addressDTO.getCity() == null || addressDTO.getCity().trim().isEmpty()) {
            throw new RuntimeException("City is required");
        }
        if (addressDTO.getPincode() == null || addressDTO.getPincode().trim().isEmpty()) {
            throw new RuntimeException("Pincode is required");
        }

        // Format pincode (remove spaces)
        if (addressDTO.getPincode() != null) {
            addressDTO.setPincode(addressDTO.getPincode().replaceAll("\\s+", ""));
        }

        return addressDTO;
    }
}