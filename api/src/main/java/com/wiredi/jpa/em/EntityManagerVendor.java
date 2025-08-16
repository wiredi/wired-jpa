package com.wiredi.jpa.em;

import jakarta.persistence.EntityManagerFactory;

public interface EntityManagerVendor {

    EntityManagerFactory getEntityManager(
            EntityManagerFactoryContext context
    );

}
