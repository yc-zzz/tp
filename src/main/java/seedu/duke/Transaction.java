package seedu.duke;

/**
 * Class to be extended for use for income and expense
 */
public abstract class Transaction {

    protected final String category;
    protected final double amount;

    public Transaction(String category, double amount) {
        this.category = category;
        this.amount = amount;
    }

    //can be used for list command
    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public abstract String getType();
}
