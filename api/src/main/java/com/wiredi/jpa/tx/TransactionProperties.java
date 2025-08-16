package com.wiredi.jpa.tx;

public record TransactionProperties(
        boolean readOnly,
        TransactionPropagation propagation,
        TransactionIsolation isolation
) {

    public static final TransactionProperties DEFAULT = new TransactionProperties(false, TransactionPropagation.SUPPORTED, TransactionIsolation.NONE);

    public static Builder builder() {
        return new Builder(DEFAULT);
    }

    public static Builder builder(TransactionProperties base) {
        return new Builder(base);
    }

    public static class Builder {
        private boolean readOnly;
        private TransactionPropagation propagation;
        private TransactionIsolation isolation;

        public Builder(TransactionProperties base) {
            this.readOnly = base.readOnly;
            this.propagation = base.propagation;
            this.isolation = base.isolation;
        }

        public Builder readOnly(boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        public Builder propagation(TransactionPropagation propagation) {
            this.propagation = propagation;
            return this;
        }

        public Builder isolation(TransactionIsolation isolation) {
            this.isolation = isolation;
            return this;
        }

        public TransactionProperties build() {
            return new TransactionProperties(readOnly, propagation, isolation);
        }
    }
}
