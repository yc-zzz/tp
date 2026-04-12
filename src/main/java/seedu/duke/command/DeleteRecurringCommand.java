package seedu.duke.command;

import seedu.duke.MoneyBagProMaxException;
import seedu.duke.budget.Budget;
import seedu.duke.transaction.RecurringTransaction;
import seedu.duke.transactionlist.RecurringTransactionList;
import seedu.duke.transactionlist.TransactionList;
import seedu.duke.ui.Ui;

public class DeleteRecurringCommand extends Command {

    private final int targetIndex;
    private final RecurringTransactionList recurringList;

    public DeleteRecurringCommand(int targetIndex, RecurringTransactionList recurringList) {
        assert recurringList != null : "RecurringTransactionList should not be null";
        this.targetIndex = targetIndex;
        this.recurringList = recurringList;
    }

    @Override
    public void execute(TransactionList list, Budget budget, Ui ui) throws MoneyBagProMaxException {
        if (recurringList.isEmpty()) {
            throw new MoneyBagProMaxException("No recurring transactions to delete.");
        }
        int listIndex = targetIndex - 1;
        if (listIndex < 0 || listIndex >= recurringList.size()) {
            throw new MoneyBagProMaxException(
                    "Invalid index. Please provide a number between 1 and " + recurringList.size());
        }
        RecurringTransaction removed = recurringList.remove(listIndex);
        ui.showMessage("Deleted recurring: " + removed);
    }

    @Override
    public boolean isMutatingRecurring() {
        return true;
    }
}
