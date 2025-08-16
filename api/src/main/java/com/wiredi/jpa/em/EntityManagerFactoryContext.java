package com.wiredi.jpa.em;

import javax.sql.DataSource;
import java.util.List;

public record EntityManagerFactoryContext(
        DataSource dataSource,
        List<EntityMetadataProvider<?>> entityMetadataProviders
) {
}
