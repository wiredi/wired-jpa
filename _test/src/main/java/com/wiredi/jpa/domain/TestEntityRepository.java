package com.wiredi.jpa.domain;

import com.wiredi.jpa.JpaRepository;
import com.wiredi.jpa.annotations.ParameterStrategy;
import com.wiredi.jpa.annotations.Query;
import com.wiredi.jpa.annotations.Repository;

import java.util.List;

@Repository
public interface TestEntityRepository extends JpaRepository<TestEntity, String> {

    @Query("select e from Test e")
    void selectVoid();

    @Query("select e from Test e")
    List<TestEntity> selectEntities();

    @Query("select e from Test e where e.name = :name")
    List<TestEntity> findByName(String name);

    @Query("select e from Test e where e.name = :name")
    TestEntity findSingleByName(String name);

    @Query("update Test e set e.name = :name where e.id = :id")
    void setName(String id);

    @Query(value = "select * from test_entities", nativeQuery = true)
    void nativeQueryTest();

    @Query(value = "select e from Test e where e.name = :name and e.description = :description", parameterStrategy = ParameterStrategy.NAMED)
    void multipleNamedParameters(String description, String name);

    @Query(value = "select e from Test e where e.description = ?0 and e.name = ?1", parameterStrategy = ParameterStrategy.POSITIONAL)
    void multiplePositionalParameters(String description, String name);

}
