package com.wiredi.jpa.tx;

import com.wiredi.annotations.Provider;
import com.wiredi.annotations.stereotypes.DefaultConfiguration;
import com.wiredi.runtime.domain.conditional.builtin.ConditionalOnMissingBean;
import jakarta.persistence.EntityManagerFactory;

@DefaultConfiguration
public class TransactionDefaultConfiguration {

    @Provider
    @ConditionalOnMissingBean(type = TransactionContext.class)
    public TransactionContext transactionTemplate(EntityManagerFactory entityManagerFactory) {
        return new TransactionContext(entityManagerFactory);
    }
}
