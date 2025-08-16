package com.wiredi.jpa;

import com.wiredi.annotations.ActiveProfiles;
import com.wiredi.jpa.domain.TestEntity;
import com.wiredi.jpa.domain.TestEntityRepository;
import com.wiredi.tests.ApplicationTest;
import com.wiredi.tests.instance.Standalone;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ApplicationTest
@Standalone
@ActiveProfiles("test")
public class IntegrationTest {
    @Test
    public void test(TestEntityRepository repository) {
        // Arrange
        TestEntity testEntity = new TestEntity().setName("Test");

        // Act
        repository.save(testEntity);
        List<TestEntity> foundList = repository.findByName("Test");
        TestEntity foundSingle = repository.findSingleByName("Test");

        // Assert
        assertEquals(testEntity, foundSingle);
        assertEquals(1, foundList.size());
        assertEquals(testEntity, foundList.getFirst());
    }
}
