package com.geopos.entity;

import io.micronaut.core.annotation.Introspected;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Introspected
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class UserLocationDto {
    String userId;
    Date createdOn;
    String email;
    String firstName;
    String secondName;
    Location location;
}
