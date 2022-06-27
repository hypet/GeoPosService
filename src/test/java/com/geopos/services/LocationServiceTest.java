package com.geopos.services;

import com.geopos.entity.Location;
import com.geopos.entity.LocationDateDto;
import com.geopos.entity.LocationDto;
import com.geopos.repository.LocationRepository;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@MicronautTest
class LocationServiceTest {

    @Inject
    LocationService locationService;

    @Inject
    LocationRepository mockedRepository;

    @MockBean(LocationRepository.class)
    LocationRepository locationRepository() {
        return mock(LocationRepository.class);
    }

    @Test
    void testSaveLocation() {
        LocationDto location = LocationDto.builder()
                .userId("userId")
                .location(new Location(1.0, 2.0))
                .createdOn(new Date())
                .build();
        locationService.saveLocation(location, 1L);
        verify(mockedRepository, times(1)).save(any());
    }

    @Test
    void testGetUserLatestLocation() {
        when(mockedRepository.findMaxCreatedOnByUserId(eq(1L)))
                .thenReturn(Optional.of(mock(LocationDateDto.class)));
        Optional<LocationDateDto> userLatestLocation = locationService.getUserLatestLocation(1L);

        assertTrue(userLatestLocation.isPresent());
        verify(mockedRepository, times(1)).findMaxCreatedOnByUserId(eq(1L));
    }

    @Test
    void testGetUserLocations() {
        Date fromDate = new Date();
        Date toDate = new Date();
        when(mockedRepository.findByUserIdAndTimeRange(eq(1L), eq(fromDate), eq(toDate)))
                .thenReturn(List.of(new LocationDateDto(), new LocationDateDto()));
        List<LocationDateDto> userLocations = locationService.getUserLocations(1L, fromDate, toDate);
        assertNotNull(userLocations);
        assertEquals(2, userLocations.size());

        verify(mockedRepository, times(1)).findByUserIdAndTimeRange(1L, fromDate, toDate);
    }

    @Test
    void testGetUserLocationsFromDateIsNull() {
        Date toDate = new Date();
        when(mockedRepository.findByUserIdAndTimeRange(eq(1L), eq(null), eq(toDate)))
                .thenReturn(List.of(new LocationDateDto(), new LocationDateDto()));
        List<LocationDateDto> userLocations = locationService.getUserLocations(1L, null, toDate);
        assertNotNull(userLocations);
        assertEquals(2, userLocations.size());

        verify(mockedRepository, times(1)).findByUserIdAndTimeRange(1L, null, toDate);
    }

    @Test
    void testGetUserLocationsToDateIsNull() {
        Date fromDate = new Date();
        when(mockedRepository.findByUserIdAndTimeRange(eq(1L), eq(fromDate), eq(null)))
                .thenReturn(List.of(new LocationDateDto(), new LocationDateDto()));
        List<LocationDateDto> userLocations = locationService.getUserLocations(1L, fromDate, null);
        assertNotNull(userLocations);
        assertEquals(2, userLocations.size());

        verify(mockedRepository, times(1)).findByUserIdAndTimeRange(1L, fromDate, null);
    }
}