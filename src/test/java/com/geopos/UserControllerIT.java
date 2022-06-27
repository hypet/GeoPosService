package com.geopos;

import com.geopos.entity.LocationDto;
import com.geopos.entity.UserDto;
import com.geopos.entity.UserLocationDto;
import com.geopos.entity.Location;
import com.geopos.entity.UserLocationsDto;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import static com.geopos.util.Consts.DATE_FORMAT;
import static io.micronaut.http.HttpStatus.BAD_REQUEST;
import static io.micronaut.http.HttpStatus.NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class UserControllerIT {

    @Inject
    @Client("/v1/users")
    HttpClient client;

    /* Save User */
    @Test
    void testSaveUser() {
        String uuid = UUID.randomUUID().toString();
        UserDto user = UserDto.builder()
                .userId(uuid)
                .createdOn(new Date())
                .email("user@mail.com")
                .firstName("firstName")
                .secondName("secondName")
                .build();
        HttpResponse<Object> result = client.toBlocking().exchange(HttpRequest.POST("", user));
        assertEquals(HttpStatus.OK, result.status());

        UserLocationDto response = client.toBlocking().retrieve(HttpRequest.GET("/" + uuid), UserLocationDto.class);
        assertEquals(uuid, response.getUserId());
        assertEquals(user.getCreatedOn(), response.getCreatedOn());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getFirstName(), response.getFirstName());
        assertEquals(user.getSecondName(), response.getSecondName());
        assertNull(response.getLocation());
    }

    @Test
    void testUpdateUser() {
        String uuid = UUID.randomUUID().toString();
        UserDto user = UserDto.builder()
                .userId(uuid)
                .createdOn(new Date())
                .email("user@mail.com")
                .firstName("firstName")
                .secondName("secondName")
                .build();
        {
            HttpResponse<Object> result = client.toBlocking().exchange(HttpRequest.POST("", user));
            assertEquals(HttpStatus.OK, result.status());

            UserLocationDto response = client.toBlocking().retrieve(HttpRequest.GET("/" + uuid), UserLocationDto.class);
            assertEquals(uuid, response.getUserId());
            assertEquals(user.getCreatedOn(), response.getCreatedOn());
            assertEquals(user.getEmail(), response.getEmail());
            assertEquals(user.getFirstName(), response.getFirstName());
            assertEquals(user.getSecondName(), response.getSecondName());
            assertNull(response.getLocation());
        }

        {
            user.setEmail("new-email@mail.com");
            user.setFirstName("new-firstName");
            user.setSecondName("new-secondName");
            HttpResponse<Object> result = client.toBlocking().exchange(HttpRequest.POST("", user));
            assertEquals(HttpStatus.OK, result.status());

            UserLocationDto response = client.toBlocking().retrieve(HttpRequest.GET("/" + uuid), UserLocationDto.class);
            assertEquals(uuid, response.getUserId());
            assertEquals(user.getCreatedOn(), response.getCreatedOn());
            assertEquals("new-email@mail.com", response.getEmail());
            assertEquals("new-firstName", response.getFirstName());
            assertEquals("new-secondName", response.getSecondName());
            assertNull(response.getLocation());
        }
    }

    @Test
    void testSaveUserInvalidDate() {
        String request = "{\n" +
                "\"userId\": \"2e3b11b0-07a4-4873-8de5-d2ae2eab26b2\",\n" +
                "\"createdOn\": \"2022-02-08_11:44:00.524\",\n" +
                "\"email\": \"alex.schmid@gmail.com\",\n" +
                "\"firstName\": \"Alex\",\n" +
                "\"secondName\": \"Schmid\"\n" +
                "}";
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(HttpRequest.POST("", request))
        );
        assertNotNull(thrown.getResponse());
        assertEquals(BAD_REQUEST, thrown.getStatus());
    }
    /* Save User */

    /* Get User */
    @Test
    public void testGetNotExistingUser() {
        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().retrieve(HttpRequest.GET("/not-existing-id"))
        );
        assertNotNull(thrown.getResponse());
        assertEquals(NOT_FOUND, thrown.getStatus());
    }
    /* Get User */

    /* Save Location */
    @Test
    public void testSaveUserLocationNotExistingUser() {
        String request = "{\n" +
                "\"userId\": \"2e3b11b0-07a4-4873-8de5-d2ae2eab26b2\",\n" +
                "\"createdOn\": \"2022-02-08T11:44:00.524\",\n" +
                "\"location\": {\n" +
                "\"latitude\": 52.25742342295784,\n" +
                "\"longitude\": 10.540583401747602\n" +
                "}\n" +
                "}";

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(HttpRequest.POST("/locations", request))
        );
        assertNotNull(thrown.getResponse());
        assertEquals(NOT_FOUND, thrown.getStatus());
    }

    @Test
    public void testSaveUserLocationInvalidLongitude() {
        String uuid = UUID.randomUUID().toString();
        UserDto user = UserDto.builder()
                .userId(uuid)
                .createdOn(new Date())
                .email("user@gmail.com")
                .firstName("firstName")
                .secondName("secondName")
                .build();
        HttpResponse<?> userSaveResult = client.toBlocking().exchange(HttpRequest.POST("", user));
        assertEquals(HttpStatus.OK, userSaveResult.status());

        String request = "{\n" +
                "\"userId\": \"" + uuid + "\",\n" +
                "\"createdOn\": \"2022-02-08T11:44:00.524\",\n" +
                "\"location\": {\n" +
                "\"latitude\": 52.25742342295784,\n" +
                "\"longitude\": 10,540583401747602\n" +
                "}\n" +
                "}";

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(HttpRequest.POST("/locations", request))
        );
        assertNotNull(thrown.getResponse());
        assertEquals(BAD_REQUEST, thrown.getStatus());
    }

    @Test
    public void testSaveUserLocationCreatedOnIsNull() {
        String uuid = UUID.randomUUID().toString();
        UserDto user = UserDto.builder()
                .userId(uuid)
                .createdOn(new Date())
                .email("user@gmail.com")
                .firstName("firstName")
                .secondName("secondName")
                .build();
        HttpResponse<?> userSaveResult = client.toBlocking().exchange(HttpRequest.POST("", user));
        assertEquals(HttpStatus.OK, userSaveResult.status());

        String request = "{\n" +
                "\"userId\": \"" + uuid + "\",\n" +
                "\"location\": {\n" +
                "\"latitude\": 52.25742342295784,\n" +
                "\"longitude\": 10.540583401747602\n" +
                "}\n" +
                "}";

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(HttpRequest.POST("/locations", request))
        );
        assertNotNull(thrown.getResponse());
        assertEquals(BAD_REQUEST, thrown.getStatus());
    }

    @Test
    public void testSaveUserLocationIsNull() {
        String uuid = UUID.randomUUID().toString();
        UserDto user = UserDto.builder()
                .userId(uuid)
                .createdOn(new Date())
                .email("user@gmail.com")
                .firstName("firstName")
                .secondName("secondName")
                .build();
        HttpResponse<?> userSaveResult = client.toBlocking().exchange(HttpRequest.POST("", user));
        assertEquals(HttpStatus.OK, userSaveResult.status());

        String request = "{\n" +
                "\"userId\": \"" + uuid + "\",\n" +
                "\"createdOn\": \"2022-02-08T11:44:00.524\"\n" +
                "}";

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(HttpRequest.POST("/locations", request))
        );
        assertNotNull(thrown.getResponse());
        assertEquals(BAD_REQUEST, thrown.getStatus());
    }

    @Test
    public void testSaveUserLocationUserIdIsNull() {
        String uuid = UUID.randomUUID().toString();
        UserDto user = UserDto.builder()
                .userId(uuid)
                .createdOn(new Date())
                .email("user@gmail.com")
                .firstName("firstName")
                .secondName("secondName")
                .build();
        HttpResponse<?> userSaveResult = client.toBlocking().exchange(HttpRequest.POST("", user));
        assertEquals(HttpStatus.OK, userSaveResult.status());

        String request = "{\n" +
                "\"createdOn\": \"2022-02-08T11:44:00.524\",\n" +
                "\"location\": {\n" +
                "\"latitude\": 52.25742342295784,\n" +
                "\"longitude\": 10.540583401747602\n" +
                "}\n" +
                "}";

        HttpClientResponseException thrown = assertThrows(HttpClientResponseException.class, () ->
                client.toBlocking().exchange(HttpRequest.POST("/locations", request))
        );
        assertNotNull(thrown.getResponse());
        assertEquals(BAD_REQUEST, thrown.getStatus());
    }

    @Test
    public void testSaveUserLocation() {
        String uuid = UUID.randomUUID().toString();
        UserDto user = UserDto.builder()
                .userId(uuid)
                .createdOn(new Date())
                .email("user@gmail.com")
                .firstName("firstName")
                .secondName("secondName")
                .build();
        HttpResponse<?> userSaveResult = client.toBlocking().exchange(HttpRequest.POST("", user));
        assertEquals(HttpStatus.OK, userSaveResult.status());

        LocationDto location = LocationDto.builder()
                .userId(uuid)
                .createdOn(new Date())
                .location(new Location(52.25742342295784, 10.540583401747602))
                .build();
        HttpResponse<?> result = client.toBlocking().exchange(HttpRequest.POST("/locations", location));
        assertEquals(HttpStatus.OK, result.status());

        UserLocationDto response = client.toBlocking().retrieve(HttpRequest.GET("/" + uuid), UserLocationDto.class);
        assertEquals(uuid, response.getUserId());
        assertEquals(user.getCreatedOn(), response.getCreatedOn());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getFirstName(), response.getFirstName());
        assertEquals(user.getSecondName(), response.getSecondName());
        assertEquals(location.getLocation().getLatitude(), response.getLocation().getLatitude());
        assertEquals(location.getLocation().getLongitude(), response.getLocation().getLongitude());
    }

    @Test
    public void testUserLocationLatest() {
        String uuid = UUID.randomUUID().toString();
        UserDto user = UserDto.builder()
                .userId(uuid)
                .createdOn(new Date())
                .email("user@gmail.com")
                .firstName("firstName")
                .secondName("secondName")
                .build();
        HttpResponse<?> userSaveResult = client.toBlocking().exchange(HttpRequest.POST("", user));
        assertEquals(HttpStatus.OK, userSaveResult.status());

        // Add 5 locations with different date and coordinates
        long now = System.currentTimeMillis();
        for (int i = 0; i < 5; i++) {
            LocationDto location = LocationDto.builder()
                    .userId(uuid)
                    .createdOn(new Date(now + i * 1000))
                    .location(new Location(10.0 + i, 20.0 + i))
                    .build();
            HttpResponse<?> result = client.toBlocking().exchange(HttpRequest.POST("/locations", location));
            assertEquals(HttpStatus.OK, result.status());
        }

        UserLocationDto response = client.toBlocking().retrieve(HttpRequest.GET("/" + uuid), UserLocationDto.class);
        assertEquals(uuid, response.getUserId());
        assertEquals(user.getCreatedOn(), response.getCreatedOn());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getFirstName(), response.getFirstName());
        assertEquals(user.getSecondName(), response.getSecondName());
        assertEquals(14.0, response.getLocation().getLatitude());
        assertEquals(24.0, response.getLocation().getLongitude());
    }

    @Test
    public void testUserLocations() {
        String uuid = UUID.randomUUID().toString();
        UserDto user = UserDto.builder()
                .userId(uuid)
                .createdOn(new Date())
                .email("user@gmail.com")
                .firstName("firstName")
                .secondName("secondName")
                .build();
        HttpResponse<?> userSaveResult = client.toBlocking().exchange(HttpRequest.POST("", user));
        assertEquals(HttpStatus.OK, userSaveResult.status());

        // Add 10 locations with different date and coordinates
        long timestamp = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            LocationDto location = LocationDto.builder()
                    .userId(uuid)
                    .createdOn(new Date(timestamp + i * 1000))
                    .location(new Location(10.0 + i, 20.0 + i))
                    .build();
            HttpResponse<?> result = client.toBlocking().exchange(HttpRequest.POST("/locations", location));
            assertEquals(HttpStatus.OK, result.status());
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

        // Check with `from` and `to` parameters
        {
            UserLocationsDto response = client.toBlocking().retrieve(
                    HttpRequest.GET(
                            String.format("/%s/locations?from=%s&to=%s",
                                    uuid,
                                    dateFormat.format(new Date(timestamp + 1000L)),
                                    dateFormat.format(new Date(timestamp + 8000))
                            )
                    ), UserLocationsDto.class);
            assertEquals(uuid, response.getUserId());
            assertEquals(8, response.getLocations().size());
            response.getLocations().forEach(location ->
                    assertTrue(
                            location.getCreatedOn().getTime() >= timestamp + 1000L
                                    && location.getCreatedOn().getTime() <= timestamp + 8000L)
            );
        }
        // Check with `from` only
        {
            UserLocationsDto response = client.toBlocking().retrieve(
                    HttpRequest.GET(
                            String.format("/%s/locations?from=%s",
                                    uuid, dateFormat.format(new Date(timestamp + 5000)))
                    ), UserLocationsDto.class);
            assertEquals(uuid, response.getUserId());
            assertEquals(5, response.getLocations().size());
            response.getLocations().forEach(location -> assertTrue(location.getCreatedOn().getTime() >= timestamp + 5000));
        }
        // Check with `to` only
        {
            UserLocationsDto response = client.toBlocking().retrieve(
                    HttpRequest.GET(
                            String.format("/%s/locations?to=%s",
                                    uuid, dateFormat.format(new Date(timestamp + 5000)))
                    ), UserLocationsDto.class);
            assertEquals(uuid, response.getUserId());
            assertEquals(6, response.getLocations().size());
            response.getLocations().forEach(location -> assertTrue(location.getCreatedOn().getTime() <= timestamp + 5000));
        }
        // Check without parameters
        {
            UserLocationsDto response = client.toBlocking().retrieve(
                    HttpRequest.GET(String.format("/%s/locations", uuid)), UserLocationsDto.class);
            assertEquals(uuid, response.getUserId());
            assertEquals(10, response.getLocations().size());
        }
    }
    /* Save Location */

}