package model;

public class Payment {

    private String webshopId;
    private String customerId;
    private PaymentType paymentType;
    private int amount;
    private String accountNumber;
    private String creditCardNumber;
    private String dateOfPayment;

    public Payment(String webshopId, String customerId, String accountNumber, String creditCardNumber, String dateOfPayment) {
        this.webshopId = webshopId;
        this.customerId = customerId;
        this.accountNumber = accountNumber;
        this.creditCardNumber = creditCardNumber;
        this.dateOfPayment = dateOfPayment;
    }

    public String getWebshopId() {
        return webshopId;
    }

    public void setWebshopId(String webshopId) {
        this.webshopId = webshopId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getDateOfPayment() {
        return dateOfPayment;
    }

    public void setDateOfPayment(String dateOfPayment) {
        this.dateOfPayment = dateOfPayment;
    }

}
