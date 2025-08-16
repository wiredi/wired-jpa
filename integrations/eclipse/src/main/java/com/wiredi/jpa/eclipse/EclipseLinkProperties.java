package com.wiredi.jpa.eclipse;

import com.wiredi.annotations.properties.Property;
import com.wiredi.annotations.properties.PropertyBinding;
import com.wiredi.runtime.properties.accessor.PropertyAccessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@PropertyBinding(prefix = "eclipselink")
public record EclipseLinkProperties(
        @NotNull
        @Property(defaultValue = "none")
        DdlGeneration ddlGeneration,
        @Nullable
        String weaving
) {
    public Map<String, Object> buildProperties() {
        HashMap<String, Object> props = new HashMap<>();
        props.put("eclipselink.ddl-generation", ddlGeneration.value);
        if (weaving != null) props.put("eclipselink.weaving", weaving);
        return props;
    }

    public enum DdlGeneration {
        NONE("none"),
        CREATE_ONLY("create-tables"),
        DROP_ONLY("drop-tables"),
        DROP_AND_CREATE("drop-and-create-tables"),
        CREATE_OR_EXTEND("create-or-extend-tables"),
        ;

        private final String value;

        DdlGeneration(String value) {
            this.value = value;
        }
    }
}
