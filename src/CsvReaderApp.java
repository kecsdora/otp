import model.Customer;
import model.Payment;
import model.PaymentType;

import java.io.*;
import java.util.*;

public class CsvReaderApp {

    private static final String CUSTOMER_FILE = "src/files/customer.csv";
    private static final String PAYMENTS_FILE = "src/files/payments.csv";
    private static final String LOG_FILE = "src/files/application.log";
    private static final String REPORT01 = "src/files/report01.csv";
    private static final String TOP = "src/files/top.csv";
    private static final String REPORT02 = "src/files/report02.csv";

    public static void main(String[] args) {

        List<Customer> customers = readCustomers();
        List<Payment> payments = readPayments();

        Map<String, Integer> customersTotalAmount = getAmountOfCustomer(customers, payments);

        getTopAmounts(customersTotalAmount);

        getAllPaymentsOfWebshops(payments);
    }

    public static List<Customer> readCustomers() {
        String line;
        String splitBy = ";";
        List<Customer> customers = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(CUSTOMER_FILE));
             FileWriter customerErrorLines = new FileWriter(LOG_FILE)) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(splitBy);
                if (data.length != 4) {
                    customerErrorLines.write("Incorrect number of fields in customer.csv: " + line + "\n");
                    continue;
                }
                if (!checkCustomerFields(data)) {
                    customerErrorLines.write("Mandatory field(s) are empty in customer.csv: " + line + "\n");
                    continue;
                }
                Customer customer = new Customer(data[0], data[1], data[2], data[3]);
                customers.add(customer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return customers;
    }

    public static List<Payment> readPayments() {
        String line;
        String splitBy = ";";
        List<Payment> payments = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(PAYMENTS_FILE));
             FileWriter paymentErrorLines = new FileWriter(LOG_FILE, true)) {
            while ((line = br.readLine()) != null) {
                String[] data = line.split(splitBy);

                if (data.length != 7) {
                    paymentErrorLines.write("Incorrect number of fields in payments.csv: " + line + "\n");
                    continue;
                }
                Payment payment = new Payment(data[0], data[1], data[4], data[5], data[6]);
                payment.setPaymentType("card".equals(data[2]) ? PaymentType.CARD : PaymentType.TRANSFER);
                payment.setAmount(Integer.parseInt(data[3]));

                if (!checkPaymentFields(payment)) {
                    paymentErrorLines.write("Mandatory field(s) are empty in payments.csv: " + line + "\n");
                    continue;
                }
                payments.add(payment);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return payments;
    }


    public static boolean checkCustomerFields(String[] data) {
        for (String d : data) {
            if (d.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkPaymentFields(Payment payment) {
        if (payment.getWebshopId().isEmpty() || payment.getCustomerId().isEmpty()
                || payment.getPaymentType().getValue().isEmpty() || payment.getDateOfPayment().isEmpty()) {
            return false;
        }
        if (payment.getPaymentType().equals(PaymentType.CARD) && payment.getCreditCardNumber().isEmpty()
                || payment.getPaymentType().equals(PaymentType.TRANSFER) && payment.getAccountNumber().isEmpty()) {
            return false;
        }
        return true;
    }

    public static  Map<String, Integer> getAmountOfCustomer(List<Customer> customers, List<Payment> payments) {
        Map<String, Integer> customersTotalAmount = new HashMap<>();

        for (Payment payment : payments) {
            Optional<Customer> customer = customers.stream()
                    .filter(c -> c.getWebshopId().equals(payment.getWebshopId()) && c.getCustomerId().equals(payment.getCustomerId()))
                    .findFirst();

            if (customer.isPresent()) {
                String customerKey = customer.get().getName() + " - " + customer.get().getAddress();
                customersTotalAmount.put(customerKey, customersTotalAmount.getOrDefault(customerKey, 0) + payment.getAmount());
            }
        }

        try (FileWriter report01 = new FileWriter(REPORT01)) {
            customersTotalAmount.forEach((customer, totalAmount) -> {
                try {
                    report01.write(customer + ";" + totalAmount + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return customersTotalAmount;
    }

    public static void getTopAmounts(Map<String, Integer> customersTotalAmount) {
        List<Map.Entry<String, Integer>> sortedTotalAmount = new ArrayList<>(customersTotalAmount.entrySet());
        sortedTotalAmount.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        try (FileWriter top = new FileWriter(TOP)) {
            if (sortedTotalAmount.size() > 0) {
                Map.Entry<String, Integer> firstLargest = sortedTotalAmount.get(0);
                top.write(firstLargest.getKey() + ";" + firstLargest.getValue() + "\n");
            }
            if (sortedTotalAmount.size() > 1) {
                Map.Entry<String, Integer> secondLargest = sortedTotalAmount.get(1);
                top.write(secondLargest.getKey() + ";" + secondLargest.getValue() + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getAllPaymentsOfWebshops(List<Payment> payments) {
        Map<String, int[]> paymentsOfWebshops = new HashMap<>();
        for (Payment payment : payments) {
            String webshop = payment.getWebshopId();
            int[] amounts = paymentsOfWebshops.getOrDefault(webshop, new int[2]);
            if (PaymentType.CARD.equals(payment.getPaymentType())) {
                amounts[0] += payment.getAmount();
            } else if (PaymentType.TRANSFER.equals(payment.getPaymentType())) {
                amounts[1] += payment.getAmount();
            }
            paymentsOfWebshops.put(webshop, amounts);
        }
        try (FileWriter report02 = new FileWriter(REPORT02)) {
            for (Map.Entry<String, int[]> entry : paymentsOfWebshops.entrySet()) {
                String webShop = entry.getKey();
                int[] amounts = entry.getValue();
                report02.write(webShop + ";" + amounts[0] + ";" + amounts[1] + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}