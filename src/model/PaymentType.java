package model;

public enum PaymentType {
    CARD("card"),
    TRANSFER("transfer");

    private String value;

    PaymentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
