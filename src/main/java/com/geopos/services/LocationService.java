package com.geopos.services;

import com.geopos.db.Location;
import com.geopos.entity.LocationDateDto;
import com.geopos.entity.LocationDto;
import com.geopos.repository.LocationRepository;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * The type Location service.
 */
@Singleton
public class LocationService {

    @Inject
    private LocationRepository locationRepository;

    /**
     * Save user's location.
     *
     * @param location the location
     * @param userId   the user id
     */
    public void saveLocation(LocationDto location, @NonNull Long userId) {
        locationRepository.save(
                Location.builder()
                        .userId(userId)
                        .createdOn(location.getCreatedOn().toInstant())
                        .longitude(location.getLocation().getLongitude())
                        .latitude(location.getLocation().getLatitude())
                .build());
    }

    /**
     * Gets user's latest location.
     *
     * @param userId the external user id
     * @return the user's latest location
     */
    public Optional<LocationDateDto> getUserLatestLocation(long userId) {
        return locationRepository.findMaxCreatedOnByUserId(userId);
    }

    /**
     * Gets user locations in specified date and time range.
     *
     * @param userId   the user id
     * @param fromDate the from date
     * @param toDate   the to date
     * @return the user locations
     */
    public List<LocationDateDto> getUserLocations(long userId, @Nullable Date fromDate, @Nullable Date toDate) {
        return locationRepository.findByUserIdAndTimeRange(userId, fromDate, toDate);
    }

}