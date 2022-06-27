package com.geopos.db;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import io.micronaut.data.annotation.MappedEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;


/**
 * User's Location DB entity.
 */
@MappedEntity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class Location {
    @Id
    @GeneratedValue(GeneratedValue.Type.IDENTITY)
    long id;
    long userId; // internal userId
    Instant createdOn;
    double latitude;
    double longitude;
}
