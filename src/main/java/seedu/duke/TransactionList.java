package seedu.duke;
import java.util.ArrayList;
//todo: delete
public class TransactionList {
    private final ArrayList<Transaction> transactions = new ArrayList<>();

    public void add(Transaction t) {
        transactions.add(t);
    }
    //returns num of ransactions
    public int size() {
        return transactions.size();
    }
    //can be used for list command later to acquire a particular transaction
    public Transaction get(int i) {
        return transactions.get(i);
    }
}