package com.abc;

import java.util.*;

class Bank {
    private final Map<Long, Customer> customers;
    private static long customerId = 0;

    Bank() {
        customers = new HashMap<>();
    }

    void addCustomer(Customer customer) {
        customerId++;
        customers.put(customerId, customer);
    }

    String customerSummary() {
        String summary = "Customer Summary";
        for (long l : customers.keySet())
            summary += "\n - " + customers.get(l).getName() + " (" + format(customers.get(l).getNumberOfAccounts(), "account") + ")";
        return summary;
    }

    //Make sure correct plural of word is created based on the number passed in:
    //If number passed in is 1 just return the word otherwise add an 's' at the end
    private String format(int number, String word) {
        return number + " " + (number == 1 ? word : word + "s");
    }

    public double totalInterestPaid() {
        double total = 0;
        for (Long id : customers.keySet())
            total += customers.get(id).totalInterestEarned();
        return total;
    }

    double totalInterestPaid(Date date) {
        double total = 0;
        for (Long id : customers.keySet())
            total += customers.get(id).totalInterestEarned(date);
        return total;
    }

/*
    public String getFirstCustomer() {
        try {
            customers = null;
            return customers.get(0).getName();
        } catch (Exception e){
            e.printStackTrace();
            return "Error";
        }
    }
*/

    Customer getCustomer(long id){
        return customers.get(id);
    }
}
