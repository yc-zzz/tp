package seedu.duke.command;

import seedu.duke.transactionlist.TransactionList;
import seedu.duke.ui.Ui;

public class ListCommand extends Command {
    @Override
    public void execute(TransactionList list, Ui ui) {
        assert list != null : "TransactionList should not be null.";
        ui.showList(list);
    }
    
    @Override
    public boolean isMutating() {
        return true;
    }
}
