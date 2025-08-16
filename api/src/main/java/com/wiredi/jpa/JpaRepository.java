package com.wiredi.jpa;

import java.util.Optional;

public interface JpaRepository<T, ID> {
    Optional<T> find(ID id);

    <S extends T> S save(S entity);

    <S extends T> Iterable<S> saveAll(Iterable<S> entities);
}
