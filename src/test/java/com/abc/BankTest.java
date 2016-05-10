package com.abc;

import com.abc.AccountManager.*;
import com.abc.util.DateHelper;
import org.junit.Test;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class BankTest {
    private static final double DOUBLE_DELTA = 1e-15;

    @Test
    public void customerSummary() {
        Bank bank = new Bank();
        Customer john = new Customer("John");
        john.openAccount(new CheckingAccount());
        bank.addCustomer(john);

        assertEquals("Customer Summary\n - John (1 account)", bank.customerSummary());
    }

    @Test
    public void checkingAccount() {
        Bank bank = new Bank();
        Account checkingAccount = new CheckingAccount();
        Customer bill = new Customer("Bill").openAccount(checkingAccount);
        bank.addCustomer(bill);

        //Depositing money - interest earned - 0.01 on 500
        checkingAccount.deposit(500.0);
        Date today = new Date();
        double actualInterestEarned = bank.totalInterestPaid(DateHelper.getFirstDateOfMonth(today));
        double expectedInterestEarned = 500*Math.pow(1 + (0.01/DateHelper.daysInYear(today)),
                DateHelper.daysBetween(DateHelper.getLastDateOfMonth(today), today)) - 500;
        assertEquals(expectedInterestEarned,actualInterestEarned , DOUBLE_DELTA);

        //withdrawing money - interest earned - 0.01 on 500-200
        checkingAccount.withdraw(200.0);
        actualInterestEarned = bank.totalInterestPaid(DateHelper.getFirstDateOfMonth(today));
        expectedInterestEarned = 300*Math.pow(1 + (0.01/DateHelper.daysInYear(today)),
                DateHelper.daysBetween(DateHelper.getLastDateOfMonth(today), today)) - 300;
        assertEquals(expectedInterestEarned,actualInterestEarned , DOUBLE_DELTA);

        //depositing money - interest earned - 0.01 on 500-200+100
        checkingAccount.deposit(100.0);
        actualInterestEarned = bank.totalInterestPaid(DateHelper.getFirstDateOfMonth(today));
        expectedInterestEarned = 400*Math.pow(1 + (0.01/DateHelper.daysInYear(today)),
                DateHelper.daysBetween(DateHelper.getLastDateOfMonth(today), today)) - 400;
        assertEquals(expectedInterestEarned,actualInterestEarned , DOUBLE_DELTA);
    }

    @Test
    public void savings_account() {
        Bank bank = new Bank();
        Account savingsAccount = new SavingsAccount();
        bank.addCustomer(new Customer("Bill").openAccount(savingsAccount));

        savingsAccount.deposit(1500.0);
        Date today = new Date();

        //deposit money 1500 - interest earned = 0.02
        double actualInterestEarned = bank.totalInterestPaid(DateHelper.getFirstDateOfMonth(today));
        double expectedInterestEarned = 1500*Math.pow(1 + (0.02/DateHelper.daysInYear(today)),
                DateHelper.daysBetween(DateHelper.getLastDateOfMonth(today), today)) - 1500;
        assertEquals(expectedInterestEarned,actualInterestEarned , DOUBLE_DELTA);

        Account savingsAccount2 = new SavingsAccount();
        bank.addCustomer(new Customer("Mill").openAccount(savingsAccount2));

        savingsAccount2.deposit(500.0);
        //deposit money 500 - interest earned = 0.01
        actualInterestEarned = bank.totalInterestPaid(DateHelper.getFirstDateOfMonth(today));
        expectedInterestEarned += 500*Math.pow(1 + (0.01/DateHelper.daysInYear(today)),
                DateHelper.daysBetween(DateHelper.getLastDateOfMonth(today), today)) - 500;
        assertEquals(expectedInterestEarned,actualInterestEarned , DOUBLE_DELTA);

    }

    @Test
    public void changeAccount(){
        Bank bank = new Bank();
        Account savingsAccount = new SavingsAccount();
        bank.addCustomer(new Customer("Bill").openAccount(savingsAccount));

        savingsAccount.deposit(1500.0);
        Date today = new Date();

        //deposit money 1500 in savings account- interest earned = 0.02
        double actualInterestEarned = bank.totalInterestPaid(DateHelper.getFirstDateOfMonth(today));
        double expectedInterestEarned = 1500*Math.pow(1 + (0.02/DateHelper.daysInYear(today)),
                DateHelper.daysBetween(DateHelper.getLastDateOfMonth(today), today)) - 1500;
        assertEquals(expectedInterestEarned,actualInterestEarned , DOUBLE_DELTA);

        //Change account to Checking account
        List<Account> accounts = bank.getCustomer(1).getAccounts();
        Account account = accounts.get(0);
        Account converted = account.convertTo(Accounts.CHECKING);
        accounts.set(0,converted);

        //checking account - interest earned = 0.01
        actualInterestEarned = bank.totalInterestPaid(DateHelper.getFirstDateOfMonth(today));
        expectedInterestEarned = 1500*Math.pow(1 + (0.01/DateHelper.daysInYear(today)),
                DateHelper.daysBetween(DateHelper.getLastDateOfMonth(today), today)) - 1500;
        assertEquals(expectedInterestEarned,actualInterestEarned , DOUBLE_DELTA);

    }


    @Test
    public void maxi_savings_account() {
        Bank bank = new Bank();
        Account account = new MaxiSavingsAccount();
        bank.addCustomer(new Customer("Bill").openAccount(account));

        account.deposit(3000.0);

        Date today = new Date();

        double actualInterestEarned = bank.totalInterestPaid(DateHelper.getFirstDateOfMonth(today));
        double expectedInterestEarned = 3000*Math.pow(1 + (0.1/DateHelper.daysInYear(today)),
                DateHelper.daysBetween(DateHelper.getLastDateOfMonth(today), today)) - 3000;
        assertEquals(expectedInterestEarned,actualInterestEarned , DOUBLE_DELTA);

        Account account2 = new MaxiSavingsAccount();
        bank.addCustomer(new Customer("Mill").openAccount(account2));

        account2.deposit(500.0);

        actualInterestEarned = bank.totalInterestPaid(DateHelper.getFirstDateOfMonth(today));
        expectedInterestEarned += 500*Math.pow(1 + (0.02/DateHelper.daysInYear(today)),
                DateHelper.daysBetween(DateHelper.getLastDateOfMonth(today), today)) - 500;
        assertEquals(expectedInterestEarned,actualInterestEarned , DOUBLE_DELTA);

        Account account3 = new MaxiSavingsAccount();
        bank.addCustomer(new Customer("Mill").openAccount(account3));

        account3.deposit(1500.0);

        actualInterestEarned = bank.totalInterestPaid(DateHelper.getFirstDateOfMonth(today));
        expectedInterestEarned += 1500*Math.pow(1 + (0.05/DateHelper.daysInYear(today)),
                DateHelper.daysBetween(DateHelper.getLastDateOfMonth(today), today)) - 1500;
        assertEquals(expectedInterestEarned,actualInterestEarned , DOUBLE_DELTA);
    }

}
