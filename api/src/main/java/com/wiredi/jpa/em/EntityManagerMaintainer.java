package com.wiredi.jpa.em;

import com.wiredi.annotations.Wire;
import com.wiredi.runtime.WireContainer;
import com.wiredi.runtime.async.StateFull;
import com.wiredi.runtime.async.state.ModifiableState;
import com.wiredi.runtime.async.state.State;
import com.wiredi.runtime.domain.Eager;
import com.wiredi.runtime.domain.conditional.builtin.ConditionalOnProperty;
import jakarta.persistence.EntityManagerFactory;
import org.jetbrains.annotations.NotNull;

@Wire
@ConditionalOnProperty(
        key = "wiredi.jpa.entity-manager-factory.maintain",
        havingValue = "true",
        matchIfMissing = true
)
public class EntityManagerMaintainer implements StateFull<EntityManagerFactory>, Eager {

    private final ModifiableState<EntityManagerFactory> state = State.empty();

    @Override
    public void setup(WireContainer wireRepository) {
        state.set(wireRepository.get(EntityManagerFactory.class));
    }

    @Override
    public @NotNull State<EntityManagerFactory> getState() {
        return state;
    }

    @Override
    public void dismantleState() {
        state.ifPresent(EntityManagerFactory::close);
        state.clear();
    }
}
