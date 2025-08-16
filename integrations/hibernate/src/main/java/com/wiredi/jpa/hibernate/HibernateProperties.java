package com.wiredi.jpa.hibernate;

import com.wiredi.annotations.properties.Property;
import com.wiredi.annotations.properties.PropertyBinding;
import org.hibernate.tool.schema.Action;
import org.jetbrains.annotations.Nullable;

@PropertyBinding(prefix = "hibernate")
public record HibernateProperties(
        @Nullable
        String dialect,
        @Property(defaultValue = "none")
        Action ddlAuto,
        @Property(defaultValue = "false")
        boolean showSql,
        @Property(defaultValue = "false")
        boolean formatSql
) {
}
