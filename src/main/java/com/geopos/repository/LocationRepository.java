package com.geopos.repository;

import com.geopos.db.Location;
import com.geopos.entity.LocationDateDto;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.Query;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.jdbc.runtime.JdbcOperations;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@JdbcRepository(dialect = Dialect.H2)
public abstract class LocationRepository implements CrudRepository<Location, Long> {

    private final JdbcOperations jdbcOperations;

    public LocationRepository(JdbcOperations jdbcOperations) {
        this.jdbcOperations = jdbcOperations;
    }

    @Query("SELECT created_on, latitude, longitude FROM location l WHERE l.created_on = " +
            "(SELECT MAX(created_on) FROM location WHERE user_id = :userId)")
    public abstract Optional<LocationDateDto> findMaxCreatedOnByUserId(long userId);

    @Transactional
    public List<LocationDateDto> findByUserIdAndTimeRange(long userId, @Nullable Date fromDate, @Nullable Date toDate) {
        String sqlQuery = "SELECT created_on,latitude,longitude FROM location AS l WHERE l.user_id = ?";
        if (fromDate != null) {
            sqlQuery += " AND created_on >= ?";
        }
        if (toDate != null) {
            sqlQuery += " AND created_on <= ?";
        }
        return jdbcOperations.prepareStatement(sqlQuery, statement -> {
                    int idx = 0;
                    statement.setLong(++idx, userId);
                    if (fromDate != null) {
                        statement.setTimestamp(++idx, new java.sql.Timestamp(fromDate.getTime()));
                    }
                    if (toDate != null) {
                        statement.setTimestamp(++idx, new java.sql.Timestamp(toDate.getTime()));
                    }
                    ResultSet resultSet = statement.executeQuery();
                    return jdbcOperations.entityStream(resultSet, LocationDateDto.class).collect(Collectors.toList());
                }
        );
    }

}
