package com.web_app.yaviPrint.service;

import com.web_app.yaviPrint.dto.UserProfileDTO;
import com.web_app.yaviPrint.entity.User;
import com.web_app.yaviPrint.entity.UserProfile;
import com.web_app.yaviPrint.repository.UserProfileRepository;
import com.web_app.yaviPrint.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;

    @Transactional
    public UserProfileDTO createOrUpdateUserProfile(Long userId, UserProfileDTO userProfileDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElse(new UserProfile());

        userProfile.setUser(user);
        userProfile.setProfilePicture(userProfileDTO.getProfilePicture());
        userProfile.setDateOfBirth(userProfileDTO.getDateOfBirth());
        userProfile.setGender(userProfileDTO.getGender());
        userProfile.setAlternatePhone(userProfileDTO.getAlternatePhone());
        userProfile.setPreferences(userProfileDTO.getPreferences());

        UserProfile savedProfile = userProfileRepository.save(userProfile);
        return mapToUserProfileDTO(savedProfile);
    }

    public UserProfileDTO getUserProfile(Long userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found for user id: " + userId));
        return mapToUserProfileDTO(userProfile);
    }

    @Transactional
    public void deleteUserProfile(Long userId) {
        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found for user id: " + userId));
        userProfileRepository.delete(userProfile);
    }

    private UserProfileDTO mapToUserProfileDTO(UserProfile userProfile) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setUserId(userProfile.getUser().getId());
        dto.setProfilePicture(userProfile.getProfilePicture());
        dto.setDateOfBirth(userProfile.getDateOfBirth());
        dto.setGender(userProfile.getGender());
        dto.setAlternatePhone(userProfile.getAlternatePhone());
        dto.setPreferences(userProfile.getPreferences());
        return dto;
    }
}