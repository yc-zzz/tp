package seedu.duke.command;

import seedu.duke.transaction.Expense;
import seedu.duke.transaction.Income;
import seedu.duke.transactionlist.TransactionList;
import seedu.duke.ui.Ui;

import java.time.LocalDate;

public class AddCommand extends Command {
    private final String category;
    private final double amount;
    private final String description;
    private final LocalDate date;

    public AddCommand(String category, double amount, String description, LocalDate date) {
        assert category != null && !category.isBlank() : "Category should not be null or blank";
        assert amount > 0 : "Amount should be positive";
        assert description != null : "Description should not be null";
        assert date != null : "Date should not be null";
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    @Override
    public void execute(TransactionList list, Ui ui) {
        if (Income.VALID_CATEGORIES.contains(category.toLowerCase())) {
            Income income = new Income(category, amount, description, date);
            list.add(income);
            ui.showMessage("Added: " + income);
        } else if (Expense.VALID_CATEGORIES.contains(category.toLowerCase())) {
            Expense expense = new Expense(category, amount, description, date);
            list.add(expense);
            ui.showMessage("Added: " + expense);
        } else {
            ui.showMessage("Invalid category '" + category + "'."
                                   + " Valid expense categories: " + Expense.VALID_CATEGORIES
                                   + " Valid income categories: " + Income.VALID_CATEGORIES);
        }
    }

}
