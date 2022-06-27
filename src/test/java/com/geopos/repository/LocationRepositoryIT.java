package com.geopos.repository;

import com.geopos.db.Location;
import com.geopos.entity.LocationDateDto;
import com.geopos.util.Consts;
import io.micronaut.context.BeanContext;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class LocationRepositoryIT {

    @Inject
    LocationRepository locationRepository;

    @Inject
    BeanContext beanContext;

    @Test
    void testSave() {
        Instant now = Instant.now();
        long userId = new Random().nextLong();
        locationRepository.save(
                Location.builder()
                        .userId(userId)
                        .createdOn(now)
                        .latitude(10.11)
                        .longitude(11.12)
                .build()
        );

        Optional<LocationDateDto> maxCreatedOnByUserId = locationRepository.findMaxCreatedOnByUserId(userId);
        assertTrue(maxCreatedOnByUserId.isPresent());
        assertEquals(Date.from(now), maxCreatedOnByUserId.get().getCreatedOn());
        SimpleDateFormat formatter = new SimpleDateFormat(Consts.DATE_FORMAT);
        assertEquals(formatter.format(Date.from(now)), formatter.format(maxCreatedOnByUserId.get().getCreatedOn()));
    }

    @Test
    void testFindByUserIdAndTimeRange() {
        long now = System.currentTimeMillis();
        long userId = new Random().nextLong();
        for (int i = 1; i <= 10; i++) {
            locationRepository.save(
                    Location.builder()
                            .userId(userId)
                            .createdOn(Instant.ofEpochMilli(now + i * 1000L))
                            .latitude(10.11 + i)
                            .longitude(20.12 + i)
                            .build()
            );
        }

        // Test DTO fields
        {
            List<LocationDateDto> locations = locationRepository.findByUserIdAndTimeRange(
                    userId, null, null
            );
            assertEquals(10, locations.size());
            locations.forEach(location -> {
                assertTrue(location.getCreatedOn().getTime() > now);
                assertTrue(location.getLatitude() > 10.11);
                assertTrue(location.getLongitude() > 20.12);
            });
        }

        // Test validity of dates
        {
            List<LocationDateDto> locations = locationRepository.findByUserIdAndTimeRange(
                    userId,
                    new Date(now),
                    new Date(now + 3000L)
            );
            assertEquals(3, locations.size());
        }
    }

    @Test
    void testFindMaxCreatedOnByUserId() {
        long now = System.currentTimeMillis();
        long userId = new Random().nextLong();
        for (int i = 1; i <= 10; i++) {
            locationRepository.save(
                    Location.builder()
                            .userId(userId)
                            .createdOn(Instant.ofEpochMilli(now + i * 1000L))
                            .latitude(10.11 + i)
                            .longitude(20.12 + i)
                            .build()
            );
        }

        Optional<LocationDateDto> result = locationRepository.findMaxCreatedOnByUserId(userId);
        assertTrue(result.isPresent());
        assertEquals(10.11 + 10, result.get().getLatitude());
        assertEquals(20.12 + 10, result.get().getLongitude());
        assertEquals(new Date(now + 10_000L), result.get().getCreatedOn());
    }
}