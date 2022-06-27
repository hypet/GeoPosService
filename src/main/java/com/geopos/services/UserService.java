package com.geopos.services;

import com.geopos.db.User;
import com.geopos.entity.UserDto;
import com.geopos.repository.UserRepository;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Date;
import java.util.Optional;

/**
 * The type User service.
 */
@Singleton
public class UserService {

    @Inject
    private UserRepository userRepository;

    /**
     * Saves user data.
     *
     * @param userDto the user dto
     */
    public void saveUser(UserDto userDto) {
        User user = User.builder()
                .id(userDto.getId())
                .userId(userDto.getUserId())
                .createdOn(userDto.getCreatedOn().toInstant())
                .email(userDto.getEmail())
                .firstName(userDto.getFirstName())
                .secondName(userDto.getSecondName())
                .build();

        if (user.getId() != null) {
            userRepository.update(user);
        } else {
            userRepository.save(user);
        }
    }

    /**
     * Gets user id (numerical) by external id (string).
     *
     * @param userId the external user id
     * @return the user id
     */
    public Optional<Long> getUserIdByExternalId(String userId) {
        return userRepository.findIdByUserId(userId);
    }

    /**
     * Gets user data by external userId.
     *
     * @param userId the user id
     * @return the user
     */
    public UserDto getUser(String userId) {
        Optional<User> userOptional = userRepository.findByUserId(userId);
        if (userOptional.isEmpty()) {
            return null;
        }
        User user = userOptional.get();

        return UserDto.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .createdOn(new Date(user.getCreatedOn().toEpochMilli()))
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .secondName(user.getSecondName())
                .build();
    }

}
