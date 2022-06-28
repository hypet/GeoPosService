## Geo Position Data Collector Service

Service collects user's location (longitude, latitude).

Tech stack: Micronaut 3.4.1, H2 DB, JUnit 5, Mockito, Flyway.

### API Methods

`POST /v1/users/` Save/update user data. Sample JSON:

```json
{
  "userId": "00000001-1111-1111-1234-123456789012",
  "createdOn": "2022-06-27T12:00:00.123",
  "email": "user@mail.com",
  "firstName": "Bob",
  "secondName": "Miller"
}
```

`GET /v1/users/{userId}` Get user data with the latest location. Sample response:

```json
{
  "userId": "00000001-1111-1111-1234-123456789012",
  "createdOn": "2022-06-27T12:00:00.123",
  "email": "user@mail.com",
  "firstName": "Bob",
  "secondName": "Miller",
  "location": {
    "latitude": 32.15742342295784,
    "longitude": 13.440583401747602
  }
}
```

`POST /v1/users/locations/` Save user's location. Sample JSON:

```json
{
  "userId": "00000001-1111-1111-1234-123456789012",
  "createdOn": "2022-06-27T12:10:10.123",
  "location": {
    "latitude": 32.15742342295784,
    "longitude": 13.440583401747602
  }
}
```

`GET /v1/users/{userId}/locations` Get user's locations. Sample response:

```json
{
  "userId": "00000001-1111-1111-1234-123456789012",
  "locations": [
    {
      "createdOn": "2022-06-27T12:10:10.123",
      "location": {
        "latitude": 32.15742342295784,
        "longitude": 13.440583401747602
      }
    },
    {
      "createdOn": "2022-06-28T12:10:10.123",
      "location": {
        "latitude": 35.00742342295784,
        "longitude": 17.000583401747602
      }
    }
  ]
}
```

### Run service

`./gradlew run`

Service will be available at http://localhost:8080/ 

### Run tests

`./gradlew test`