package com.geopos.controller;

import com.geopos.entity.LocationDateDto;
import com.geopos.entity.LocationDto;
import com.geopos.entity.UserDto;
import com.geopos.entity.UserLocationDto;
import com.geopos.entity.UserLocationsDto;
import com.geopos.services.LocationService;
import com.geopos.services.UserService;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.convert.format.Format;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.geopos.util.Consts.DATE_FORMAT;

/**
 * Controller for user related methods.
 */
@Controller("/v1/users")
@Validated
public class UserController {

    @Inject
    private UserService userService;

    @Inject
    private LocationService locationService;


    /**
     * Saves user's data.
     *
     * @param user the user DTO
     * @return the HttpResponse
     */
    @Post
    public HttpResponse<?> saveUser(UserDto user) {
        UserDto userFound = userService.getUser(user.getUserId());
        if (userFound != null) {
            user.setId(userFound.getId());
        }

        userService.saveUser(user);
        return HttpResponse.ok();
    }


    /**
     * Returns user with the latest location if it has any.
     *
     * @param userId the user id
     * @return the HttpResponse 404 (Not found) when user doesn't exist.
     */
    @Get("/{userId}")
    public HttpResponse<UserLocationDto> userWithLatestLocation(String userId) {
        UserDto user = userService.getUser(userId);
        if (user == null) {
            return HttpResponse.notFound();
        }

        UserLocationDto userWithLatestLocation = UserLocationDto.builder()
                .userId(user.getUserId())
                .createdOn(user.getCreatedOn())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .secondName(user.getSecondName())
                .build();
        Optional<LocationDateDto> latestLocation = locationService.getUserLatestLocation(user.getId());
        latestLocation.ifPresent(location -> userWithLatestLocation.setLocation(
                new com.geopos.entity.Location(location.getLatitude(), location.getLongitude())
        ));
        return HttpResponse.ok(userWithLatestLocation);
    }

    /**
     * Finds user's locations in the specified date and time range.
     * Returns all the locations till the specified toDate if fromDate query parameter is not set.
     * Returns all the locations from the specified fromDate if toDate query parameter is not set.
     * Returns all the locations if not fromDate neither toDate query parameters are not set.
     *
     * @param userId   the userId
     * @param fromDate the date to start from (inclusive)
     * @param toDate   the to end with (inclusive)
     * @return the HttpResponse 404 (Not found) when user doesn't exist.
     */
    @Get(uri = "/{userId}/locations")
    public HttpResponse<UserLocationsDto> userLocations(String userId,
                                                        @Nullable @Format(DATE_FORMAT) @QueryValue("from") Date fromDate,
                                                        @Nullable @Format(DATE_FORMAT) @QueryValue("to") Date toDate) {
        Optional<Long> userIdInternal = userService.getUserIdByExternalId(userId);
        if (userIdInternal.isEmpty()) {
            return HttpResponse.notFound();
        }
        List<LocationDateDto> userLocations = locationService.getUserLocations(userIdInternal.get(), fromDate, toDate);

        return HttpResponse.ok(UserLocationsDto.builder()
                        .userId(userId)
                        .locations(userLocations)
                .build());
    }

    /**
     * Saves user's location.
     *
     * @param location the location DTO
     * @return the HttpResponse 404 (Not found) when user doesn't exist.
     */
    @Post("/locations")
    public HttpResponse<?> saveLocation(@Valid @Body LocationDto location) {
        Optional<Long> userId = userService.getUserIdByExternalId(location.getUserId());
        if (userId.isEmpty()) {
            return HttpResponse.notFound();
        }
        locationService.saveLocation(location, userId.get());
        return HttpResponse.ok();
    }

}
