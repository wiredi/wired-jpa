package com.wiredi.jpa.tx;

import com.wiredi.jpa.tx.exception.InactiveTransactionException;
import com.wiredi.runtime.lang.ThrowingConsumer;
import com.wiredi.runtime.lang.ThrowingFunction;
import com.wiredi.runtime.lang.ThrowingRunnable;
import com.wiredi.runtime.lang.ThrowingSupplier;
import org.jetbrains.annotations.NotNull;

public interface Transaction {

    default <E extends Exception> void run(@NotNull ThrowingRunnable<E> runnable) throws E {
        run(TransactionProperties.DEFAULT, (status) -> runnable.run());
    }

    default <E extends Exception> void run(@NotNull TransactionProperties properties, @NotNull ThrowingRunnable<E> runnable) throws E {
        run(properties, (status) -> runnable.run());
    }

    default <E extends Exception> void run(@NotNull ThrowingConsumer<@NotNull TransactionStatus, E> consumer) throws E {
        run(TransactionProperties.DEFAULT, consumer);
    }

    <E extends Exception> void run(@NotNull TransactionProperties properties, @NotNull ThrowingConsumer<@NotNull TransactionStatus, E> consumer) throws E;

    <E extends Exception> void runInCurrent(@NotNull ThrowingConsumer<@NotNull TransactionStatus, E> consumer) throws E, InactiveTransactionException;

    @NotNull
    default <T, E extends Exception> T call(@NotNull ThrowingSupplier<@NotNull T, E> supplier) throws E {
        return call(TransactionProperties.DEFAULT, (status) -> supplier.get());
    }

    @NotNull
    default <T, E extends Exception> T call(@NotNull TransactionProperties properties, @NotNull ThrowingSupplier<@NotNull T, E> supplier) throws E {
        return call(properties, (status) -> supplier.get());
    }

    @NotNull
    default <T, E extends Exception> T call(@NotNull ThrowingFunction<@NotNull TransactionStatus, @NotNull T, E> function) throws E {
        return call(TransactionProperties.DEFAULT, function);
    }

    @NotNull
    <T, E extends Exception> T call(@NotNull TransactionProperties properties, @NotNull ThrowingFunction<@NotNull TransactionStatus, @NotNull T, E> function) throws E;

    @NotNull
    <T, E extends Exception> T callInCurrent(@NotNull ThrowingFunction<@NotNull TransactionStatus, @NotNull T, E> function) throws E, InactiveTransactionException;
}
