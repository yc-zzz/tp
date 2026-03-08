package seedu.duke;

public class Expense extends Transaction {
    public Expense(String category, double amount) {
        super(category, amount);
    }

    //not needed for now, can be used for specific type of category later
    @Override
    public String getType() {
        return "expense";
    }

    @Override
    public String toString() {
        return String.format("[Expense] %s $%.2f", category, amount); //$%.2f is floating point for 2dp
    }
}
