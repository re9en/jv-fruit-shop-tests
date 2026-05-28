package model;

import java.util.Objects;

public class FruitTransaction {

    public enum Operation {
        BALANCE("b"),
        SUPPLY("s"),
        PURCHASE("p"),
        RETURN("r");

        private final String code;

        Operation(String code) {
            this.code = code;
        }

        public static Operation from(String value) {
            for (Operation a : values()) {
                if (a.code.equals(value)) {
                    return a;
                }
            }
            throw new IllegalArgumentException("Unknown operation: " + value);
        }
    }

    private final Operation operation;
    private final String fruit;
    private final int quantity;

    public FruitTransaction(Operation operation, String fruit, int quantity) {
        this.operation = operation;
        this.fruit = fruit;
        this.quantity = quantity;
    }

    public Operation getOperation() {
        return operation;
    }

    public String getFruit() {
        return fruit;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FruitTransaction that = (FruitTransaction) o;

        return quantity == that.quantity
                && operation == that.operation
                && fruit.equals(that.fruit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(operation, fruit, quantity);
    }
}
