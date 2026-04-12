package seedu.duke.command;

import seedu.duke.MoneyBagProMaxException;
import seedu.duke.budget.Budget;
import seedu.duke.category.CategoryManager;
import seedu.duke.transaction.Expense;
import seedu.duke.transaction.Income;
import seedu.duke.transaction.Transaction;
import seedu.duke.transactionlist.TransactionList;
import seedu.duke.ui.Ui;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;

public class SummaryCommand extends Command {
    private final String summaryType;
    private String targetMonth;

    public SummaryCommand(String summaryType) {
        this(summaryType, null);
    }

    public SummaryCommand(String summaryType, String targetMonth) {
        assert summaryType != null : "Summary type should not be null.";
        this.summaryType = summaryType.toLowerCase();
        this.targetMonth = targetMonth;
    }

    /**
     * Calculates and displays the summary of transactions based on the requested category.
     *
     * @param list  The current list of transactions.
     * @param ui    The ui instance to display results.
     */
    @Override
    public void execute(TransactionList list, Budget budget, Ui ui) throws MoneyBagProMaxException {
        assert list != null : "List should not be null.";

        // Resolve the month filter up-front
        YearMonth parsedYearMonth = null;
        if (targetMonth != null) {
            try {
                parsedYearMonth = YearMonth.parse(targetMonth);
            } catch (DateTimeParseException e) {
                throw new MoneyBagProMaxException(
                        "Invalid month format! Please use YYYY-MM (e.g., 2026-04).");
            }
        }

        // For category-type summaries, validate the category exists before
        // iterating — this lets us give a precise "not found" message.
        boolean isCategorySummary = isCategoryType(summaryType);
        if (isCategorySummary) {
            CategoryManager cm = CategoryManager.getInstance();
            boolean isBuiltInExpense = Expense.VALID_CATEGORIES.stream()
                    .anyMatch(c -> c.equalsIgnoreCase(summaryType));
            boolean isBuiltInIncome = Income.VALID_CATEGORIES.stream()
                    .anyMatch(c -> c.equalsIgnoreCase(summaryType));
            boolean isCustom = cm.getAllExpenseCategories().stream()
                    .anyMatch(c -> c.equalsIgnoreCase(summaryType));

            if (!isBuiltInExpense && !isBuiltInIncome && !isCustom) {
                ui.showMessage("Category '" + summaryType + "' not found.");
                return;
            }
        }

        if (list.isEmpty()) {
            ui.showMessage("No transactions found to summarise.");
            return;
        }

        double totalExpense = 0.0;
        double totalIncome = 0.0;
        double categoryTotal = 0.0;
        boolean categoryHasTransactions = false;

        for (int i = 0; i < list.size(); i++) {
            Transaction transaction = list.get(i);

            if (parsedYearMonth != null) {
                YearMonth transactionMonth = YearMonth.from(transaction.getDate());
                if (!transactionMonth.equals(parsedYearMonth)) {
                    continue;
                }
            }

            if (transaction.getType().equals("expense")) {
                totalExpense += transaction.getAmount();
            } else if (transaction.getType().equals("income")) {
                totalIncome += transaction.getAmount();
            }

            if (isCategorySummary &&
                    transaction.getCategory().equalsIgnoreCase(summaryType)) {
                categoryTotal += transaction.getAmount();
                categoryHasTransactions = true;
            }
        }
        displaySummary(ui, totalIncome, totalExpense, categoryTotal,  categoryHasTransactions);
    }

    /**
     * Returns true when summaryType refers to a user-defined/expense category
     * rather than a built-in aggregate keyword.
     */
    private boolean isCategoryType(String type) {
        switch (type) {
        case "all":
        case "month":
        case "outflow":
        case "expense":
        case "income":
        case "inflow":
            return false;
        default:
            return true;
        }
    }

    private void displaySummary(Ui ui, double totalIncome, double totalExpense,
                                double categoryTotal, boolean categoryHasTransactions) {
        if (targetMonth != null) {
            ui.showMessage("=== Summary for " + targetMonth + " ===");
        }
        switch (summaryType) {
        case "all":
            ui.showOverallSummary(totalIncome, totalExpense);
            break;
        case "month":
            ui.showMonthlySummary(totalIncome, totalExpense);
            break;
        case "outflow":
        case "expense":
            ui.showCategorySummary("Expenses", totalExpense);
            break;
        case "income":
        case "inflow":
            ui.showCategorySummary("Income", totalIncome);
            break;
        default:
            if (!categoryHasTransactions) {
                ui.showMessage("No transactions found for category '" + summaryType + "'.");
            } else {
                ui.showCategorySummary("Category '" + summaryType + "'", categoryTotal);
            }
            break;
        }
    }
}
