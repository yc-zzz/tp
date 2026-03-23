package seedu.duke.transactionlist;

import seedu.duke.transaction.Transaction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/**
 *  Manages the list of transactions stored in the program.
 *  This class provides operations for adding, retrieving,
 *  removing and checking transactions in the list.
 */
public class TransactionList {

    private static final Logger logger = Logger.getLogger(TransactionList.class.getName());
    private final ArrayList<Transaction> transactions = new ArrayList<>();

    /**
     * Adds a transaction to the list.
     *
     * Defensive checks ensures that null transactions are not added,
     * which would cause unexpected behaviour.
     *
     * @param t Transaction object to add to list
     */
    public void add(Transaction t) {
        assert t != null : "Transaction should not be null";
        logger.info("Adding transaction: " + t);
        transactions.add(t);
    }

    /**
     * Returns the number of transactions currently stored.
     *
     * @return the size of the transaction list
     */
    public int size() {
        return transactions.size();
    }

    /**
     * Checks whether the transaction list is empty.
     *
     * @return true if the list contains no transactions
     */
    public boolean isEmpty() {
        return transactions.isEmpty();
    }

    /**
     * Retrieves a transaction at the specified index.
     *
     * Defensive programming ensures the index is within valid bounds.
     * This prevents runtime errors caused by invalid access.
     *
     * @param i index of the transaction
     * @return the transaction at the specified index
     */
    public Transaction get(int i) {
        assert i >= 0 && i < transactions.size() : "Index is out of bounds";
        return transactions.get(i);
    }

    /**
     * Removes a transaction at the specified index.
     *
     * Defensive check ensures the index is valid before attempting removal.
     * Logging helps trace transaction deletion during debugging.
     *
     * @param i index of the transaction to remove
     * @return the removed transaction
     */
    public Transaction remove(int i) {
        assert i >= 0 && i < transactions.size() : "Index is out of bounds";
        logger.info("Removing transaction at index: " + i);
        return transactions.remove(i);
    }

    /**
     * Inserts a transaction at the specified index, shifting subsequent elements right.
     *
     * @param index position at which to insert
     * @param t     the transaction to insert
     */
    public void insert(int index, Transaction t) {
        assert index >= 0 && index <= transactions.size() : "Insert index is out of bounds";
        assert t != null : "Transaction should not be null";
        logger.info("Inserting transaction at index " + index + ": " + t);
        transactions.add(index, t);
    }

    /**
     * Returns a new list containing all transactions sorted by the given comparator.
     * The original list order is not modified.
     *
     * @param comparator the comparator to determine sort order
     * @return a new sorted list of transactions
     */
    public List<Transaction> getSortedList(Comparator<Transaction> comparator) {
        assert comparator != null : "Comparator should not be null";
        ArrayList<Transaction> sorted = new ArrayList<>(transactions);
        sorted.sort(comparator);
        return sorted;
    }
}
