package com.geopos.db;

import io.micronaut.data.annotation.MappedEntity;
import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;


/**
 * User DB entity.
 */
@MappedEntity(value = "user")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class User {
    @Id
    @GeneratedValue
    Long id;       // internal id
    String userId; // external Id
    Instant createdOn;
    String email;
    String firstName;
    String secondName;
}
