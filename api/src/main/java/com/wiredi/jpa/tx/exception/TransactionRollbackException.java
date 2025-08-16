package com.wiredi.jpa.tx.exception;

public class TransactionRollbackException extends RuntimeException {
    public TransactionRollbackException(Throwable cause) {
        super(cause);
    }

    public TransactionRollbackException() {}
}
