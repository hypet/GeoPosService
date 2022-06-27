package com.geopos.repository;

import com.geopos.db.User;
import io.micronaut.context.annotation.Executable;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.data.jdbc.annotation.JdbcRepository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;

import java.util.Optional;

@JdbcRepository(dialect = Dialect.H2)
public interface UserRepository extends CrudRepository<User, Long> {

    @Executable
    Optional<Long> findIdByUserId(@NonNull String userId);

    @Executable
    Optional<User> findByUserId(@NonNull String userId);

}