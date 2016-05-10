package com.abc;

import com.abc.AccountManager.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Math.abs;

class Customer {
    private long id;
    private final String name;
    private final List<Account> accounts;


    Customer(String name) {
        this.name = name;
        this.accounts = new ArrayList<>();
    }

    List<Account> getAccounts() {
        return accounts;
    }

    String getName() {
        return name;
    }

    Customer openAccount(Account account) {
        accounts.add(account);
        return this;
    }

    int getNumberOfAccounts() {
        return accounts.size();
    }

    double totalInterestEarned() {
        double total = 0;
        for (Account a : accounts)
            total += a.getInterestEarned();
        return total;
    }

    double totalInterestEarned(Date date) {
        double total = 0;
        for (Account a : accounts)
            total += a.getInterestEarned(date);
        return total;
    }

    String getStatement() {
        StringBuilder statement = new StringBuilder();


        statement.append("Statement for ");
        statement.append(name);
        statement.append("\n");

        double total = 0.0;
        for (Account a : accounts) {
            statement.append(a.getAccountType());
            statement.append("\n");
            statement.append(a.printStatement());
            statement.append("\n");
            total += a.getCurrentBalance();
        }
        statement.append("\nTotal In All Accounts ");
        statement.append(String.format("$%,.2f", abs(total)));
        return statement.toString();
    }


}
