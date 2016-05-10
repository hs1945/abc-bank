package com.abc.AccountManager;

import com.abc.Transaction.AccountTransaction;
import com.abc.Transaction.Transaction;
import com.abc.util.DateHelper;

import java.util.*;

import static java.lang.Math.abs;


public abstract class Account {

    private Accounts accountType;
    //created a map, so that it will be easy to find transactions for each month
    //will be useful if user wants to find transactions for particular month
    //and calculate interest for each month separately
    private HashMap<Date, LinkedList<AccountTransaction>> transactions;

    //Keep a track of all the interests calculated monthly in a queue, if one month is skipped
    //can find that month and update it, since we are not using cron jobs
    private LinkedList<interest> monthlyInterests;
    private double interestEarned;
    private double currentBalance;
    private Date openDate;
    private long accountNumber;

    //keep a counter for account number
    private static long accountCounter = 0;

    Account(Accounts accountType) {
        this.accountNumber = ++accountCounter;
        this.accountType = accountType;
        this.transactions = new HashMap<>();
        this.monthlyInterests = new LinkedList<>();
        this.currentBalance = 0;
        this.interestEarned = 0;
        this.openDate = new Date();
    }

    public long getAccountNumber() {
        return accountNumber;
    }

    private void addTransaction(double amount) {
        synchronized (this) {
            //update balance
            currentBalance +=amount;

            //create transaction
            AccountTransaction transaction = new AccountTransaction(amount, currentBalance);

            //find transaction date from the transaction object
            Date transDate = DateHelper.formatDate(transaction.getTransactionDate());

            //update the map containing all transactions
            Date firstDate = DateHelper.getFirstDateOfMonth(transDate);

            if (transactions.containsKey(firstDate))
                transactions.get(firstDate).add(transaction);
            else {
                LinkedList<AccountTransaction> linkedList = new LinkedList<>();
                linkedList.add(transaction);
                transactions.put(firstDate, linkedList);
            }
        }

    }

    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Check the amount of money");
        } else {
            synchronized (this) {
                    addTransaction(amount);
            }
        }
    }

    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Check the amount of money");
        } else {
            synchronized (this) {
                //check if balance exists
                    if (currentBalance < amount)
                        System.err.println("Withdrawal exceeds balance");
                    else {
                        //storing negative value
                        addTransaction(-1 * amount);
                    }
            }
        }
    }

    public Account convertTo(Accounts accountType){
        Account account = null;
        if (accountType == Accounts.CHECKING) {
            account = new CheckingAccount();
            account.accountType = Accounts.CHECKING;
        } else if (accountType == Accounts.SAVINGS) {
            account = new SavingsAccount();
            account.accountType = Accounts.SAVINGS;
        } else if (accountType == Accounts.MAXISAVINGS) {
            account = new MaxiSavingsAccount();
            account.accountType = Accounts.MAXISAVINGS;
        }

        //creating a new object for conversion

        assert account != null;
        account.accountNumber = this.accountNumber;
        account.transactions = this.transactions;
        account.monthlyInterests = this.monthlyInterests;
        account.interestEarned = this.interestEarned;
        account.currentBalance = this.currentBalance;
        account.openDate = this.openDate;

        return account;
    }

    HashMap<Date, LinkedList<AccountTransaction>> getTransactions() {
        return transactions;
    }

    public String printStatement() {
        return printStatement(Calendar.getInstance().getTime());
    }

    private String printStatement(Date date) {

        //print statement for a month
        StringBuilder s = new StringBuilder();

        double total = 0.0;

        LinkedList<AccountTransaction> trans= transactions.get(DateHelper.getFirstDateOfMonth(date));

        for (Transaction t : trans) {
            s.append("  ");
            s.append(t.getAmount() < 0 ? "withdrawal" : "deposit");
            s.append(" ");
            s.append(toDollars(t.getAmount()));
            s.append("\n");
            total += t.getAmount();
        }
        s.append("Total ");
        s.append(toDollars(total));
        s.append("\n");
        return s.toString();
    }

    private String toDollars(double d){
        return String.format("$%,..2f", abs(d));
    }

    private LinkedList<interest> getMonthlyInterests() {
        return monthlyInterests;
    }

    public Accounts getAccountType() {
        return accountType;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public double getInterestEarned() {
        return calculateInterest();
    }

    public double getInterestEarned(Date date) {
        Date currentMonth = DateHelper.getFirstDateOfMonth(date);
        return calculateInterest(currentMonth);
    }

    private double calculateInterest() {
        //since we don't have scheduler, we might have to calculate interests for previous months as well
        Date lastInterestMonth = this.getMonthlyInterests().getLast().getDate();
        Date currentMonth = DateHelper.getFirstDateOfMonth(new Date());
        double interest = 0;
        //loop through all the moths where interest is not generated
        for (Date month = lastInterestMonth; DateHelper.monthsBetween(currentMonth, lastInterestMonth) >= 0; ) {
            double monthInterest = calculateInterest(month);
            interest += monthInterest;
            month = DateHelper.getFirstDateOfNextMonth(month);
        }
        //create a transaction for interest
        addTransaction(interest);
        //add total interest
        interestEarned += interest;

        //create a map entry to check if interest is calculated for the month
        monthlyInterests.add(new interest(currentMonth, interest));
        return interest;
    }

    protected abstract double calculateInterest(Date startDate);

}

class interest{
    private final Date date;
    private final double interest;

    interest(Date date, double interest){
        this.date = date;
        this.interest = interest;
    }

    public Date getDate() {
        return date;
    }

}


